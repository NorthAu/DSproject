package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.InventoryTransactionDao;
import com.example.textbookmgmt.dao.TextbookDao;
import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.entity.InventoryTransaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryService {
    private final InventoryTransactionDao dao;
    private final TextbookDao textbookDao;

    public InventoryService(InventoryTransactionDao dao, TextbookDao textbookDao) {
        this.dao = dao;
        this.textbookDao = textbookDao;
    }

    public InventoryTransaction record(InventoryTransaction transaction) {
        validate(transaction);
        try {
            guardAgainstStockExhaustion(transaction);
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

    private void guardAgainstStockExhaustion(InventoryTransaction transaction) throws SQLException {
        if (!"OUT".equals(transaction.getDirection())) {
            return;
        }
        Textbook textbook = textbookDao.findById(transaction.getTextbookId())
                .orElseThrow(() -> new IllegalArgumentException("教材不存在，请刷新后重试"));
        int remaining = textbook.getStock() - transaction.getQuantity();
        if (remaining <= 0) {
            throw new IllegalArgumentException("库存不足，此次出库后库存会降到 0，请调整数量");
        }
    }
}
