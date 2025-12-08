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
        String sql = "INSERT INTO textbooks(title, author, publisher, isbn, stock) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, textbook.getTitle());
            ps.setString(2, textbook.getAuthor());
            ps.setString(3, textbook.getPublisher());
            ps.setString(4, textbook.getIsbn());
            ps.setInt(5, textbook.getStock());
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
        String sql = "UPDATE textbooks SET title=?, author=?, publisher=?, isbn=?, stock=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, textbook.getTitle());
            ps.setString(2, textbook.getAuthor());
            ps.setString(3, textbook.getPublisher());
            ps.setString(4, textbook.getIsbn());
            ps.setInt(5, textbook.getStock());
            ps.setLong(6, textbook.getId());
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
        String sql = "SELECT id, title, author, publisher, isbn, stock FROM textbooks WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Textbook> findAll() throws SQLException {
        String sql = "SELECT id, title, author, publisher, isbn, stock FROM textbooks ORDER BY id DESC";
        List<Textbook> textbooks = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                textbooks.add(mapRow(rs));
            }
        }
        return textbooks;
    }

    private Textbook mapRow(ResultSet rs) throws SQLException {
        return new Textbook(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getString("isbn"),
                rs.getInt("stock")
        );
    }
}
