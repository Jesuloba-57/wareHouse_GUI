import java.util.*;
import java.text.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginState extends WarehouseState implements ActionListener {
    private JFrame frame;
    private AbstractButton clientButton, exitButton, clerkButton, managerButton;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private WarehouseContext context;
    private static LoginState instance;

    private LoginState() {
        super();
//        if (frame == null) {
//            frame = new JFrame("Login");
//            frame.setBounds(100, 100, 300, 200);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setLocationRelativeTo(null);
//        }
    }

    public static LoginState instance() {
        if (instance == null) {
            instance = new LoginState();
        }
        return instance;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(this.clientButton)) {
            this.user();
        } else if (event.getSource().equals(this.managerButton)) {
            this.manager();
//        } else if (event.getSource().equals(this.clerkButton)) {
//            this.clerk();
        } else if (event.getSource().equals(this.exitButton)) {
            (WarehouseContext.instance()).changeState(3);
        }
    }

    public void clear() {
        frame.getContentPane().removeAll();
        frame.paint(frame.getGraphics());
    }

    private void manager() {
        (WarehouseContext.instance()).setLogin(WarehouseContext.IsManager);
        (WarehouseContext.instance()).changeState(0);
    }

    private void user() {
        (WarehouseContext.instance()).setLogin(WarehouseContext.IsClient);
        (WarehouseContext.instance()).changeState(2);
    }

    public void process() {
        frame = new JFrame("Login");
        frame.setBounds(100, 100, 300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //frame.setLocationRelativeTo(null);

        clientButton = new JButton("Client");
        clerkButton = new JButton("Clerk");
        managerButton = new JButton("Manager");
        exitButton = new JButton("Exit");

        clientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                user();
                frame.dispose();
            }
        });

        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager();
                frame.dispose();
            }
        });

        clerkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ClerkButton().actionPerformed(e);
                frame.dispose();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                (WarehouseContext.instance()).changeState(3);
                frame.dispose();
            }
        });

        frame.setLayout(new GridLayout(4, 1));
        frame.setSize(400,400);
        frame.setLocation(400, 400);
        frame.add(clientButton);
        frame.add(clerkButton);
        frame.add(managerButton);
        frame.add(exitButton);
        frame.setVisible(true);

    }

    public void run() {
        process();
    }
}
