package chess.user;

import javax.swing.*;

public class LoginDialog {

    public static Player showLogin() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField usernameField = new JTextField(12);
        JPasswordField passwordField = new JPasswordField(12);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        String[] options = {"Login", "Create Account", "Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null, panel, "Chess Login",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]
            );

            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (choice == 0) { // LOGIN
                if (PlayerDatabase.validate(user, pass)) {
                    return PlayerDatabase.get(user);
                }
                JOptionPane.showMessageDialog(null, "Invalid login");
            }

            if (choice == 1) { // CREATE ACCOUNT
                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter username and password");
                    continue;
                }

                if (PlayerDatabase.get(user) != null) {
                    JOptionPane.showMessageDialog(null, "User already exists");
                    continue;
                }

                return PlayerDatabase.create(user, pass);
            }
        }
    }
}
