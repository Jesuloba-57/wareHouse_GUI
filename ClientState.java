import java.util.*;
import java.text.*;
import java.io.*;
public class ClientState extends WarehouseState {
    private static ClientState clientState;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int VIEW_ACCOUNT = 3;
    private static final int CHECK_PRICE = 5;
    private static final int PROCESS_WISHLIST = 7;
    private static final int MODIFY_WISHLIST = 8;
    private static final int LOGOUT = 10;
    private static final int VIEW_INVOICES = 12;
    private static final int HELP = 13;
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
    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
            return false;
        }
        return true;
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

    public void viewAccount(){
        System.out.println("View Account");

    }
    public void checkPrice(){
        System.out.println("check Price");
    }
    public void processWishlist(){
        System.out.println("process Wishlist");
    }
    public void modifyWishlist(){
        System.out.println("modify Wishlist");
    }
    public void printInvoices() {
        System.out.println("printInvoices");
    }

    public void help() {
        System.out.println("Enter a number between 0 and 12 as explained below:");
        System.out.println(EXIT + " to Exit\n");
        System.out.println(VIEW_ACCOUNT + " to view your account details");
        System.out.println(CHECK_PRICE + " to check price of a product ");
        System.out.println(PROCESS_WISHLIST + " to process your wishlist");
        System.out.println(MODIFY_WISHLIST + " to modify your wishlist");
        System.out.println(VIEW_INVOICES + " to print your invoices");
        System.out.println(LOGOUT + " to logout");
        System.out.println(HELP + " for help");
    }



    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {

                case VIEW_ACCOUNT:       viewAccount();
                    break;
                case CHECK_PRICE:       checkPrice();
                    break;
                case PROCESS_WISHLIST:        processWishlist();
                    break;
                case MODIFY_WISHLIST:       modifyWishlist();
                    break;
                case VIEW_INVOICES:  printInvoices();
                    break;
                case LOGOUT:  logout();
                    break;
                case HELP:              help();
                    break;
            }
        }
        logout();
    }

    public void run() {
        process();
    }

    public void logout()
    {
        if ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk)
        { //stem.out.println(" going to clerk \n ");
            (WarehouseContext.instance()).changeState(1); // exit with a code 1
        }
        else if (WarehouseContext.instance().getLogin() == WarehouseContext.IsClient)
        {  //stem.out.println(" going to login \n");
            (WarehouseContext.instance()).changeState(0); // exit with a code 2
        }
        else
            (WarehouseContext.instance()).changeState(2); // exit code 2, indicates error
    }

}
