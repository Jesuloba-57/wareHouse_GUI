import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClerkButton extends JButton implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public ClerkButton() {
        super("Clerk");
        setListener();
    }

    public void setListener() {
        addActionListener(this);
    }

    private boolean verifyPassword(String user, String pass, String role) {
        if ("clerk".equals(role)) {
            return "salesclerk".equals(user) && "salesclerk".equals(pass);
        } else {
            return false;
        }
    }

    private void clerk() {
        // Create a JFrame for the login GUI
        JFrame loginFrame = new JFrame("Clerk Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //loginFrame.setSize(300, 150);
        loginFrame.setBounds(100, 100, 300, 200);
        loginFrame.setLayout(new GridLayout(3, 2));
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLocation(400, 400);

        // Components for the login GUI
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton submitButton = new JButton("Submit");

        // Add components to the login GUI
        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(new JLabel()); // Empty label for spacing
        loginFrame.add(submitButton);

        // Action listener for the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                char[] enteredPasswordChars = passwordField.getPassword();
                String enteredPassword = new String(enteredPasswordChars);

                if (verifyPassword(enteredUsername, enteredPassword, "clerk")) {
                    WarehouseContext.instance().setLogin(WarehouseContext.IsClerk);
                    WarehouseContext.instance().changeState(1);
                    loginFrame.dispose(); // Close the login GUI
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password");
                }
            }
        });

        // Set the login GUI visible
        loginFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        WarehouseContext.instance().setLogin(WarehouseContext.IsClerk);
        //LoginState.instance().clear();
        clerk();
    }
}
