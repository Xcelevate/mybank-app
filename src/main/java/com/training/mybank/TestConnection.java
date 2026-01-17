package com.training.mybank;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnection {
    // Update these according to your DB
    private static final String JDBC_URL  = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER      = "myuser";
    private static final String PASSWORD  = "mypassword";

    private static final String SQL_SELECT_ALL =
            "SELECT id, first_name, last_name, salary FROM employees";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                long   id        = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName  = rs.getString("last_name");
                double salary    = rs.getDouble("salary");

                System.out.printf("ID=%d, %s %s, salary=%.2f%n",
                        id, firstName, lastName, salary);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // in real code, log this properly
        }
    }
}
