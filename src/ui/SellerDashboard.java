//Nighat Part
package ui;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import model.Book;
import model.User;
import service.BookService;
import service.OrderService;

public class SellerDashboard extends JFrame {

    User   myUser;
    JTable myBooksTable;
   
    ArrayList<Book> myBooksList = new ArrayList<>();

    public SellerDashboard(User user) {
        this.myUser = user;

        setTitle("KitaabBazaar - Seller");
        setSize(900, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(LoginScreen.GRAY);

        add(topBar(),  BorderLayout.NORTH);
        add(content(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(40, 167, 69));
        p.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JLabel lbl = new JLabel("Seller Dashboard — " + myUser.getName());
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        JButton out = new JButton("Logout");
        out.addActionListener(e -> { dispose(); new LoginScreen(); });

        p.add(lbl, BorderLayout.WEST);
        p.add(out, BorderLayout.EAST);
        return p;
    }

   
    private JSplitPane content() {
        myBooksTable = new JTable(loadMyBooks());
        myBooksTable.setRowHeight(26);
        myBooksTable.setFont(new Font("Arial", Font.PLAIN, 13));
        myBooksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        myBooksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
        myBooksTable.getColumnModel().getColumn(0).setMinWidth(0);
        myBooksTable.getColumnModel().getColumn(0).setMaxWidth(0);
        JScrollPane scroll = new JScrollPane(myBooksTable);
        scroll.setBorder(BorderFactory.createTitledBorder("My Books (select a row to Update or Delete)"));

        JPanel bottom = new JPanel(new BorderLayout(8,8));
        bottom.setBackground(LoginScreen.GRAY);
        bottom.add(addBookForm(),    BorderLayout.CENTER);
        bottom.add(actionButtons(), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, bottom);
        split.setDividerLocation(220);
        split.setEnabled(false);
        return split;
    }

    private JPanel addBookForm() {
        JPanel p = new JPanel(new GridLayout(7, 2, 10, 8));
        p.setBackground(LoginScreen.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Add New Book"));

        JTextField titleF     = new JTextField();
        JTextField gradeF     = new JTextField("0");
        JTextField publisherF = new JTextField();
        JTextField priceF     = new JTextField();
        JComboBox<String> condBox = new JComboBox<>(new String[]{"good","new","fair"});

        ArrayList<String> cats = BookService.getCategories();
        JComboBox<String> catBox = new JComboBox<>(cats.toArray(new String[0]));

        JButton addBtn    = LoginScreen.makeButton("Post Book", LoginScreen.BLUE);
        JLabel  statusLbl = new JLabel(" ");

        p.add(new JLabel("Book Title:"));       p.add(titleF);
        p.add(new JLabel("Grade (0=Novel):"));  p.add(gradeF);
        p.add(new JLabel("Publisher:"));        p.add(publisherF);
        p.add(new JLabel("Price (Rs.):"));      p.add(priceF);
        p.add(new JLabel("Condition:"));        p.add(condBox);
        p.add(new JLabel("Category:"));         p.add(catBox);
        p.add(addBtn);                          p.add(statusLbl);

        addBtn.addActionListener(e -> {
            String title     = titleF.getText().trim();
            String grade     = gradeF.getText().trim();
            String publisher = publisherF.getText().trim();
            String price     = priceF.getText().trim();

            if (title.isEmpty() || grade.isEmpty() || price.isEmpty()) {
                statusLbl.setForeground(Color.RED);
                statusLbl.setText("Fill all required fields!"); return;
            }

            try {
                int    g     = Integer.parseInt(grade);
                double pr    = Double.parseDouble(price);
                if (pr <= 0) throw new NumberFormatException("Price must be > 0");
                String cond  = (String) condBox.getSelectedItem();
                int    catId = catBox.getSelectedIndex() + 1;
                String pub   = publisher.isEmpty() ? "N/A" : publisher;

                boolean ok = BookService.addBook(title, g, pub, pr, cond, myUser.getId(), catId);
                if (ok) {
                    statusLbl.setForeground(new Color(0,128,0));
                    statusLbl.setText("Book posted successfully!");
                    titleF.setText(""); gradeF.setText("0");
                    publisherF.setText(""); priceF.setText("");
                    refreshBooksTable();
                } else {
                    statusLbl.setForeground(Color.RED);
                    statusLbl.setText("Error posting book.");
                }
            } catch (NumberFormatException ex) {
                statusLbl.setForeground(Color.RED);
                statusLbl.setText("Grade must be a whole number, Price must be a valid amount.");
            }
        });

        return p;
    }

  
    private JPanel actionButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p.setBackground(LoginScreen.GRAY);

        JButton updateBtn  = LoginScreen.makeButton("Update Selected Book", new Color(0,120,180));
        JButton deleteBtn  = LoginScreen.makeButton("Delete Selected Book", new Color(200,50,50));
        JButton ordersBtn  = LoginScreen.makeButton("View Orders & Confirm Sale", new Color(255,153,0));
        JButton msgsBtn    = LoginScreen.makeButton("View Messages & Reply",      new Color(108,99,255));
        JButton refreshBtn = new JButton("Refresh");

        p.add(updateBtn);
        p.add(deleteBtn);
        p.add(ordersBtn);
        p.add(msgsBtn);
        p.add(refreshBtn);

        updateBtn.addActionListener(e -> {
            int row = myBooksTable.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Please select a book from the table to update."); return; }

            
            int bookId = (int) myBooksTable.getModel().getValueAt(row, 0);
            Book selected = null;
            for (Book b : myBooksList) {
                if (b.getId() == bookId) { selected = b; break; }
            }
            if (selected == null) { LoginScreen.msg("Could not find book data."); return; }

            if (selected.getStatus().equals("sold")) {
                LoginScreen.msg("This book is already sold and cannot be updated.");
                return;
            }

          
            JTextField titleF  = new JTextField(selected.getTitle());
            JTextField gradeF  = new JTextField(String.valueOf(selected.getGrade()));
            JTextField priceF  = new JTextField(String.valueOf(selected.getPrice()));
            JComboBox<String> condBox = new JComboBox<>(new String[]{"good","new","fair"});
            condBox.setSelectedItem(selected.getCondition());

            ArrayList<String> cats = BookService.getCategories();
            JComboBox<String> catBox = new JComboBox<>(cats.toArray(new String[0]));
            
            catBox.setSelectedIndex(Math.max(0, selected.getCategoryId() - 1));

            Object[] fields = {
                "Title:",     titleF,
                "Grade (0=Novel):", gradeF,
                "Price (Rs.):", priceF,
                "Condition:", condBox,
                "Category:",  catBox
            };

            int choice = JOptionPane.showConfirmDialog(this, fields,
                "Update Book: " + selected.getTitle(), JOptionPane.OK_CANCEL_OPTION);

            if (choice != JOptionPane.OK_OPTION) return;

            try {
                String newTitle = titleF.getText().trim();
                int    newGrade = Integer.parseInt(gradeF.getText().trim());
                double newPrice = Double.parseDouble(priceF.getText().trim());
                String newCond  = (String) condBox.getSelectedItem();
                int    newCatId = catBox.getSelectedIndex() + 1;

                if (newTitle.isEmpty()) { LoginScreen.msg("Title cannot be empty."); return; }
                if (newPrice <= 0)      { LoginScreen.msg("Price must be greater than 0."); return; }

                boolean ok = BookService.updateBook(bookId, newTitle, newGrade, newPrice, newCond, newCatId);
                if (ok) {
                    LoginScreen.msg("Book updated successfully!");
                    refreshBooksTable();
                } else {
                    LoginScreen.msg("Could not update — book may already be sold or not found.");
                }
            } catch (NumberFormatException ex) {
                LoginScreen.msg("Grade must be a whole number and Price must be a valid number.");
            }
        });

        
        deleteBtn.addActionListener(e -> {
            int row = myBooksTable.getSelectedRow();
            if (row == -1) { LoginScreen.msg("Please select a book from the table to delete."); return; }

            int    bookId    = (int)    myBooksTable.getModel().getValueAt(row, 0);
            String bookTitle = (String) myBooksTable.getModel().getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n\"" + bookTitle + "\"?\n\n" +
                "Note: Books with pending orders cannot be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = BookService.deleteBook(bookId);
            if (ok) {
                LoginScreen.msg("Book deleted successfully.");
                refreshBooksTable();
            } else {
                LoginScreen.msg("Cannot delete this book.\nIt may have pending orders attached to it.\nCancel those orders first or wait for them to complete.");
            }
        });

       
        ordersBtn.addActionListener(e -> {
            ArrayList<String[]> orders = OrderService.getOrdersForSeller(myUser.getId());

            if (orders.isEmpty()) {
                LoginScreen.msg("No orders yet. Your books have not been ordered.");
                return;
            }

            String[] displayOrders = new String[orders.size()];
            for (int i = 0; i < orders.size(); i++) {
                displayOrders[i] =
                    "Order #" + orders.get(i)[0] +
                    " | Book: "   + orders.get(i)[1] +
                    " | Buyer: "  + orders.get(i)[2] +
                    " | Status: " + orders.get(i)[4];
            }

            String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select an order to confirm sale:\n(Only 'pending' orders can be confirmed)",
                "Orders — Confirm Sale",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOrders,
                displayOrders[0]
            );

            if (chosen == null) return;

            int selectedIndex = 0;
            for (int i = 0; i < displayOrders.length; i++) {
                if (displayOrders[i].equals(chosen)) { selectedIndex = i; break; }
            }

            String[] selectedOrder = orders.get(selectedIndex);
            int    orderId       = Integer.parseInt(selectedOrder[0]);
            String bookTitle     = selectedOrder[1];
            String buyerName     = selectedOrder[2];
            int    buyerId       = Integer.parseInt(selectedOrder[3]);
            String currentStatus = selectedOrder[4];

           
            if (currentStatus.equals("completed")) {
                LoginScreen.msg("This order is already completed!");
                return;
            }
            if (currentStatus.equals("cancelled")) {
                LoginScreen.msg("This order has been cancelled and cannot be confirmed.");
                return;
            }

           
            double price      = Double.parseDouble(selectedOrder[5]);
            double commission = price * 0.02;
            double sellerGets = price - commission;

                int confirm = JOptionPane.showConfirmDialog(
                this,
                "=== SALE CONFIRMATION ===\n\n" +
                "Book Title  : " + bookTitle    + "\n" +
                "Sold To     : " + buyerName    + "\n\n" +
                "--- Payment Breakdown ---\n" +
                "Book Price  : Rs. " + String.format("%.2f", price)       + "\n" +
                "Platform Fee: Rs. " + String.format("%.2f", commission)  + " (2%)\n" +
                "You Receive : Rs. " + String.format("%.2f", sellerGets)  + "\n\n" +
                "Confirming this will mark the book as SOLD\n" +
                "and notify " + buyerName + " automatically.\n\n" +
                "Proceed with sale?",
                "Confirm Sale",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = OrderService.confirmSale(orderId, buyerId);

            if (ok) {
                
                String autoMsg =
                    "Assalam o Alaikum " + buyerName + "!\n" +
                    "Your order for '" + bookTitle + "' has been CONFIRMED by " + myUser.getName() + ".\n" +
                    "Book Price: Rs." + String.format("%.2f", price) + "\n" +
                    "Please contact the seller to arrange pickup. JazakAllah!";

                OrderService.sendMessage(myUser.getId(), buyerId, autoMsg);

                LoginScreen.msg(
                    "Sale confirmed successfully!\n\n" +
                    "Book: " + bookTitle + "\n" +
                    "Sold to: " + buyerName + "\n" +
                    "Amount you receive: Rs. " + String.format("%.2f", sellerGets) + "\n\n" +
                    buyerName + " has been notified automatically."
                );
                refreshBooksTable();
            } else {
                LoginScreen.msg("Error confirming sale. Please try again.\n" +
                    "Make sure the order is still pending.");
            }
        });

        
        msgsBtn.addActionListener(e -> {
            ArrayList<String[]> msgs = OrderService.getMessagesForUser(myUser.getId());

            if (msgs.isEmpty()) {
                LoginScreen.msg("No messages yet. Buyers will message you here when interested.");
                return;
            }

            String[] displayMsgs = new String[msgs.size()];
            for (int i = 0; i < msgs.size(); i++) {
                displayMsgs[i] = "From: " + msgs.get(i)[0] + " — " + msgs.get(i)[2];
            }

            String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select a message to reply to:",
                "Your Messages",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayMsgs,
                displayMsgs[0]
            );

            if (chosen == null) return;

            int selectedIndex = 0;
            for (int i = 0; i < displayMsgs.length; i++) {
                if (displayMsgs[i].equals(chosen)) { selectedIndex = i; break; }
            }

            int    senderId  = Integer.parseInt(msgs.get(selectedIndex)[1]);
            String senderName = msgs.get(selectedIndex)[0];

            String reply = JOptionPane.showInputDialog(
                this,
                "Reply to " + senderName + ":"
            );

            if (reply == null || reply.trim().isEmpty()) return;

            boolean ok = OrderService.sendMessage(myUser.getId(), senderId, reply.trim());
            LoginScreen.msg(ok ? "Reply sent to " + senderName + "!" : "Error sending reply.");
        });

   
        refreshBtn.addActionListener(e -> refreshBooksTable());

        return p;
    }

    private DefaultTableModel loadMyBooks() {
        String[] cols = {"ID","Title","Grade","Category","Condition","Price","Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        myBooksList = BookService.getBooksBySeller(myUser.getId());
        for (Book b : myBooksList) {
            model.addRow(new Object[]{
                b.getId(),                                         // hidden col 0
                b.getTitle(),
                b.getGrade() == 0 ? "Novel" : b.getGrade(),
                b.getCategoryName(),
                b.getCondition(),
                "Rs. " + b.getPrice(),
                b.getStatus()
            });
        }
        return model;
    }

    private void refreshBooksTable() {
        myBooksTable.setModel(loadMyBooks());
  
        myBooksTable.getColumnModel().getColumn(0).setMinWidth(0);
        myBooksTable.getColumnModel().getColumn(0).setMaxWidth(0);
    }
}
