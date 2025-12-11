package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.InventoryTransaction;
import com.example.textbookmgmt.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcInventoryTransactionDao implements InventoryTransactionDao {
    @Override
    public InventoryTransaction save(InventoryTransaction transaction) throws SQLException {
        String sql = "INSERT INTO inventory_transactions(textbook_id, quantity, direction, reason, occurred_at)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, transaction.getTextbookId());
            ps.setInt(2, transaction.getQuantity());
            ps.setString(3, transaction.getDirection());
            ps.setString(4, transaction.getReason());
            ps.setTimestamp(5, toTimestamp(transaction.getOccurredAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setId(keys.getLong(1));
                }
            }
            return transaction;
        }
    }

    @Override
    public List<InventoryTransaction> findAll() throws SQLException {
        String sql = "SELECT id, textbook_id, quantity, direction, reason, occurred_at"
                + " FROM inventory_transactions ORDER BY occurred_at DESC, id DESC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<InventoryTransaction> list = new ArrayList<>();
            while (rs.next()) {
                InventoryTransaction tx = new InventoryTransaction();
                tx.setId(rs.getLong("id"));
                tx.setTextbookId(rs.getLong("textbook_id"));
                tx.setQuantity(rs.getInt("quantity"));
                tx.setDirection(rs.getString("direction"));
                tx.setReason(rs.getString("reason"));
                tx.setOccurredAt(toLocalDateTime(rs.getTimestamp("occurred_at")));
                list.add(tx);
            }
            return list;
        }
    }

    private Timestamp toTimestamp(LocalDateTime time) {
        return time == null ? null : Timestamp.valueOf(time);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
