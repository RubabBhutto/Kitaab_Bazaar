

package ui;
import db.DBConnection;
import exception.InvalidUserInputException;
import java.awt.*;
import javax.swing.*;
import model.User;
import service.UserService;

public class LoginScreen extends JFrame {

   
    public static Color BLUE  = new Color(26, 115, 232);
    public static Color WHITE = Color.WHITE;
    public static Color GRAY  = new Color(245, 245, 245);

    public LoginScreen() {
        setTitle("KitaabBazaar — Used Book Marketplace");
        setSize(400, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DBConnection.closeConnection();
                System.exit(0);
            }
        });

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(GRAY);
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("KitaabBazaar", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(BLUE);

        JLabel sub = new JLabel("Buy & Sell Used Books", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(GRAY);
        header.add(title);
        header.add(sub);
        header.add(Box.createVerticalStrut(15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login",    loginTab());
        tabs.addTab("Register", registerTab());

        main.add(header, BorderLayout.NORTH);
        main.add(tabs,   BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // LOGIN TAB
    private JPanel loginTab() {
        JPanel p = new JPanel(new GridLayout(5, 1, 5, 10));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField     phoneF = new JTextField();
        JPasswordField passF  = new JPasswordField();
        JButton        loginB = makeButton("Login", BLUE);

        p.add(new JLabel("Phone (11 digits):")); p.add(phoneF);
        p.add(new JLabel("Password:"));          p.add(passF);
        p.add(loginB);

        loginB.addActionListener(e -> {
            String phone = phoneF.getText().trim();
            String pass  = new String(passF.getPassword());

            if (phone.isEmpty() || pass.isEmpty()) {
                msg("Please fill in all fields!"); return;
            }

            if (!phone.matches("\\d{11}")) {
                msg("Phone number must be exactly 11 digits.\nExample: 03001234567");
                return;
            }

            User user = UserService.login(phone, pass);

            if (user == null) {
                msg("Wrong phone or password.");
            } else {
                dispose();
                String role = user.getRole();
                if      (role.equals("admin"))  new AdminDashboard(user);
                else if (role.equals("seller")) new SellerDashboard(user);
                else                            new BuyerDashboard(user);
            }
        });

        return p;
    }

    // REGISTER TAB
    private JPanel registerTab() {
        JPanel p = new JPanel(new GridLayout(9, 1, 5, 8));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JTextField     nameF  = new JTextField();
        JTextField     phoneF = new JTextField();
        JPasswordField passF  = new JPasswordField();
        JComboBox<String> roleB = new JComboBox<>(new String[]{"buyer","seller"});
        JButton        regB   = makeButton("Create Account", BLUE);

        p.add(new JLabel("Name:"));              p.add(nameF);
        p.add(new JLabel("Phone (11 digits):")); p.add(phoneF);
        p.add(new JLabel("Password (min 6):")); p.add(passF);
        p.add(new JLabel("I am a:"));            p.add(roleB);
        p.add(regB);

        regB.addActionListener(e -> {
            String name  = nameF.getText().trim();
            String phone = phoneF.getText().trim();
            String pass  = new String(passF.getPassword());
            String role  = (String) roleB.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                msg("Please fill all fields!"); return;
            }

            try {
                boolean ok = UserService.register(name, phone, pass, role);
                if (ok) {
                    msg("Account created! Please login.");
                    nameF.setText(""); phoneF.setText(""); passF.setText("");
                } else {
                    msg("Phone already registered! Try a different number.");
                }
            } catch (InvalidUserInputException ex) {
                msg("Invalid " + ex.getFieldName() + ": " + ex.getMessage());
            }
        });

        return p;
    }

    public static void msg(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    public static JButton makeButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
