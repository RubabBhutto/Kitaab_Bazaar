//Rubab AND Nighat 

package service;
import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class OrderService {

    
    public static boolean confirmSale(int orderId, int buyerId) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement getBook = conn.prepareStatement(
                "SELECT o.book_id, b.price, u.name AS buyer_name " +
                "FROM orders o " +
                "JOIN books b ON o.book_id = b.id " +
                "JOIN users u ON o.buyer_id = u.id " +
                "WHERE o.id=?"
            );
            getBook.setInt(1, orderId);
            ResultSet rs = getBook.executeQuery();
            if (!rs.next()) { return false; }
            int    bookId    = rs.getInt("book_id");
            double bookPrice = rs.getDouble("price");

            double commission   = bookPrice * 0.02;       // 2% to platform
            double sellerAmount = bookPrice - commission;  // 98% to seller

            PreparedStatement confirm = conn.prepareStatement(
                "UPDATE orders SET status='completed' WHERE id=?"
            );
            confirm.setInt(1, orderId);
            confirm.executeUpdate();

            PreparedStatement cancelOthers = conn.prepareStatement(
                "UPDATE orders SET status='cancelled' WHERE book_id=? AND id!=?"
            );
            cancelOthers.setInt(1, bookId);
            cancelOthers.setInt(2, orderId);
            cancelOthers.executeUpdate();

            PreparedStatement sold = conn.prepareStatement(
                "UPDATE books SET status='sold' WHERE id=?"
            );
            sold.setInt(1, bookId);
            sold.executeUpdate();

            PreparedStatement payment = conn.prepareStatement(
                "INSERT INTO payments (order_id, total_amount, commission, seller_amount, status) " +
                "VALUES (?, ?, ?, ?, 'paid')"
            );
            payment.setInt(1, orderId);
            payment.setDouble(2, bookPrice);
            payment.setDouble(3, commission);
            payment.setDouble(4, sellerAmount);
            payment.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Confirm sale error: " + e.getMessage());
            return false;
        }
    }

    
    public static boolean placeOrder(int buyerId, int bookId) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement order = conn.prepareStatement(
                "INSERT INTO orders (buyer_id, book_id, status) VALUES (?,?,'pending')"
            );
            order.setInt(1, buyerId);
            order.setInt(2, bookId);
            order.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Place order error: " + e.getMessage());
            return false;
        }
    }

   
    public static boolean sendMessage(int senderId, int receiverId, String message) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message) VALUES (?,?,?)";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, message);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Send message error: " + e.getMessage());
            return false;
        }
    }

    public static boolean writeReview(int buyerId, int sellerId,
                                       int rating, String comment) {
        if (rating < 1 || rating > 5) return false;

        String sql = "INSERT INTO reviews (buyer_id, seller_id, rating, comment) VALUES (?,?,?,?)";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, buyerId);
            stmt.setInt(2, sellerId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Write review error: " + e.getMessage());
            return false;
        }
    }

    
    public static ArrayList<String[]> getOrdersForSeller(int sellerId) {
        ArrayList<String[]> orders = new ArrayList<>();

        String sql =
            "SELECT o.id, b.title, u.name AS buyer, u.id AS buyer_id, " +
            "o.status, b.price " +
            "FROM orders o " +
            "JOIN books b ON o.book_id  = b.id " +
            "JOIN users u ON o.buyer_id = u.id " +
            "WHERE b.seller_id = ? " +
            "ORDER BY o.placed_at DESC";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] order = {
                    String.valueOf(rs.getInt("id")),       // index 0: order id
                    rs.getString("title"),                  // index 1: book title
                    rs.getString("buyer"),                  // index 2: buyer name
                    String.valueOf(rs.getInt("buyer_id")), // index 3: buyer id
                    rs.getString("status"),                 // index 4: order status
                    String.valueOf(rs.getDouble("price"))   // index 5: book price
                };
                orders.add(order);
            }

        } catch (SQLException e) {
            System.out.println("Get orders error: " + e.getMessage());
        }
        return orders;
    }

    
    public static ArrayList<String[]> getMessagesForUser(int userId) {
        ArrayList<String[]> messages = new ArrayList<>();

        String sql =
            "SELECT u.name AS sender, u.id AS sender_id, m.message, m.sent_at " +
            "FROM messages m JOIN users u ON m.sender_id = u.id " +
            "WHERE m.receiver_id = ? " +
            "ORDER BY m.sent_at DESC";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] msg = {
                    rs.getString("sender"),                  // index 0: sender name
                    String.valueOf(rs.getInt("sender_id")), // index 1: sender id
                    rs.getString("message")                  // index 2: message text
                };
                messages.add(msg);
            }

        } catch (SQLException e) {
            System.out.println("Get messages error: " + e.getMessage());
        }
        return messages;
    }

    
    public static double getBookPrice(int orderId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT b.price FROM orders o JOIN books b ON o.book_id=b.id WHERE o.id=?"
            );
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            System.out.println("Get price error: " + e.getMessage());
        }
        return 0;
    }

    
    public static int getCount(String table) {
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet  rs   = conn.createStatement()
                                  .executeQuery("SELECT COUNT(*) FROM " + table);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }
}
