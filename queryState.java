import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class QueryState extends WarehouseState {
    private static final int EXIT = 0;
    private static final int VIEW_CLIENTS = 1;
    private static final int N_CLIENTS = 2;
    private static final int P_CLIENTS = 3;
    private static final int HELP = 4;

    private static QueryState queryState;
    private JFrame frame;
    private static Warehouse warehouse;

    private QueryState() {
        warehouse = Warehouse.instance();
    }

    public static QueryState instance() {
        if (queryState == null) {
            return queryState = new QueryState();
        } else {
            return queryState;
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Query State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));

        addButton("Exit", e -> exit());
        addButton("View All Clients", e -> viewClients());
        addButton("View Clients with Outstanding Balances", e -> viewClientsWithOutstandingBalances());
        addButton("View Clients without Outstanding Balances", e -> viewClientsWithoutOutstandingBalances());
        addButton("Help", e -> help());

        frame.pack();
        frame.setVisible(true);
        frame.setLocation(400, 400);
    }

    private void addButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        frame.add(button);
    }

    private void exit() {
        JOptionPane.showMessageDialog(frame, "Returning to Clerk Menu");
        (WarehouseContext.instance()).changeState(1);
        frame.dispose(); // Close the Query State window
    }

    private void viewClients() {
        StringBuilder clientInfo = new StringBuilder("All Clients:\n");
        Iterator allMembers = warehouse.getMembers();
        while (allMembers.hasNext()) {
            Member member = (Member) (allMembers.next());
            clientInfo.append(member.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, clientInfo.toString());
    }

    private void viewClientsWithOutstandingBalances() {
        // Implement logic to view clients with outstanding balances
        JOptionPane.showMessageDialog(frame, "View Clients with Outstanding Balances - To be implemented");
    }

    private void viewClientsWithoutOutstandingBalances() {
        // Implement logic to view clients without outstanding balances
        JOptionPane.showMessageDialog(frame, "View Clients without Outstanding Balances - To be implemented");
    }

    private void help() {
        StringBuilder message = new StringBuilder("\nEnter a number between 0 and 4 as explained below:\n");
        message.append(EXIT).append(" to Exit\n");
        message.append(VIEW_CLIENTS).append(" to view All Clients\n");
        message.append(N_CLIENTS).append(" to Views Clients with outstanding balances\n");
        message.append(P_CLIENTS).append(" to Views Clients without outstanding balances\n");
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
                QueryState.instance().run();
            }
        });
    }
}
