package com.training.mybank.dao;

import com.training.mybank.DatabaseConfig;
import com.training.mybank.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void createAccount(Connection conn, String userId, double initialDeposit) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setDouble(2, initialDeposit);
            pstmt.executeUpdate();
        }
    }

    public Account getAccountById(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("user_id"),
                            rs.getDouble("balance")
                    );
                    return account;
                }
            }
        }
        return null;
    }

    public List<Account> getAccountsByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(new Account(
                            rs.getInt("account_id"),
                            rs.getString("user_id"),
                            rs.getDouble("balance")
                    ));
                }
            }
        }
        return accounts;
    }

    /**
     * Updates balance. Positive amount for deposits, negative for withdrawals.
     * Uses the connection provided by Service to support ACID transactions.
     */
    public void updateBalance(Connection conn, int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new SQLException("Update failed, account not found: " + accountId);
        }
    }
}