package com.training.mybank;


import java.sql.*;

public class TestConnection {
    // Update these according to your DB
    private static final String JDBC_URL  = "jdbc:postgresql://localhost:5432/mybankdb?currentSchema=mybank";
    //private static final String JDBC_URL  = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER      = "postgres";
    private static final String PASSWORD  = "password";

    private static final String SQL_SELECT_ALL =
            "SELECT id, name, salary FROM employees";

    public static void main(String[] args) throws SQLException {


        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                long   id        = rs.getLong("id");
                String name = rs.getString("name");
                double salary    = rs.getDouble("salary");

                System.out.printf("ID=%d, %s, salary=%s",
                        id, name, salary);
                System.out.println("--------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // in real code, log this properly
        }
    }

    private static void createTable() throws SQLException {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS employees (
                id          INTEGER PRIMARY KEY,
                name        VARCHAR(100) NOT NULL,
                salary      DOUBLE PRECISION NOT NULL
            )
            """;

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(createTableSql)) {
            ps.execute();
            System.out.println("Table 'employees' ready.");
        }
    }
}
