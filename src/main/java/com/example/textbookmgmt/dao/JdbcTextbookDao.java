package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTextbookDao implements TextbookDao {

    @Override
    public Textbook save(Textbook textbook) throws SQLException {
        String sql = "INSERT INTO textbooks(title, author, publisher, publisher_id, type_id, isbn, stock) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, textbook.getTitle());
            ps.setString(2, textbook.getAuthor());
            ps.setString(3, textbook.getPublisher());
            if (textbook.getPublisherId() == null) {
                ps.setNull(4, Types.BIGINT);
            } else {
                ps.setLong(4, textbook.getPublisherId());
            }
            if (textbook.getTypeId() == null) {
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setLong(5, textbook.getTypeId());
            }
            ps.setString(6, textbook.getIsbn());
            ps.setInt(7, textbook.getStock());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    textbook.setId(rs.getLong(1));
                }
            }
            return textbook;
        }
    }

    @Override
    public Textbook update(Textbook textbook) throws SQLException {
        String sql = "UPDATE textbooks SET title=?, author=?, publisher=?, publisher_id=?, type_id=?, isbn=?, stock=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, textbook.getTitle());
            ps.setString(2, textbook.getAuthor());
            ps.setString(3, textbook.getPublisher());
            if (textbook.getPublisherId() == null) {
                ps.setNull(4, Types.BIGINT);
            } else {
                ps.setLong(4, textbook.getPublisherId());
            }
            if (textbook.getTypeId() == null) {
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setLong(5, textbook.getTypeId());
            }
            ps.setString(6, textbook.getIsbn());
            ps.setInt(7, textbook.getStock());
            ps.setLong(8, textbook.getId());
            ps.executeUpdate();
            return textbook;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM textbooks WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Textbook> findById(long id) throws SQLException {
        String sql = baseSelectSql() + " WHERE t.id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowWithType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Textbook> findAll() throws SQLException {
        String sql = baseSelectSql() + " ORDER BY t.id DESC";
        List<Textbook> textbooks = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                textbooks.add(mapRowWithType(rs));
            }
        }
        return textbooks;
    }

    @Override
    public Optional<Textbook> findByIsbn(String isbn) throws SQLException {
        String sql = baseSelectSql() + " WHERE t.isbn = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowWithType(rs));
                }
            }
        }
        return Optional.empty();
    }

    private String baseSelectSql() {
        return "SELECT t.id, t.title, t.author, t.publisher, t.publisher_id, t.type_id, t.isbn, t.stock, tt.name AS type_name " +
                "FROM textbooks t " +
                "LEFT JOIN textbook_types tt ON t.type_id = tt.id";
    }

    private Textbook mapRow(ResultSet rs) throws SQLException {
        return new Textbook(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getObject("publisher_id", Long.class),
                rs.getObject("type_id", Long.class),
                rs.getString("isbn"),
                rs.getInt("stock")
        );
    }

    private Textbook mapRowWithType(ResultSet rs) throws SQLException {
        Textbook textbook = mapRow(rs);
        textbook.setTypeName(rs.getString("type_name"));
        return textbook;
    }
}
