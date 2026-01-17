package com.training.mybank.model;

import java.util.Objects;

// Represents the Users table
public final class User {
    private final String userId;
    private final String password;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String userId() {
        return userId;
    }

    public String password() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, password);
    }

    @Override
    public String toString() {
        return "User[" +
                "userId=" + userId + ", " +
                "password=" + password + ']';
    }
}