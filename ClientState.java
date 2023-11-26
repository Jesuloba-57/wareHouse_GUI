import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class ClientState extends WarehouseState {
    private static ClientState clientState;
    private JFrame frame;
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int VIEW_ACCOUNT = 1;
    private static final int CHECK_PRICE = 2;
    private static final int PROCESS_WISHLIST = 5;
    private static final int SHOW_PAYMENT = 4;
    private static final int DISPLAY_WAITLIST = 8;
    private static final int LOGOUT = 7;
    private static final int VIEW_INVOICES = 3;
    private static final int HELP = 8;

    private ClientState() {
        warehouse = Warehouse.instance();
    }

    public static ClientState instance() {
        if (clientState == null) {
            return clientState = new ClientState();
        } else {
            return clientState;
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Client State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Prompt for a valid member name
        String memberName = promptForValidMemberName();
        if (memberName == null) {
            // User canceled or entered an invalid member name, so terminate
            (WarehouseContext.instance()).changeState(3);
            return;
        }

        frame.setLayout(new GridLayout(7, 1));
        addButton("View Account", e -> viewAccount(memberName));
        addButton("Check Price", e -> checkPrice());
        addButton("Process Wishlist", e -> processShopping());
        addButton("Show Payments", e -> showPayments());
        addButton("Print Invoices", e -> printInvoices(memberName));
        addButton("Logout", e -> logout());
        addButton("Help", e -> help());
//        addButton("Exit", e -> exit());

        //frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setLocation(400, 400);
        //frame.setBounds(100, 100, 300, 200);
        frame.setVisible(true);
    }

    private String promptForValidMemberName() {
        String memberName;
        do {
            memberName = JOptionPane.showInputDialog(frame, "Enter Member Name:");
            if (memberName == null) {
                // User clicked Cancel, terminate
                JOptionPane.showMessageDialog(frame, "Member Not Found! Try again");
                return null;
            }
        } while (!isValidMemberName(memberName));

        return memberName;
    }

    private boolean isValidMemberName(String memberName) {
        // Add your logic to check if the entered member name is valid
        // For example, check if the member with the given name exists in the warehouse
        return warehouse.findMemberByName(memberName) != null;
    }

    private void addButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        frame.add(button);
    }

    private void viewAccount(String memberName) {
        Member member = warehouse.findMemberByName(memberName);
        JOptionPane.showMessageDialog(frame, member.toString());
    }

    private void checkPrice() {
        StringBuilder prices = new StringBuilder("Product Prices:\n");
        Iterator allProducts = warehouse.getProducts();
        while (allProducts.hasNext()) {
            Product product = (Product) (allProducts.next());
            prices.append(product.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, prices.toString());
    }

    private void processShopping() {
        JOptionPane.showMessageDialog(frame, "Switching to Shopping Processing state");
        (WarehouseContext.instance()).changeState(4);
        frame.dispose(); // Close the Client State window
    }

    private void showPayments() {
        JOptionPane.showMessageDialog(frame, "Dummy Function");
    }

    private void printInvoices(String memberName) {
        Member member = warehouse.findMemberByName(memberName);
        StringBuilder invoices = new StringBuilder("Invoices:\n");
        Iterator invoiceIterator = warehouse.getInvoices(member);
        while (invoiceIterator.hasNext()) {
            Record invoice = (Record) invoiceIterator.next();
            invoices.append(invoice.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, invoices.toString());
    }

    private void logout() {
        if ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk) {
            (WarehouseContext.instance()).changeState(1);
            frame.dispose(); // Close the Clerk State window
        }
        else{
            JOptionPane.showMessageDialog(frame, "Logging out");
            (WarehouseContext.instance()).changeState(2); // exit code 2, indicates client logout
            frame.dispose(); // Close the Client State window
        }
    }

    private void help() {
        StringBuilder message = new StringBuilder("\nEnter a number between 1 and 8 as explained below:\n");
        message.append(VIEW_ACCOUNT).append(" to view your account details\n");
        message.append(CHECK_PRICE).append(" to display products and their prices\n");
        message.append(VIEW_INVOICES).append(" to print your invoices\n");
        message.append(SHOW_PAYMENT).append(" to show payments\n");
        message.append(PROCESS_WISHLIST).append(" to process your wishlist\n");
        message.append(LOGOUT).append(" to logout\n");
        message.append(HELP).append(" for help\n");
        JOptionPane.showMessageDialog(frame, message.toString());
    }

    private void exit() {
        frame.dispose();
    }

    @Override
    public void run() {
        // You may need to implement the logic for the run method based on your application flow
        // This could include prompting for member name, checking member existence, etc.
        // For this example, we'll use a placeholder name "SampleMember"
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> ClientState.instance().run());
    }
}
