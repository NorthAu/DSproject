package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.TextbookType;

import java.sql.SQLException;
import java.util.List;

public interface TextbookTypeDao {
    TextbookType save(TextbookType type) throws SQLException;

    TextbookType update(TextbookType type) throws SQLException;

    void delete(long id) throws SQLException;

    List<TextbookType> findAll() throws SQLException;
}
