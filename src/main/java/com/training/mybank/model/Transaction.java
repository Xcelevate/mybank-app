package com.training.mybank.model;

import java.sql.Timestamp;
import java.util.Objects;

// Represents the Transactions table
public final class Transaction {
    private final int transactionId;
    private final Integer fromAccount;
    private final Integer toAccount;
    private final double amount;
    private final Timestamp timestamp;

    public Transaction(
            int transactionId,
            Integer fromAccount, // Integer allows null for deposits
            Integer toAccount,   // Integer allows null for withdrawals
            double amount,
            Timestamp timestamp
    ) {
        this.transactionId = transactionId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int transactionId() {
        return transactionId;
    }

    public Integer fromAccount() {
        return fromAccount;
    }

    public Integer toAccount() {
        return toAccount;
    }

    public double amount() {
        return amount;
    }

    public Timestamp timestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transaction) obj;
        return this.transactionId == that.transactionId &&
                Objects.equals(this.fromAccount, that.fromAccount) &&
                Objects.equals(this.toAccount, that.toAccount) &&
                Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, fromAccount, toAccount, amount, timestamp);
    }

    @Override
    public String toString() {
        return "Transaction[" +
                "transactionId=" + transactionId + ", " +
                "fromAccount=" + fromAccount + ", " +
                "toAccount=" + toAccount + ", " +
                "amount=" + amount + ", " +
                "timestamp=" + timestamp + ']';
    }
}
