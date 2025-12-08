package com.example.textbookmgmt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:h2:mem:textbooks;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS textbooks (
                        id IDENTITY PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        publisher VARCHAR(255) NOT NULL,
                        isbn VARCHAR(50) NOT NULL,
                        stock INT NOT NULL
                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库失败", e);
        }
    }
}
