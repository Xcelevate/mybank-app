package com.training.mybank.service;

import com.training.mybank.config.DatabaseConfig;
import com.training.mybank.dao.AccountDAO;
import com.training.mybank.dao.TransactionDAO;
import com.training.mybank.dao.UserDAO;
import com.training.mybank.model.Account;
import com.training.mybank.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BankingService {
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private User currentUser = null;

    // --- Authentication ---

    public boolean login(String userId, String password) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user != null && user.password().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
        System.out.println("Logged out successfully.");
    }

    // --- Account Management ---

    public void createAccount(double initialDeposit) throws SQLException {
        if (currentUser == null) throw new IllegalStateException("Login required.");

        try (Connection conn = DatabaseConfig.getConnection()) {
            accountDAO.createAccount(conn, currentUser.userId(), initialDeposit);
        }
    }

    public List<Account> getMyAccounts() throws SQLException {
        if (currentUser == null) throw new IllegalStateException("Login required.");
        return accountDAO.getAccountsByUserId(currentUser.userId());
    }

    public double getBalance(int accountId) throws SQLException {
        validateOwnership(accountId);
        return accountDAO.getAccountById(accountId).balance();
    }

    // --- Financial Operations ---

    public void deposit(int accountId, double amount) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        validateOwnership(accountId);

        try (Connection conn = DatabaseConfig.getConnection()) {
            accountDAO.updateBalance(conn, accountId, amount);
            transactionDAO.recordTransaction(conn, null, accountId, amount); // null fromAcc = deposit
        }
    }

    public void withdraw(int accountId, double amount) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        validateOwnership(accountId);

        try (Connection conn = DatabaseConfig.getConnection()) {
            Account account = accountDAO.getAccountById(accountId);
            double currentBalance = account.balance();
            if (currentBalance < amount) throw new RuntimeException("Insufficient funds.");

            accountDAO.updateBalance(conn, accountId, -amount);
            transactionDAO.recordTransaction(conn, accountId, null, amount); // null toAcc = withdrawal
        }
    }

    public void transferFunds(int fromAccId, int toAccId, double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        validateOwnership(fromAccId);

        try (Connection conn = DatabaseConfig.getConnection()) {
            try {
                conn.setAutoCommit(false); // ACID Transaction Start

                double balance = accountDAO.getAccountById(fromAccId).balance();
                if (balance < amount) throw new SQLException("Insufficient funds.");

                accountDAO.updateBalance(conn, fromAccId, -amount);
                accountDAO.updateBalance(conn, toAccId, amount);
                transactionDAO.recordTransaction(conn, fromAccId, toAccId, amount);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // --- Helper Logic ---

    private void validateOwnership(int accountId) throws SQLException {
        if (currentUser == null) throw new IllegalStateException("Login required.");
        Account acc = accountDAO.getAccountById(accountId);
        if (acc == null || !acc.userId().equals(currentUser.userId())) {
            throw new SecurityException("Unauthorized access to account ID: " + accountId);
        }
    }
}