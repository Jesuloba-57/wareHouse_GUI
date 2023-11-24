import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;

public class QueryState extends WarehouseState{
    private static final int EXIT = 0;
    private static final int VIEW_CLIENTS = 1;
    private static final int N_CLIENTS = 2;
    private static final int P_CLIENTS = 3;
    private static final int HELP = 4;

    private static QueryState queryState;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;

    private QueryState() {
        warehouse = Warehouse.instance();
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

    public static QueryState instance() {
        if (queryState == null) {
            return queryState = new QueryState();
        } else {
            return queryState;
        }
    }
    public void help() {
        System.out.println("\nEnter a number between 0 and 7 as explained below:");
        System.out.println(EXIT + " to Exit ");
        System.out.println(VIEW_CLIENTS + " to view All Clients");
        System.out.println(N_CLIENTS + " to Views Clients with outstanding balances");
        System.out.println(P_CLIENTS + " to Views Clients without outstanding balances");
        System.out.println(HELP + " for help");
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

    public void process() {
        int command;
//        String Name = getToken("Enter Client Name: ");
//        Member member = warehouse.findMemberByName(Name);
//        Iterator wishIter = warehouse.getWishlist(member);
//
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {

                case EXIT:
                    exit();
                    break;
                case VIEW_CLIENTS:
                    viewClients();
                    break;
//                case N_CLIENTS:
//                    removeProduct(member);
//                    break;
//                case P_CLIENTS:
//                    changeQty(member);
//                    break;
                case HELP:
                    help();
                    break;
            }
        }
    }
    public void run() {
        process();
    }
    public void exit(){
        System.out.println("Returning to Clerk Menu");
        (WarehouseContext.instance()).changeState(1);
    }

    public void viewClients(){
        Iterator allMembers = warehouse.getMembers();
        while (allMembers.hasNext()) {
            Member member = (Member) (allMembers.next());
            System.out.println(member.toString());
        }
    }
}
