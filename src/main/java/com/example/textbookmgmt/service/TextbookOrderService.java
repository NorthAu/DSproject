package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.TextbookOrderDao;
import com.example.textbookmgmt.entity.TextbookOrder;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TextbookOrderService {
    private final TextbookOrderDao dao;

    public TextbookOrderService(TextbookOrderDao dao) {
        this.dao = dao;
    }

    public TextbookOrder create(TextbookOrder order) {
        validate(order);
        try {
            return dao.save(order);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存订购信息", e);
        }
    }

    public List<TextbookOrder> list() {
        try {
            return dao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("无法查询订购记录", e);
        }
    }

    private void validate(TextbookOrder order) {
        if (order.getTextbookId() == null) {
            throw new IllegalArgumentException("请选择教材");
        }
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("订购数量必须大于 0");
        }
        if (order.getArrivedQuantity() < 0) {
            throw new IllegalArgumentException("到货数量不能为负");
        }
        if (order.getOrderedDate() == null) {
            order.setOrderedDate(LocalDate.now());
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("已下单");
        }
    }
}
