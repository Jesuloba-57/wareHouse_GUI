import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class ClerkState extends WarehouseState {
    private static final int EXIT = 0;
    private static final int PRINT_CATALOG = 2;
    private static final int QUERY = 3;
    private static final int ACCEPT_PAY = 4;

    private static final int ADD_MEMBER = 1;
    //    private static final int ADD_PRODUCT= 3;
    private static final int SAVE_DATABASE = 6;
    private static final int LOGOUT = 7;
    private static final int CLIENT_MENU = 5;
    private static final int HELP = 8;
    private static Warehouse warehouse;
    private WarehouseContext context;
    private static ClerkState instance;
    private JFrame frame;

    private ClerkState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static ClerkState instance() {
        if (instance == null) {
            instance = new ClerkState();
        }
        return instance;
    }

    private void createAndShowGUI() {
        frame = new JFrame("Clerk State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2));

        addButton("Add Member", e -> addMember());
        addButton("Print Catalog", e -> printCatalog());
        addButton("Query Client", e -> queryClient());
        addButton("Accept Payment", e -> acceptPay());
        addButton("Become Client", e -> clientMenu());
        addButton("Save Database", e -> saveDatabase());
        addButton("Display Product Waitlist", e -> displayWaitlist());
        addButton("Logout", e -> logout());
        addButton("Help", e -> help());

        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,400);
        frame.setLocation(400, 400);
    }

    private void addButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        frame.add(button);
    }

    private void addMember() {
        String name = JOptionPane.showInputDialog(frame, "Enter the member's name:");
        String address = JOptionPane.showInputDialog(frame, "Enter the member's address:");
        String phone = JOptionPane.showInputDialog(frame, "Enter the member's phone number");

        Member newMember = new Member(name, address, phone);
        if (MemberList.instance().insertMember(newMember)) {
            JOptionPane.showMessageDialog(frame, "Member added successfully.");
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to add the member. Member may already exist.");
        }
    }

    private void printCatalog() {
        StringBuilder catalogInfo = new StringBuilder("Product Catalog:\n");
        Iterator<Product> productIterator = Catalog.instance().getProducts();
        while (productIterator.hasNext()) {
            Product product = productIterator.next();
            catalogInfo.append("Product ID: ").append(product.getId()).append("\n");
            catalogInfo.append("Name: ").append(product.getProductName()).append("\n");
            catalogInfo.append("Price: $").append(product.getPrice()).append("\n");
            catalogInfo.append("Quantity: ").append(product.getQuantity()).append("\n");
            catalogInfo.append("----------------------------\n");
        }
        JOptionPane.showMessageDialog(frame, catalogInfo.toString());
    }

    private void saveDatabase() {
        try {
            // Serialize MemberList
            FileOutputStream memberListFile = new FileOutputStream("MemberList.ser");
            ObjectOutputStream memberListOut = new ObjectOutputStream(memberListFile);
            memberListOut.writeObject(MemberList.instance());
            memberListOut.close();
            memberListFile.close();

            // Serialize Catalog
            FileOutputStream catalogFile = new FileOutputStream("Catalog.ser");
            ObjectOutputStream catalogOut = new ObjectOutputStream(catalogFile);
            catalogOut.writeObject(Catalog.instance());
            catalogOut.close();
            catalogFile.close();

            JOptionPane.showMessageDialog(frame, "Database saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving the database: " + e.getMessage());
        }
    }

    private void queryClient() {
        (WarehouseContext.instance()).changeState(5);
        frame.dispose(); // Close the Clerk State window
    }

    private void acceptPay() {
        JOptionPane.showMessageDialog(frame, "Dummy Function");
    }

    private void clientMenu() {
//        String userID = JOptionPane.showInputDialog(frame, "Please input the user id: ");
//        if (Warehouse.instance().searchMembership(userID) != null) {
//            (WarehouseContext.instance()).setUser(userID);
            (WarehouseContext.instance()).changeState(2);
            frame.dispose(); // Close the Clerk State window
//        } else {
//            JOptionPane.showMessageDialog(frame, "Invalid user id.");
//        }
    }

    private void logout() {
        if ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk) {
            (WarehouseContext.instance()).changeState(3);
            frame.dispose(); // Close the Clerk State window
        }
        else {
            (WarehouseContext.instance()).changeState(0);
            frame.dispose(); // Close the Clerk State window
        }
    }
    private void displayWaitlist(){
        String name = JOptionPane.showInputDialog(null, "Enter Product Name:");
        Product product = warehouse.findProductByName(name);

        // Ensure that the product is not null before proceeding
        if (product != null) {
            if (product.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Product does not have a waitlist");
            } else {
                Iterator allItems = warehouse.getWaitlist(product);
                StringBuilder waitlistMessage = new StringBuilder("Waitlist for " + product.getProductName() + ":\n");

                while (allItems.hasNext()) {
                    Item item = (Item) (allItems.next());
                    waitlistMessage.append(item.toString()).append("\n");
                }

                JOptionPane.showMessageDialog(null, waitlistMessage.toString());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Product entered does not exist in catalog");
        }
    }

    private void help() {
        StringBuilder message = new StringBuilder("Enter a number between 1 and 8 as explained below:\n");
        message.append(EXIT).append(" to Exit\n");
        message.append(ADD_MEMBER).append(" to add a member\n");
        message.append(PRINT_CATALOG).append(" to display products in catalog\n");
        message.append(QUERY).append(" for Client Viewing Options\n");
        message.append(ACCEPT_PAY).append(" to accept Payment from a client\n");
        message.append(CLIENT_MENU).append(" to become a client\n");
        message.append(SAVE_DATABASE).append(" to save the database\n");
        message.append(LOGOUT).append(" to Logout\n");
        message.append(HELP).append(" for help\n");
        JOptionPane.showMessageDialog(frame, message.toString());
    }

    public void run() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClerkState.instance().run();
            }
        });
    }
}
