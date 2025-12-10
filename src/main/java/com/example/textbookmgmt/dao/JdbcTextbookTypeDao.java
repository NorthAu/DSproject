package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.TextbookType;
import com.example.textbookmgmt.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTextbookTypeDao implements TextbookTypeDao {
    @Override
    public TextbookType save(TextbookType type) throws SQLException {
        String sql = "INSERT INTO textbook_types(name, description) VALUES (?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type.getName());
            ps.setString(2, type.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    type.setId(keys.getLong(1));
                }
            }
            return type;
        }
    }

    @Override
    public TextbookType update(TextbookType type) throws SQLException {
        String sql = "UPDATE textbook_types SET name=?, description=? WHERE id=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.getName());
            ps.setString(2, type.getDescription());
            ps.setLong(3, type.getId());
            ps.executeUpdate();
            return type;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM textbook_types WHERE id=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<TextbookType> findAll() throws SQLException {
        String sql = "SELECT id, name, description FROM textbook_types ORDER BY id";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<TextbookType> list = new ArrayList<>();
            while (rs.next()) {
                TextbookType type = new TextbookType();
                type.setId(rs.getLong("id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                list.add(type);
            }
            return list;
        }
    }
}
