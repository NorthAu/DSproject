package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.Publisher;

import java.sql.SQLException;
import java.util.List;

public interface PublisherDao {
    Publisher save(Publisher publisher) throws SQLException;

    Publisher update(Publisher publisher) throws SQLException;

    void delete(long id) throws SQLException;

    List<Publisher> findAll() throws SQLException;
}
