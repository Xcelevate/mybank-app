package com.training.mybank.dao;

import java.sql.*;

public class TransactionDAO {

    /**
     * Records a log of the transaction.
     * Use Integer for IDs to allow 'null' values (e.g., for external deposits/withdrawals).
     */
    public void recordTransaction(Connection conn, Integer fromAcc, Integer toAcc, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (from_account, to_account, amount) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (fromAcc != null) pstmt.setInt(1, fromAcc);
            else pstmt.setNull(1, Types.INTEGER);

            if (toAcc != null) pstmt.setInt(2, toAcc);
            else pstmt.setNull(2, Types.INTEGER);

            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        }
    }
}