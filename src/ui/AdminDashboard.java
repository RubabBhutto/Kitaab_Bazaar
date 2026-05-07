//Rubab

package ui;
import db.DBConnection;

import model.User;
import service.UserService;
import service.OrderService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    User myUser;

    public AdminDashboard(User user) {
        this.myUser = user;

        setTitle("KitaabBazaar - Admin");
        setSize(920, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(LoginScreen.GRAY);

        add(topBar(), BorderLayout.NORTH);
        add(tabs(),   BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(50,50,50));
        p.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JLabel lbl = new JLabel("Admin Panel — " + myUser.getName());
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        JButton out = new JButton("Logout");
        out.addActionListener(e -> { dispose(); new LoginScreen(); });

        p.add(lbl, BorderLayout.WEST);
        p.add(out, BorderLayout.EAST);
        return p;
    }

    private JTabbedPane tabs() {
        JTabbedPane t = new JTabbedPane();
        t.setFont(new Font("Arial", Font.PLAIN, 13));

        t.addTab("Users",      usersTab());
        t.addTab("Books",      tableTab("SELECT b.id, b.title, b.grade, b.price, b.condition_, b.status, u.name AS seller FROM books b JOIN users u ON b.seller_id=u.id"));
        t.addTab("Orders",     tableTab("SELECT o.id, u.name AS buyer, b.title AS book, o.status FROM orders o JOIN users u ON o.buyer_id=u.id JOIN books b ON o.book_id=b.id"));
        t.addTab("Payments",   tableTab("SELECT * FROM payments"));
        t.addTab("Statistics", statsTab());

        return t;
    }

    private JPanel usersTab() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBackground(LoginScreen.GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ArrayList<User> users = UserService.getAllUsers();

        String[] cols = {"ID","Name","Phone","Role"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (User u : users) { 
            model.addRow(new Object[]{
                u.getId(), u.getName(), u.getPhone(), u.getRole()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JButton deleteBtn = LoginScreen.makeButton("Delete Selected User", new Color(200,50,50));
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Select a user first."); return; }

            int uid = (int) table.getModel().getValueAt(row, 0);
            int choice = JOptionPane.showConfirmDialog(this,
                "Delete user ID " + uid + "?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                boolean ok = UserService.deleteUser(uid);
                if (ok) ((DefaultTableModel) table.getModel()).removeRow(row);
                else LoginScreen.msg("Cannot delete — user may have active data.");
            }
        });

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(deleteBtn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel tableTab(String sql) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBackground(LoginScreen.GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(sql);

            int colCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                model.addColumn(rs.getMetaData().getColumnLabel(i));
            }
            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 1; i <= colCount; i++) row[i-1] = rs.getObject(i);
                model.addRow(row);
            }
            conn.close();

        } catch (Exception e) {
            System.out.println("Admin table error: " + e.getMessage());
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel statsTab() {
        JPanel p = new JPanel(new GridLayout(2, 4, 15, 15));
        p.setBackground(LoginScreen.GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        p.add(card("Users",      String.valueOf(OrderService.getCount("users"))));
        p.add(card("Books",      String.valueOf(OrderService.getCount("books"))));
        p.add(card("Orders",     String.valueOf(OrderService.getCount("orders"))));
        p.add(card("Payments",   String.valueOf(OrderService.getCount("payments"))));

        return p;
    }

    private JPanel card(String title, String value) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(LoginScreen.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.PLAIN, 12));
        t.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.BOLD, 30));
        v.setForeground(LoginScreen.BLUE);
        c.add(t, BorderLayout.NORTH);
        c.add(v, BorderLayout.CENTER);
        return c;
    }
}