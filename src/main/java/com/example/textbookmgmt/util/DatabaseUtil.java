package com.example.textbookmgmt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseUtil {
    private static final String MYSQL_DEFAULT_URL = "jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC";
    private static final String MYSQL_DEFAULT_USER = "root";
    private static final String MYSQL_DEFAULT_PASSWORD = "";

    private static final String MYSQL_URL_ENV = "DB_URL";
    private static final String MYSQL_USER_ENV = "DB_USER";
    private static final String MYSQL_PASSWORD_ENV = "DB_PASSWORD";

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String url = resolveJdbcUrl();
        String user = resolveUser();
        String password = resolvePassword();
        return DriverManager.getConnection(url, user, password);
    }

    public static void initializeDatabase() {
        ensureDatabaseExists();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            createCoreTables(statement);
            createTriggers(statement);
            createStoredProcedures(statement);

            statement.executeUpdate("""
                    INSERT INTO publishers(name, contact)
                    SELECT '机械工业出版社', '010-88361066' WHERE NOT EXISTS (SELECT 1 FROM publishers)
                    """);
            statement.executeUpdate("""
                    INSERT INTO textbook_types(name, description)
                    SELECT '计算机科学', '编程、算法与软件工程类教材' WHERE NOT EXISTS (SELECT 1 FROM textbook_types)
                    """);
            statement.executeUpdate("""
                    INSERT INTO textbooks(title, author, publisher, publisher_id, type_id, isbn, stock, price)
                    SELECT 'Java 核心技术', 'Cay S. Horstmann', '机械工业出版社', 1, 1, 'ISBN9787111547', 12, 88.00
                    WHERE NOT EXISTS (SELECT 1 FROM textbooks)
                    """);
            statement.executeUpdate("""
                    INSERT INTO textbook_orders(textbook_id, quantity, status, ordered_date, arrived_quantity)
                    SELECT 1, 20, '已下单', CURRENT_DATE, 0 WHERE NOT EXISTS (SELECT 1 FROM textbook_orders)
                    """);
            statement.executeUpdate("""
                    INSERT INTO inventory_transactions(textbook_id, quantity, direction, reason, occurred_at)
                    SELECT 1, 5, 'IN', '样书入库', CURRENT_TIMESTAMP
                    WHERE NOT EXISTS (SELECT 1 FROM inventory_transactions)
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库失败", e);
        }
    }

    private static void ensureDatabaseExists() {
        String urlWithDb = resolveJdbcUrl();
        String user = resolveUser();
        String password = resolvePassword();
        String dbName = extractDatabaseName(urlWithDb);
        String adminUrl = buildAdminUrl(urlWithDb);

        try (Connection connection = DriverManager.getConnection(adminUrl, user, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` DEFAULT CHARACTER SET utf8mb4");
        } catch (SQLException e) {
            throw new RuntimeException("无法创建或访问 MySQL 数据库: " + dbName, e);
        }
    }

    private static String extractDatabaseName(String urlWithDb) {
        int paramsIndex = urlWithDb.indexOf('?');
        int end = paramsIndex >= 0 ? paramsIndex : urlWithDb.length();
        int lastSlash = urlWithDb.lastIndexOf('/', end);
        if (lastSlash == -1 || lastSlash + 1 >= end) {
            return "TextBookManager";
        }
        return urlWithDb.substring(lastSlash + 1, end);
    }

    private static String buildAdminUrl(String urlWithDb) {
        int paramsIndex = urlWithDb.indexOf('?');
        int end = paramsIndex >= 0 ? paramsIndex : urlWithDb.length();
        int lastSlash = urlWithDb.lastIndexOf('/', end);
        if (lastSlash == -1) {
            return urlWithDb;
        }
        String base = urlWithDb.substring(0, lastSlash + 1);
        if (paramsIndex >= 0) {
            base = base + urlWithDb.substring(paramsIndex);
        }
        return base;
    }

    private static void createCoreTables(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS publishers (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    contact VARCHAR(255)
                )
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS textbook_types (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    description VARCHAR(500)
                )
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS textbooks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    publisher VARCHAR(255) NOT NULL,
                    publisher_id BIGINT,
                    type_id BIGINT,
                    isbn VARCHAR(50) NOT NULL,
                    stock INT NOT NULL CHECK (stock >= 0),
                    price DECIMAL(10,2) NOT NULL DEFAULT 0,
                    CONSTRAINT chk_isbn_format CHECK (isbn LIKE 'ISBN__________'),
                    CONSTRAINT fk_textbooks_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL,
                    CONSTRAINT fk_textbooks_type FOREIGN KEY (type_id) REFERENCES textbook_types(id) ON DELETE SET NULL
                )
                """);

        statement.executeUpdate("ALTER TABLE textbooks ADD COLUMN IF NOT EXISTS price DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER stock");

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS textbook_orders (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    textbook_id BIGINT NOT NULL,
                    quantity INT NOT NULL,
                    arrived_quantity INT DEFAULT 0,
                    status VARCHAR(50) NOT NULL,
                    ordered_date DATE,
                    arrival_date DATE,
                    CONSTRAINT fk_orders_textbook FOREIGN KEY (textbook_id) REFERENCES textbooks(id) ON DELETE CASCADE
                )
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS inventory_transactions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    textbook_id BIGINT NOT NULL,
                    quantity INT NOT NULL,
                    direction VARCHAR(10) NOT NULL,
                    reason VARCHAR(255),
                    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_inventory_textbook FOREIGN KEY (textbook_id) REFERENCES textbooks(id) ON DELETE CASCADE
                )
                """);
    }

    private static void createTriggers(Statement statement) throws SQLException {
        statement.executeUpdate("DROP TRIGGER IF EXISTS trg_inventory_insert");
        statement.executeUpdate("""
                CREATE TRIGGER trg_inventory_insert
                AFTER INSERT ON inventory_transactions
                FOR EACH ROW
                BEGIN
                    UPDATE textbooks
                    SET stock = stock + CASE WHEN NEW.direction = 'IN' THEN NEW.quantity ELSE -NEW.quantity END
                    WHERE id = NEW.textbook_id;
                END
                """);
    }

    private static void createStoredProcedures(Statement statement) throws SQLException {
        statement.executeUpdate("DROP PROCEDURE IF EXISTS sp_textbook_stats");
        statement.executeUpdate("""
                CREATE PROCEDURE sp_textbook_stats()
                BEGIN
                    SELECT t.id,
                           t.title,
                           COALESCE(SUM(o.quantity), 0) AS total_ordered,
                           COALESCE(SUM(CASE WHEN it.direction = 'IN' THEN it.quantity END), 0) AS total_received,
                           COALESCE(SUM(CASE WHEN it.direction = 'OUT' THEN it.quantity END), 0) AS total_issued
                    FROM textbooks t
                    LEFT JOIN textbook_orders o ON o.textbook_id = t.id
                    LEFT JOIN inventory_transactions it ON it.textbook_id = t.id
                    GROUP BY t.id, t.title;
                END
                """);
    }

    private static String resolveJdbcUrl() {
        return System.getProperty(MYSQL_URL_ENV,
                System.getenv().getOrDefault(MYSQL_URL_ENV, MYSQL_DEFAULT_URL));
    }

    private static String resolveUser() {
        return System.getProperty(MYSQL_USER_ENV,
                System.getenv().getOrDefault(MYSQL_USER_ENV, MYSQL_DEFAULT_USER));
    }

    private static String resolvePassword() {
        return System.getProperty(MYSQL_PASSWORD_ENV,
                System.getenv().getOrDefault(MYSQL_PASSWORD_ENV, MYSQL_DEFAULT_PASSWORD));
    }
}
