import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ManagerState extends WarehouseState {
    private JFrame frame;
    private static Warehouse warehouse;
    private WarehouseContext context;
    private static ManagerState instance;
    private static final int MODIFY_PRODUCT_PRICE = 1;
    private static final int RECEIVE_SHIPMENT = 2;
    private static final int FREEZE_UNFREEZE_CLIENT = 3;
    private static final int BECOME_SALESCLERK = 4;
    private static final int LOGOUT = 5;
    private static final int HELP = 6;

    private ManagerState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static ManagerState instance() {
        if (instance == null) {
            instance = new ManagerState();
        }
        return instance;
    }

    private void createAndShowGUI() {
        if (!login()) {
            // User canceled or entered invalid credentials, terminate
            return;
        }

        frame = new JFrame("Manager State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 1));

        addButton("Add a product", e -> addProduct());
        addButton("Modify Product Price", e -> modifyProductPrice());
        addButton("Receive Shipment", e -> receiveShipment());
        addButton("Freeze/Unfreeze Client", e -> freezeUnfreezeClient());
        addButton("Become Salesclerk", e -> becomeSalesclerk());
        addButton("Logout", e -> logout());
        addButton("Help", e -> help());

        //frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setLocation(400, 400);
        //frame.setBounds(100, 100, 300, 200);
        frame.setVisible(true);
    }

    private boolean login() {
        String username = JOptionPane.showInputDialog(frame, "Enter Manager Username:");
        if (username == null) {
            // User clicked Cancel, terminate
            return false;
        }

        String password = JOptionPane.showInputDialog(frame, "Enter Manager Password:");
        if (password == null) {
            return false;
        }

        if (isValidManagerCredentials(username, password)) {
            return true;
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid manager credentials.");
            return false;
        }
    }

    private boolean isValidManagerCredentials(String username, String password) {
        // Add your logic to check if the entered manager credentials are valid
        // For example, check if the username and password match predefined values
        return username.equals("manager") && password.equals("manager");
    }

    private void addButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        frame.add(button);
    }

    public void modifyProductPrice() {
        JOptionPane.showMessageDialog(frame, "Modify product price");
    }

    public void addProduct(){
        Product result;
        do {
            // Prompt for product name
            String productName = JOptionPane.showInputDialog(null, "Enter Product Name:");

            // Prompt for quantity
            String quantityStr = JOptionPane.showInputDialog(null, "Enter Quantity:");
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.");
                continue;  // Go back to prompting the user
            }

            // Prompt for price
            String priceStr = JOptionPane.showInputDialog(null, "Enter Price:");
            float price;
            try {
                price = Float.parseFloat(priceStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid price. Please enter a valid number.");
                continue;  // Go back to prompting the user
            }

            // Add the product to the warehouse
            result = warehouse.addProduct(productName, quantity, price);

            if (result != null) {
                JOptionPane.showMessageDialog(null, result.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Product could not be added");
            }

            // Ask if the user wants to add more products
            if (JOptionPane.showConfirmDialog(null, "Add more products?", "Add Products", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                break;
            }
        } while (true);
    }

    public int getNumber(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                Integer num = Integer.valueOf(item);
                return num.intValue();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please input a number");
            }
        } while (true);
    }
    public String getToken(String prompt) {
        do {
            try {
                // Show a dialog to prompt the user for input
                String userInput = JOptionPane.showInputDialog(null, prompt);

                // Check if the user clicked Cancel or closed the dialog
                if (userInput == null) {
                    JOptionPane.showMessageDialog(null, "Input canceled. Please enter a valid value.");
                    continue; // Go back to prompting the user
                }

                // Use StringTokenizer to tokenize the user input
                StringTokenizer tokenizer = new StringTokenizer(userInput, "\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid value.");
            }
        } while (true);
    }

    public void receiveShipment() {
        String name = JOptionPane.showInputDialog(null, "Enter Shipment Product Name:");
        Product product = warehouse.findProductByName(name);

        // Ensure that the product is not null before proceeding
        if (product != null) {
            int quantity = getNumber("Enter quantity of Product:");

            if (!product.isEmpty()) {
                Shipment ship = new Shipment(product, quantity);
                JOptionPane.showMessageDialog(null, ship.toString());

                Iterator waitIter = warehouse.getWaitlist(product);

                Object[] options = {"Add Product to Member Invoice", "Remove item from Waitlist", "Change item quantity and add to Member Invoice"};
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Your processing Options:",
                        "Processing Options",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]);

                while (waitIter.hasNext()) {
                    Item wait = (Item) (waitIter.next());
                    Member member = wait.getMember();
                    int addQuantity = wait.getQuantity();
                    float price = wait.getPrice();
                    Record record = new Record(product, member, addQuantity, price);

                    JOptionPane.showMessageDialog(null, wait.toString());

                    switch (choice) {
                        case 0: // Add Product to Member Invoice
                            boolean success = warehouse.allocate(ship, addQuantity);
                            if (success) {
                                warehouse.addToInvoices(record, member);
                            } else {
                                JOptionPane.showMessageDialog(null, "Unexpected Input");
                            }
                            break;

                        case 1: // Remove item from Waitlist
                            if (member != null) {
                                Record record1 = new Record(wait.getProduct(), member, wait.getQuantity(), wait.getPrice());
                                boolean removed = warehouse.removeFromWishlist(member, record1);
                                if (removed) {
                                    JOptionPane.showMessageDialog(null, "Item removed from the wishlist.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Item was not found on the wishlist.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Product or member not found.");
                            }
                            break;

                        case 2: // Change item quantity and add to Member Invoice
                            if (member != null) {
                                int newQuantity = getNumber("Enter the new quantity:");
                                Record record2 = new Record(product, member, newQuantity, wait.getPrice());
                                wait.setQuantity(newQuantity);
                                boolean success2 = warehouse.allocate(ship, newQuantity);
                                if (success2) {
                                    warehouse.addToInvoices(record2, member);
                                    JOptionPane.showMessageDialog(null, "Added to invoice with new quantity");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Unexpected Input");
                                }
                                break;
                            }
                    }
                }
            } else {
                // The product is empty, but not null
                Shipment ship = new Shipment(product, quantity);
                JOptionPane.showMessageDialog(null, "Waitlist is Currently Empty\n" + ship.toString() + " has been added to catalog");
                warehouse.allocate(ship, quantity);
            }
        } else {
            // The product is null
            JOptionPane.showMessageDialog(null, "Waitlist is Currently Empty");
        }
    }

    public void freezeUnfreezeClient() {
        JOptionPane.showMessageDialog(frame, "Freeze/Unfreeze client");
    }

    public void becomeSalesclerk() {
        String userID = JOptionPane.showInputDialog(frame, "Please input the manager password:");
        if (userID.equals("manager")) {
            (WarehouseContext.instance()).setUser(userID);
            (WarehouseContext.instance()).changeState(1);
            frame.dispose(); // Close the Manager State window
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid manager password.");
        }
    }

    public void logout() {
        JOptionPane.showMessageDialog(frame, "Logging out");
        (WarehouseContext.instance()).changeState(0); // exit with a code 0
        frame.dispose(); // Close the Manager State window
    }

    public void help() {
        StringBuilder message = new StringBuilder("Enter a number between 1 and 6 as explained below:\n");
        message.append(MODIFY_PRODUCT_PRICE).append(" to modify product price\n");
        message.append(RECEIVE_SHIPMENT).append(" to receive a shipment\n");
        message.append(FREEZE_UNFREEZE_CLIENT).append(" to freeze/unfreeze client account\n");
        message.append(BECOME_SALESCLERK).append(" to switch to the sales clerk menu\n");
        message.append(LOGOUT).append(" to logout\n");
        message.append(HELP).append(" for help\n");
        JOptionPane.showMessageDialog(frame, message.toString());
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> ManagerState.instance().run());
    }
}
