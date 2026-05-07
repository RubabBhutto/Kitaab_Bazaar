//Nighat

package service;

import db.DBConnection;
import exception.InvalidUserInputException;
import java.sql.*;
import java.util.ArrayList;
import model.Buyer;
import model.Seller;
import model.User;

public class UserService {

   
    public static User login(String phone, String password) {
        String sql = "SELECT * FROM users WHERE phone=? AND password=?";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int    id   = rs.getInt("id");
                String name = rs.getString("name");
                String pass = rs.getString("password");
                conn.close();

                // Return correct subtype — POLYMORPHISM
                if (role.equals("buyer"))  return new Buyer(id, name, phone, pass);
                if (role.equals("seller")) return new Seller(id, name, phone, pass);
                return new User(id, name, phone, pass, role); // admin
            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    
    public static boolean register(String name, String phone,
                                    String password, String role)
                                    throws InvalidUserInputException {

        // --- INPUT VALIDATION using custom exception ---
        if (name == null || name.trim().isEmpty())
            throw new InvalidUserInputException("name", "Name cannot be empty.");

        if (phone == null || !phone.matches("\\d{11}"))
            throw new InvalidUserInputException("phone",
                "Phone must be exactly 11 digits.");

        if (password == null || password.length() < 6)
            throw new InvalidUserInputException("password",
                "Password must be at least 6 characters.");

        if (phoneExists(phone)) return false;

        String sql = "INSERT INTO users (name, phone, password, role) VALUES (?,?,?,?)";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setString(4, role);
            int rows = stmt.executeUpdate();
            conn.close();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }

    
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>(); // COLLECTION
        String sql = "SELECT * FROM users";

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(sql);

            while (rs.next()) {
                User u = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                users.add(u);
            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("Get users error: " + e.getMessage());
        }
        return users;
    }

    
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id=?";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            conn.close();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Delete user error: " + e.getMessage());
            return false;
        }
    }

    
    private static boolean phoneExists(String phone) {
        String sql = "SELECT id FROM users WHERE phone=?";

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            conn.close();
            return exists;

        } catch (SQLException e) {
            return false;
        }
    }
}
