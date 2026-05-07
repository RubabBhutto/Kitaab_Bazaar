//Nighat
package ui;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import model.Book;
import model.User;
import service.BookService;
import service.OrderService;

public class BuyerDashboard extends JFrame {

    User   myUser;
    JTable booksTable;

    public BuyerDashboard(User user) {
        this.myUser = user;

        setTitle("KitaabBazaar - Buyer");
        setSize(920, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(LoginScreen.GRAY);

        JPanel top = new JPanel(new BorderLayout());
        top.add(topBar(),    BorderLayout.NORTH);
        top.add(searchBar(), BorderLayout.SOUTH);

        add(top,           BorderLayout.NORTH);
        add(centerTable(), BorderLayout.CENTER);
        add(buttons(),     BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(LoginScreen.BLUE);
        p.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JLabel lbl = new JLabel("Welcome " + myUser.getName() + "  |  Available Books");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        JButton out = new JButton("Logout");
        out.addActionListener(e -> { dispose(); new LoginScreen(); });

        p.add(lbl, BorderLayout.WEST);
        p.add(out, BorderLayout.EAST);
        return p;
    }

    private JPanel searchBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JTextField searchF = new JTextField(20);
        searchF.setPreferredSize(new Dimension(200, 30));

        String[] grades = {"All Grades","1","2","3","4","5","6","7","8","9","10","11","12"};
        JComboBox<String> gradeBox = new JComboBox<>(grades);
        gradeBox.setPreferredSize(new Dimension(110, 30));

        JButton searchBtn = LoginScreen.makeButton("Search", LoginScreen.BLUE);
        JButton clearBtn  = new JButton("Clear");

        p.add(new JLabel("Search:")); p.add(searchF);
        p.add(new JLabel("Grade:"));  p.add(gradeBox);
        p.add(searchBtn); p.add(clearBtn);

        searchBtn.addActionListener(e -> {
            String keyword = searchF.getText().trim();
            int    grade   = gradeBox.getSelectedIndex();
            ArrayList<Book> books = BookService.searchBooks(keyword, grade);
            booksTable.setModel(toTableModel(books));
            hideIdColumn();
        });

        clearBtn.addActionListener(e -> {
            searchF.setText("");
            gradeBox.setSelectedIndex(0);
            loadAllBooks();
        });

        return p;
    }

    private JScrollPane centerTable() {
        booksTable = new JTable();
        loadAllBooks();
        booksTable.setRowHeight(26);
        booksTable.setFont(new Font("Arial", Font.PLAIN, 13));
        booksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(booksTable);
    }

    private void loadAllBooks() {
        ArrayList<Book> books = BookService.getAllBooks();
        booksTable.setModel(toTableModel(books));
        hideIdColumn();
    }

    private DefaultTableModel toTableModel(ArrayList<Book> books) {
        String[] cols = {"ID","Title","Grade","Publisher","Category","Condition","Price","Seller"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Book b : books) {
            model.addRow(new Object[]{
                b.getId(),
                b.getTitle(),
                b.getGrade() == 0 ? "Novel/General" : "Grade " + b.getGrade(),
                b.getPublisher(),
                b.getCategoryName(),
                b.getCondition(),
                "Rs. " + b.getPrice(),
                b.getSellerName()
            });
        }
        return model;
    }

    private void hideIdColumn() {
        booksTable.getColumnModel().getColumn(0).setMinWidth(0);
        booksTable.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    private JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(LoginScreen.GRAY);

        JButton orderBtn   = LoginScreen.makeButton("Place Order",    new Color(40,167,69));
        JButton msgBtn     = LoginScreen.makeButton("Message Seller", new Color(255,153,0));
        JButton inboxBtn   = LoginScreen.makeButton("My Inbox",       new Color(0,153,204));
        JButton reviewBtn  = LoginScreen.makeButton("Write Review",   new Color(108,99,255));
        JButton refreshBtn = new JButton("Refresh");

        p.add(orderBtn);
        p.add(msgBtn);
        p.add(inboxBtn);
        p.add(reviewBtn);
        p.add(refreshBtn);

        orderBtn.addActionListener(e -> {
            int row = booksTable.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Please select a book first!"); return; }

            int    bookId    = (int)    booksTable.getModel().getValueAt(row, 0);
            String bookTitle = (String) booksTable.getModel().getValueAt(row, 1);
            String price     = (String) booksTable.getModel().getValueAt(row, 6);
            String seller    = (String) booksTable.getModel().getValueAt(row, 7);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Book  : " + bookTitle + "\n" +
                "Price : " + price     + "\n" +
                "Seller: " + seller    + "\n\n" +
                "Place order? The seller will confirm the sale.",
                "Confirm Order", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = OrderService.placeOrder(myUser.getId(), bookId);
            if (ok) {
                LoginScreen.msg("Order placed successfully!\nWaiting for the seller to confirm your order.");
                loadAllBooks();
            } else {
                LoginScreen.msg("Error placing order. Please try again.");
            }
        });

        msgBtn.addActionListener(e -> {
            int row = booksTable.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Please select a book first!"); return; }

            String sellerName = (String) booksTable.getModel().getValueAt(row, 7);
            String text = JOptionPane.showInputDialog(this,
                "Type your message to " + sellerName + ":");
            if (text == null || text.trim().isEmpty()) return;

            int bookId   = (int) booksTable.getModel().getValueAt(row, 0);
            int sellerId = BookService.getSellerIdByBook(bookId);

            boolean ok = OrderService.sendMessage(myUser.getId(), sellerId, text.trim());
            LoginScreen.msg(ok ? "Message sent to " + sellerName + "!" : "Error sending message.");
        });

        inboxBtn.addActionListener(e -> {
            ArrayList<String[]> msgs = OrderService.getMessagesForUser(myUser.getId());

            if (msgs.isEmpty()) {
                LoginScreen.msg("Your inbox is empty.\nMessages from sellers will appear here.");
                return;
            }

            StringBuilder sb = new StringBuilder("=== YOUR INBOX ===\n\n");
            int count = 1;
            for (String[] msg : msgs) {
                sb.append(count++).append(". From: ").append(msg[0])
                  .append("\n   ").append(msg[2])
                  .append("\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 13));
            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(480, 320));
            JOptionPane.showMessageDialog(this, scroll,
                "My Inbox (" + msgs.size() + " messages)", JOptionPane.INFORMATION_MESSAGE);
        });

        reviewBtn.addActionListener(e -> {
            int row = booksTable.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Please select a book first!"); return; }

            String sellerName = (String) booksTable.getModel().getValueAt(row, 7);

            String ratingStr = JOptionPane.showInputDialog(this,
                "Rate seller \"" + sellerName + "\" (enter 1 to 5):");
            if (ratingStr == null) return;

            String comment = JOptionPane.showInputDialog(this, "Write a comment (optional):");
            if (comment == null) comment = "";

            try {
                int rating   = Integer.parseInt(ratingStr.trim());
                if (rating < 1 || rating > 5) {
                    LoginScreen.msg("Rating must be between 1 and 5.");
                    return;
                }
                int bookId   = (int) booksTable.getModel().getValueAt(row, 0);
                int sellerId = BookService.getSellerIdByBook(bookId);

                boolean ok = OrderService.writeReview(myUser.getId(), sellerId, rating, comment);
                LoginScreen.msg(ok ? "Review submitted! Thank you." : "Could not submit review.");
            } catch (NumberFormatException ex) {
                LoginScreen.msg("Please enter a number from 1 to 5.");
            }
        });

        // REFRESH
        refreshBtn.addActionListener(e -> loadAllBooks());

        return p;
    }
}
