import models.Account;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/accountant";
        String username = "root";
        String password = "Admin@123";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Your database operations here
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}