import javax.crypto.spec.RC2ParameterSpec;
import java.util.*;
import java.text.*;
import java.io.*;

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
        int command;
        String Name = getToken("Enter Client Name: ");
        Member member = warehouse.findMemberByName(Name);
        Iterator wishIter = warehouse.getWishlist(member);

        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_PRODUCT:       addProduct(member);
                    break;
                case REMOVE_PRODUCT:        removeProduct(member);
                    break;
                case CHANGE_QTY:       changeQty(member);
                    break;
                case VIEW_CART:       viewCart(member);
                    break;
                case CHECKOUT:  checkout(member);
                    break;
                case HELP:              help();
                    break;
            }
        }
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
                System.out.println(prompt);
                String line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line,"\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            } catch (IOException ioe) {
                System.exit(0);
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
                System.out.println("Please input a number ");
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
        if (member.isEmpty()){
            System.out.println("WishList is Empty");
        }
        while (allItems.hasNext()) {
            Record record = (Record) allItems.next();
            System.out.println(record.toString());
        }
    }

    public void addProduct(Member member){
        String productName = getToken("Enter Product Name");
        int quantity = getNumber("Enter Quantity");

        Product product = warehouse.findProductByName(productName);
        //float price = product.getPrice();
        if (product != null) {
            Record record = new Record(product, member, quantity, product.getPrice());
            boolean added = warehouse.addToWishlist(member, record);

            if (added) {
                System.out.println("Item added to the wishlist.");
            } else {
                System.out.println("Item could not be added to the wishlist.");
            }
        } else {
            System.out.println("Product not found.");
        }
    }

    public void removeProduct(Member member){
            String productName = getToken("Enter Product Name");
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
                System.out.println("Item removed from the wishlist.");
            } else {
                System.out.println("Item was not found on the wishlist.");
            }
    }

    public void changeQty(Member member){
            String productName = getToken("Enter Product Name");
            Product product = warehouse.findProductByName(productName);
            Iterator wishIter = warehouse.getWishlist(member);
            int newQuantity = getNumber("Enter the new quantity:");
            boolean check = false;

            while (wishIter.hasNext()) {
                Record record = (Record)wishIter.next();
                if (record.getProduct().equals(product)) {
                    record.setQuantity(newQuantity);
                    check = true;
                }
            }
            if (check = false) {
                System.out.println("Inputed Product not Found on Wishlist ");
            }

//            System.out.println("New Quantity for " + product + " is " + wish.getQuantity());
    }

    public void showInvoices(Member member) {
        System.out.println("Here is your Invoice:");
        Iterator invoices = warehouse.getInvoices(member);
        while (invoices.hasNext()) {
            Record invoice = (Record) invoices.next();
            System.out.println(invoice.toString());
        }
    }

    public void endProcess(){
        System.out.println("You have Successfully Checked out!");
        (WarehouseContext.instance()).changeState(2);
    }

    public void checkout(Member member) {
        Iterator wishIter = warehouse.getWishlist(member);
        boolean check = yesOrNo("Confirm Checkout:");
        if (check) {
            while (wishIter.hasNext()) {
                Record record = (Record) wishIter.next();
                member.addtoInvoice(record);
            }
            showInvoices(member);
            warehouse.save();
            endProcess();
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
