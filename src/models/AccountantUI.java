package models;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AccountantUI {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/accountant"; // Change to your MySQL database URL
    private static final String DB_USER = "root"; // Change to your MySQL username
    private static final String DB_PASS = "Admin@123"; // Change to your MySQL password

    private static final Logger logger = Logger.getLogger(AccountantUI.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("accountant.log", true); // true appends to the log file
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Display welcome message
        JOptionPane.showMessageDialog(null, "Welcome Mr. Accountant!\n\n\nWhat do you want to do?");

        // Initialize account balance
        double balance = 0;
        int newAccountId = 0;

        logger.log(Level.INFO, "Accountant app started!");

        // Main program loop
        while (true) {
            // Show options using JOptionPane
            String[] options = {"Create Account", "Add Balance", "Withdraw", "Check Balance", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Choose an option:",
                    "Accountant Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // Process user choice
            switch (choice) {
                case 0: // Create Account
                    String accountName = JOptionPane.showInputDialog(null, "Enter account holder's name:");
                    Account newAccount = new Account(++newAccountId, accountName, 0.0);
                    createAccount(newAccount);
                    break;

                case 1: // Add Balance
                    String addBalanceStr = JOptionPane.showInputDialog(null, "Enter amount to add:");
                    double amountToAdd = Double.parseDouble(addBalanceStr);
                    String accountId = JOptionPane.showInputDialog(null, "Enter account ID:");
                    addBalance(accountId, amountToAdd);
                    break;

                case 2: // Withdraw
                    String withdrawStr = JOptionPane.showInputDialog(null, "Enter amount to withdraw:");
                    double amountToWithdraw = Double.parseDouble(withdrawStr);
                    accountId = JOptionPane.showInputDialog(null, "Enter account ID:");
                    withdraw(accountId, amountToWithdraw);
                    break;

                case 3: // Check Balance
                    accountId = JOptionPane.showInputDialog(null, "Enter account ID:");
                    checkBalance(accountId);
                    break;

                case 4: // Exit
                    JOptionPane.showMessageDialog(null, "Thank you for using the Accountant App!");
                    System.exit(0);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice!");
            }
        }
    }


    private static void createAccount(Account account) {
        String query = "INSERT INTO accounts (name, balance) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // Create connection to database
             // Create query
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, account.getName());
            stmt.setDouble(2, account.getBalance()); // Initial balance is set to 0
            stmt.executeUpdate();
            logger.log(Level.INFO, "Account created for: " + account.getName());
            JOptionPane.showMessageDialog(null, "Account created for: " + account.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Failed to create account: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Failed to create account: " + e.getMessage());
        }
    }

    private static void addBalance(String accountId, double amountToAdd) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
            stmt.setDouble(1, amountToAdd);
            stmt.setString(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO,  "Balance updated. New balance: " + getBalance(accountId));
                JOptionPane.showMessageDialog(null, "Balance updated. New balance: " + getBalance(accountId));
            } else {
                logger.log(Level.INFO,  null, "Account not found!");
                JOptionPane.showMessageDialog(null, "Account not found!");
            }
        } catch (SQLException e) {
            logger.log(Level.INFO,  null, "Failed to add balance: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to add balance: " + e.getMessage());
        }
    }

    private static void withdraw(String accountId, double amountToWithdraw) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?")) {
            stmt.setDouble(1, amountToWithdraw);
            stmt.setString(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO,  null, "Withdraw successful. New balance: " + getBalance(accountId));
                JOptionPane.showMessageDialog(null, "Withdraw successful. New balance: " + getBalance(accountId));
            } else {
                logger.log(Level.INFO,  null, "Account not found or insufficient balance!");
                JOptionPane.showMessageDialog(null, "Account not found or insufficient balance!");
            }
        } catch (SQLException e) {
            logger.log(Level.INFO,  null, "Failed to withdraw: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to withdraw: " + e.getMessage());
        }
    }

    private static void checkBalance(String accountId) {
        double balance = getBalance(accountId);
        if (balance >= 0) {
            logger.log(Level.INFO,  null, "Your current balance: " + balance);
            JOptionPane.showMessageDialog(null, "Your current balance: " + balance);
        } else {
            logger.log(Level.INFO,  null,"Account not found!");
            JOptionPane.showMessageDialog(null, "Account not found!");
        }
    }

    private static double getBalance(String accountId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?")) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.INFO,  null,"Failed to retrieve balance: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to retrieve balance: " + e.getMessage());
        }
        return -1; // Account not found
    }
}

