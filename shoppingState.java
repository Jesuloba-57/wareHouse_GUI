import javax.crypto.spec.RC2ParameterSpec;
import java.util.*;
import java.text.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ShoppingState extends WarehouseState{
    private static ShoppingState shopping;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int VIEW_CART = 4;
    private static final int ADD_PRODUCT = 1;
    private static final int REMOVE_PRODUCT = 2;
    private static final int CHANGE_QTY = 3;
    private static final int CHECKOUT = 6;
    private static final int HELP = 7;
    private JFrame frame;

    private void createAndShowGUI() {
        frame = new JFrame("Shopping State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 1));
        String username = JOptionPane.showInputDialog(frame, "Enter Member name:");
        if (!login(username)) {
            // User canceled or entered invalid credentials, terminate
            return;
        }
        Member member = warehouse.findMemberByName(username);

        addButton("View Cart", e -> viewCart(member));
        addButton("Add Product", e -> addProduct(member));
        addButton("Remove Product", e -> removeProduct(member));
        addButton("Change Quantity", e -> changeQty(member));
        addButton("Checkout", e -> checkout(member));
        addButton("Help", e -> help());

        //frame.pack();
        frame.setSize(400,400);
        frame.setLocation(400, 400);
        frame.setVisible(true);
    }

    private boolean login(String username) {
        Member member = warehouse.findMemberByName(username);
        if (username == null) {
            // User clicked Cancel, terminate
            return false;
        }
        else if (member != null){
            return true;
        }
        else {
            JOptionPane.showMessageDialog(frame, "Invalid manager credentials!");
            return false;
        }
    }

    private void addButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        frame.add(button);
    }

    public static ShoppingState instance() {
        if (shopping == null) {
            return shopping = new ShoppingState();
        } else {
            return shopping;
        }
    }

    private ShoppingState() {
        warehouse = Warehouse.instance();
    }
    public void process() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public void help() {
        System.out.println("\nEnter a number between 0 and 7 as explained below:");
        System.out.println(EXIT + " to Exit ");
        System.out.println(VIEW_CART + " to view your Cart");
        System.out.println(ADD_PRODUCT + " to add a product");
        System.out.println(REMOVE_PRODUCT + " to remove a product");
        System.out.println(CHANGE_QTY + " to the quantity of a product");
        System.out.println(CHECKOUT + " to checkout");
        System.out.println(HELP + " for help");
    }

    public void run() {
        process();
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
                // Handle exceptions (e.g., NumberFormatException) if needed
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid value.");
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

    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
            return false;
        }
        return true;
    }

    public void viewCart(Member member) {
        Iterator allItems = warehouse.getWishlist(member);
        StringBuilder cartContents = new StringBuilder();

        if (member.isEmpty()) {
            // Wishlist is empty
            JOptionPane.showMessageDialog(frame, "Wishlist is Empty");
            return;
        }

        while (allItems.hasNext()) {
            Record record = (Record) allItems.next();
            cartContents.append(record.toString()).append("\n");
        }

        // Display all records in a single JOptionPane
        JOptionPane.showMessageDialog(frame, cartContents.toString(), "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    public void addProduct(Member member) {
        String productName = JOptionPane.showInputDialog(frame, "Enter Product name: ");
        String quant = JOptionPane.showInputDialog(frame, "Enter Quantity: ");
        int quantity = -1;
        try {
            // Parse the user input as an integer
            quantity = Integer.parseInt(quant);

//            JOptionPane.showMessageDialog(null, "You entered: " + enteredInteger);
        } catch (NumberFormatException e) {
            // Handle the case where the user entered a non-integer value
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
        }

            Product product = warehouse.findProductByName(productName);
            //float price = product.getPrice();
            if (product != null && quantity != -1) {
                Record record = new Record(product, member, quantity, product.getPrice());
                boolean added = warehouse.addToWishlist(member, record);

                if (added) {
                    JOptionPane.showMessageDialog(frame, "Item added to the wishlist.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Item could not be added to the wishlist.");
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Product not found.");
            }
    }

    public void removeProduct(Member member){
            String productName = JOptionPane.showInputDialog(frame, "Enter Product name: ");
            Product product = warehouse.findProductByName(productName);
            Iterator wishIter = warehouse.getWishlist(member);
            float price = 0; int qty = 0;

            while (wishIter.hasNext()) {
                Record record = (Record)wishIter.next();
                if (record.getProduct().equals(product)) {
                    qty = record.getQuantity();
                    price = record.getPrice();
                }
            }
            Record target = new Record(product, member, qty, price);
            boolean removed = warehouse.removeFromWishlist(member, target);
            if (removed) {
                JOptionPane.showMessageDialog(frame, "Item removed from the wishlist.");
            } else {
                JOptionPane.showMessageDialog(frame, "Item was not found on the wishlist.");
            }
    }

    public void changeQty(Member member) {
        String productName = JOptionPane.showInputDialog(frame, "Enter Product name: ");
        Product product = warehouse.findProductByName(productName);
        Iterator wishIter = warehouse.getWishlist(member);
        String quant = JOptionPane.showInputDialog(frame, "Enter Quantity: ");
        int newQuantity = -1;
        boolean check = false;
        try {
            // Parse the user input as an integer
            newQuantity = Integer.parseInt(quant);

            //            JOptionPane.showMessageDialog(null, "You entered: " + enteredInteger);
        } catch (NumberFormatException e) {
            // Handle the case where the user entered a non-integer value
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
            check = false;
        }

        while (wishIter.hasNext()) {
            Record record = (Record) wishIter.next();
            if (record.getProduct().equals(product) && newQuantity != -1) {
                record.setQuantity(newQuantity);
                check = true;
            }
        }
        if (check == false) {
            System.out.println("Inputed Product not Found on Wishlist.");
        }
        else{
            JOptionPane.showMessageDialog(frame, "Change successfully made.");
        }

//            System.out.println("New Quantity for " + product + " is " + wish.getQuantity());
    }

    public void showInvoices(Member member) {
        Iterator invoices = warehouse.getInvoices(member);
        StringBuilder invoiceContents = new StringBuilder();

        while (invoices.hasNext()) {
            Record invoice = (Record) invoices.next();
            invoiceContents.append(invoice.toString()).append("\n");
        }

        // Check if there are any invoices
        if (invoiceContents.length() > 0) {
            // Display all invoices in a single JOptionPane
            JOptionPane.showMessageDialog(frame, invoiceContents.toString(), "INVOICE", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // No invoices to display
            JOptionPane.showMessageDialog(frame, "No Invoices found for " + member.getName(), "INVOICE", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void endProcess(){
        JOptionPane.showMessageDialog(frame, "You have Successfully Checked out!");
        (WarehouseContext.instance()).changeState(2);
    }
    public void checkout(Member member) {
        Iterator wishIter = warehouse.getWishlist(member);
        boolean check = yesOrNo("Confirm Checkout:");
        if (check) {
            while (wishIter.hasNext()) {
                Record record = (Record) wishIter.next();
                Product p = record.getProduct();
                int newQuantity = p.getQuantity() - record.getQuantity();
                if (newQuantity < 0){
                    int qauntity = record.getQuantity() - p.getQuantity();
                    Item item = new Item(p, member, qauntity, p.getPrice());
                    p.addWait(item);
                    record.setQuantity(p.getQuantity());
                    newQuantity = 0;
                }
                warehouse.changeProductQuantity(p.getProductName(), newQuantity);
                member.addtoInvoice(record);
                //member.removeWish(record);
            }
            member.clearWishes();
            showInvoices(member);
            warehouse.save();
            endProcess();
            frame.dispose();
        }
        else{
            help();
        }
    }
    public int getCommand() {
        do {
            try {
                int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
                if (value >= EXIT && value <= HELP) {
                    return value;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Enter a number");
            }
        } while (true);
    }

}
