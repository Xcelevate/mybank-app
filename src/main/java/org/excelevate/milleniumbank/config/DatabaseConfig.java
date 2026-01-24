package org.excelevate.milleniumbank.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/mybankdb?currentSchema=mybank";
    private static final String USER = "postgres";
    private static final String PASS = "password";
    private static HikariDataSource dataSource;
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mybankdb?currentSchema=mybank");
        config.setUsername("postgres");
        config.setPassword("password");
        // =======================================

        config.setMaximumPoolSize(2);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);

        dataSource = new HikariDataSource(config);
    }

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection ==null) {
            connection = dataSource.getConnection();
        }
        return connection;
    }
}
