//Rubab

package service;
import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import model.Book;

public class BookService {

    
    public static ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();

        String sql =
            "SELECT b.id, b.title, b.grade, b.publisher, b.price, " +
            "b.condition_, b.status, b.seller_id, b.category_id, " +
            "c.name AS cat, u.name AS seller " +
            "FROM books b " +
            "JOIN categories c ON b.category_id = c.id " +
            "JOIN users u      ON b.seller_id   = u.id " +
            "WHERE b.status = 'available'";

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(sql);

            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("grade"),
                    rs.getString("publisher"),
                    rs.getDouble("price"),
                    rs.getString("condition_"),
                    rs.getString("status"),
                    rs.getInt("seller_id"),
                    rs.getInt("category_id")
                );
                b.setCategoryName(rs.getString("cat"));
                b.setSellerName(rs.getString("seller"));
                books.add(b);
            }

        } catch (SQLException e) {
            System.out.println("Get books error: " + e.getMessage());
        }
        return books;
    }

    
    public static ArrayList<Book> searchBooks(String keyword, int grade) {
        ArrayList<Book> books = new ArrayList<>();

        String sql =
            "SELECT b.id, b.title, b.grade, b.publisher, b.price, " +
            "b.condition_, b.status, b.seller_id, b.category_id, " +
            "c.name AS cat, u.name AS seller " +
            "FROM books b " +
            "JOIN categories c ON b.category_id = c.id " +
            "JOIN users u      ON b.seller_id   = u.id " +
            "WHERE b.status = 'available'";

        if (!keyword.isEmpty()) sql += " AND (b.title LIKE '%" + keyword + "%' OR b.publisher LIKE '%" + keyword + "%')";
        if (grade > 0)          sql += " AND b.grade = " + grade;

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(sql);

            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("id"), rs.getString("title"),
                    rs.getInt("grade"), rs.getString("publisher"),
                    rs.getDouble("price"), rs.getString("condition_"),
                    rs.getString("status"), rs.getInt("seller_id"),
                    rs.getInt("category_id")
                );
                b.setCategoryName(rs.getString("cat"));
                b.setSellerName(rs.getString("seller"));
                books.add(b);
            }

        } catch (SQLException e) {
            System.out.println("Search error: " + e.getMessage());
        }
        return books;
    }

    
    public static ArrayList<Book> getBooksBySeller(int sellerId) {
        ArrayList<Book> books = new ArrayList<>();

        String sql =
            "SELECT b.id, b.title, b.grade, b.publisher, b.price, " +
            "b.condition_, b.status, b.seller_id, b.category_id, c.name AS cat " +
            "FROM books b JOIN categories c ON b.category_id = c.id " +
            "WHERE b.seller_id = ?";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("id"), rs.getString("title"),
                    rs.getInt("grade"), rs.getString("publisher"),
                    rs.getDouble("price"), rs.getString("condition_"),
                    rs.getString("status"), rs.getInt("seller_id"),
                    rs.getInt("category_id")
                );
                b.setCategoryName(rs.getString("cat"));
                books.add(b);
            }

        } catch (SQLException e) {
            System.out.println("Get seller books error: " + e.getMessage());
        }
        return books;
    }

    public static boolean addBook(String title, int grade, String publisher,
                                   double price, String condition,
                                   int sellerId, int categoryId) {
        String sql =
            "INSERT INTO books (title, grade, publisher, price, condition_, seller_id, category_id) " +
            "VALUES (?,?,?,?,?,?,?)";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, grade);
            stmt.setString(3, publisher);
            stmt.setDouble(4, price);
            stmt.setString(5, condition);
            stmt.setInt(6, sellerId);
            stmt.setInt(7, categoryId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Add book error: " + e.getMessage());
            return false;
        }
    }

    
    public static boolean updateBook(int bookId, String title, int grade,
                                      double price, String condition, int categoryId) {
        String sql =
            "UPDATE books SET title=?, grade=?, price=?, condition_=?, category_id=? " +
            "WHERE id=? AND status='available'";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, grade);
            stmt.setDouble(3, price);
            stmt.setString(4, condition);
            stmt.setInt(5, categoryId);
            stmt.setInt(6, bookId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Update book error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteBook(int bookId) {
        try {
            Connection conn = DBConnection.getConnection();

            // Check if any pending orders exist for this book
            PreparedStatement check = conn.prepareStatement(
                "SELECT COUNT(*) FROM orders WHERE book_id=? AND status='pending'"
            );
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Cannot delete — there are pending orders
                return false;
            }

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id=?");
            stmt.setInt(1, bookId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Delete book error: " + e.getMessage());
            return false;
        }
    }

    
    public static ArrayList<String> getCategories() {
        ArrayList<String> cats = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery("SELECT name FROM categories ORDER BY id");
            while (rs.next()) cats.add(rs.getString("name"));

        } catch (SQLException e) {
            System.out.println("Get categories error: " + e.getMessage());
        }
        return cats;
    }

    public static int getCategoryIdByName(String name) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM categories WHERE name=?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("Get category id error: " + e.getMessage());
        }
        return 1;
    }

    
    public static int getSellerIdByBook(int bookId) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT seller_id FROM books WHERE id=?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("seller_id");
            }
        } catch (SQLException e) {
            System.out.println("Get seller error: " + e.getMessage());
        }
        return -1;
    }
}
