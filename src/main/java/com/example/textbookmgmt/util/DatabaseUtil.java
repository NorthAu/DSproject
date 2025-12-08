package com.example.textbookmgmt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public final class DatabaseUtil {
    private static final String PROFILE_PROPERTY = "db.profile";
    private static final String PROFILE_ENV = "DB_PROFILE";

    private static final String H2_URL = "jdbc:h2:mem:textbooks;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    private static final String MYSQL_DEFAULT_URL = "jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC";
    private static final String MYSQL_DEFAULT_USER = "root";
    private static final String MYSQL_DEFAULT_PASSWORD = "";

    private static final String MYSQL_URL_ENV = "DB_URL";
    private static final String MYSQL_USER_ENV = "DB_USER";
    private static final String MYSQL_PASSWORD_ENV = "DB_PASSWORD";

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        Profile profile = resolveProfile();
        String url = resolveJdbcUrl(profile);
        String user = resolveUser(profile);
        String password = resolvePassword(profile);
        return DriverManager.getConnection(url, user, password);
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS textbooks (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        publisher VARCHAR(255) NOT NULL,
                        isbn VARCHAR(50) NOT NULL,
                        stock INT NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    INSERT INTO textbooks(title, author, publisher, isbn, stock)
                    SELECT 'Java 核心技术', 'Cay S. Horstmann', '机械工业出版社', '9787111547425', 12
                    WHERE NOT EXISTS (SELECT 1 FROM textbooks)
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库失败", e);
        }
    }

    private static Profile resolveProfile() {
        String configured = System.getProperty(PROFILE_PROPERTY, System.getenv(PROFILE_ENV));
        if (configured == null || configured.isBlank()) {
            return Profile.H2;
        }
        try {
            return Profile.valueOf(configured.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return Profile.H2;
        }
    }

    private static String resolveJdbcUrl(Profile profile) {
        if (profile == Profile.MYSQL) {
            return System.getProperty(MYSQL_URL_ENV,
                    System.getenv().getOrDefault(MYSQL_URL_ENV, MYSQL_DEFAULT_URL));
        }
        return H2_URL;
    }

    private static String resolveUser(Profile profile) {
        if (profile == Profile.MYSQL) {
            return System.getProperty(MYSQL_USER_ENV,
                    System.getenv().getOrDefault(MYSQL_USER_ENV, MYSQL_DEFAULT_USER));
        }
        return H2_USER;
    }

    private static String resolvePassword(Profile profile) {
        if (profile == Profile.MYSQL) {
            return System.getProperty(MYSQL_PASSWORD_ENV,
                    System.getenv().getOrDefault(MYSQL_PASSWORD_ENV, MYSQL_DEFAULT_PASSWORD));
        }
        return H2_PASSWORD;
    }

    private enum Profile {
        H2, MYSQL
    }
}
