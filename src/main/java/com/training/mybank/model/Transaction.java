package com.training.mybank.model;

import java.sql.Timestamp;

// Represents the Transactions table
public record Transaction(
        int transactionId,
        Integer fromAccount, // Integer allows null for deposits
        Integer toAccount,   // Integer allows null for withdrawals
        double amount,
        Timestamp timestamp
) {}
