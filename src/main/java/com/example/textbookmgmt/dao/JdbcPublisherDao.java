package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.Publisher;
import com.example.textbookmgmt.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcPublisherDao implements PublisherDao {
    @Override
    public Publisher save(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO publishers(name, contact) VALUES (?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, publisher.getName());
            ps.setString(2, publisher.getContact());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    publisher.setId(keys.getLong(1));
                }
            }
            return publisher;
        }
    }

    @Override
    public Publisher update(Publisher publisher) throws SQLException {
        String sql = "UPDATE publishers SET name=?, contact=? WHERE id=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, publisher.getName());
            ps.setString(2, publisher.getContact());
            ps.setLong(3, publisher.getId());
            ps.executeUpdate();
            return publisher;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM publishers WHERE id=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Publisher> findAll() throws SQLException {
        String sql = "SELECT id, name, contact FROM publishers ORDER BY id";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Publisher> list = new ArrayList<>();
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(rs.getLong("id"));
                publisher.setName(rs.getString("name"));
                publisher.setContact(rs.getString("contact"));
                list.add(publisher);
            }
            return list;
        }
    }
}
