import java.util.*;
import java.text.*;
import java.io.*;
public class ClerkState extends WarehouseState {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private WarehouseContext context;
    private static ClerkState instance;
    private static final int EXIT = 0;
    private static final int PRINT_CATALOG = 1;

    private static final int ADD_MEMBER = 2;
    private static final int ADD_PRODUCT= 3;
    private static final int SAVE_DATABASE = 4;
    private static final int LOGOUT = 5;
    private static final int CLIENT_MENU = 11;
    private static final int HELP = 13;
    private ClerkState() {
        super();
        warehouse = Warehouse.instance();
        //context = LibContext.instance();
    }

    public static ClerkState instance() {
        if (instance == null) {
            instance = new ClerkState();
        }
        return instance;
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
    public Calendar getDate(String prompt) {
        do {
            try {
                Calendar date = new GregorianCalendar();
                String item = getToken(prompt);
                DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
                date.setTime(df.parse(item));
                return date;
            } catch (Exception fe) {
                System.out.println("Please input a date as mm/dd/yy");
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

    public void help() {
        System.out.println("Enter a number between 0 and 12 as explained below:");
        System.out.println(EXIT + " to Exit\n");
        System.out.println(ADD_MEMBER + " to add a member");
        System.out.println(ADD_PRODUCT + " to  add products to catalog");
        System.out.println(PRINT_CATALOG + " to display products in catalog ");
        System.out.println(SAVE_DATABASE + " to save the database");
        System.out.println(LOGOUT + " to Logout");
        System.out.println(CLIENT_MENU+ " to become a client");
        System.out.println(HELP + " for help");
    }

    public void addMember() {
        System.out.println("Add member");
    }

    public void addProduct() {
        System.out.println("Product");

    }

    public void printCatalog() {
        System.out.println("Catalog");
    }
    public void SaveDatabase(){
        System.out.println("SaveDatabase");
    }




    public void clientMenu()
    {
        String userID = getToken("Please input the user id: ");
        if (Warehouse.instance().searchMembership(userID) != null){
            (WarehouseContext.instance()).setUser(userID);
            (WarehouseContext.instance()).changeState(2);
        }
        else
            System.out.println("Invalid user id.");
    }

    public void logout()
    {
        (WarehouseContext.instance()).changeState(3); // exit with a code 0
    }


    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_MEMBER:        addMember();
                    break;
                case ADD_PRODUCT:         addProduct();
                    break;
                case PRINT_CATALOG:      printCatalog();
                    break;
                case SAVE_DATABASE:      SaveDatabase();
                    break;
                case LOGOUT:      logout();
                    break;
                case CLIENT_MENU:          clientMenu();
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
}
