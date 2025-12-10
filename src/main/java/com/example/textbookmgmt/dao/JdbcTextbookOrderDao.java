package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.TextbookOrder;
import com.example.textbookmgmt.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcTextbookOrderDao implements TextbookOrderDao {
    @Override
    public TextbookOrder save(TextbookOrder order) throws SQLException {
        String sql = "INSERT INTO textbook_orders(textbook_id, quantity, arrived_quantity, status, ordered_date, arrival_date)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getTextbookId());
            ps.setInt(2, order.getQuantity());
            ps.setInt(3, order.getArrivedQuantity());
            ps.setString(4, order.getStatus());
            ps.setDate(5, toDate(order.getOrderedDate()));
            ps.setDate(6, toDate(order.getArrivalDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
            return order;
        }
    }

    @Override
    public List<TextbookOrder> findAll() throws SQLException {
        String sql = "SELECT id, textbook_id, quantity, arrived_quantity, status, ordered_date, arrival_date"
                + " FROM textbook_orders ORDER BY ordered_date IS NULL, ordered_date DESC, id DESC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<TextbookOrder> list = new ArrayList<>();
            while (rs.next()) {
                TextbookOrder order = new TextbookOrder();
                order.setId(rs.getLong("id"));
                order.setTextbookId(rs.getLong("textbook_id"));
                order.setQuantity(rs.getInt("quantity"));
                order.setArrivedQuantity(rs.getInt("arrived_quantity"));
                order.setStatus(rs.getString("status"));
                order.setOrderedDate(toLocalDate(rs.getDate("ordered_date")));
                order.setArrivalDate(toLocalDate(rs.getDate("arrival_date")));
                list.add(order);
            }
            return list;
        }
    }

    private Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
