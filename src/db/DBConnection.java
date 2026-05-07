//Nighat 
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static String URL  = "jdbc:mysql://127.0.0.1:3306/kitaabbazaar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static String USER = "root";
    static String PASS = "Rubab@Bhutto25";

    
    private static Connection connection = null;

    
    public static Connection getConnection() {
        try {
            
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Database connected successfully.");
            }
            return connection; 

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();           
                connection = null;           
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    
}