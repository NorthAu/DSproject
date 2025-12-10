package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.TextbookOrder;

import java.sql.SQLException;
import java.util.List;

public interface TextbookOrderDao {
    TextbookOrder save(TextbookOrder order) throws SQLException;

    List<TextbookOrder> findAll() throws SQLException;
}
