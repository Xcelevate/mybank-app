package com.training.mybank.model;

import java.util.Objects;

// Represents the Accounts table
public final class Account {
    private final int accountId;
    private final String userId;
    private final double balance;

    public Account(int accountId, String userId, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public int accountId() {
        return accountId;
    }

    public String userId() {
        return userId;
    }

    public double balance() {
        return balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Account) obj;
        return this.accountId == that.accountId &&
                Objects.equals(this.userId, that.userId) &&
                Double.doubleToLongBits(this.balance) == Double.doubleToLongBits(that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, balance);
    }

    @Override
    public String toString() {
        return "Account[" +
                "accountId=" + accountId + ", " +
                "userId=" + userId + ", " +
                "balance=" + balance + ']';
    }
}