package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.InventoryTransactionDao;
import com.example.textbookmgmt.entity.InventoryTransaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryService {
    private final InventoryTransactionDao dao;

    public InventoryService(InventoryTransactionDao dao) {
        this.dao = dao;
    }

    public InventoryTransaction record(InventoryTransaction transaction) {
        validate(transaction);
        try {
            return dao.save(transaction);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存库存流水", e);
        }
    }

    public List<InventoryTransaction> list() {
        try {
            return dao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("无法查询库存流水", e);
        }
    }

    private void validate(InventoryTransaction transaction) {
        if (transaction.getTextbookId() == null) {
            throw new IllegalArgumentException("请选择教材");
        }
        if (transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量必须大于 0");
        }
        if (transaction.getDirection() == null || (!"IN".equals(transaction.getDirection()) && !"OUT".equals(transaction.getDirection()))) {
            throw new IllegalArgumentException("方向必须是 IN 或 OUT");
        }
        if (transaction.getOccurredAt() == null) {
            transaction.setOccurredAt(LocalDateTime.now());
        }
    }
}
