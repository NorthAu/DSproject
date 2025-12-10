package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.Textbook;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TextbookDao {
    Textbook save(Textbook textbook) throws SQLException;

    Textbook update(Textbook textbook) throws SQLException;

    void delete(long id) throws SQLException;

    Optional<Textbook> findById(long id) throws SQLException;

    Optional<Textbook> findByIsbn(String isbn) throws SQLException;

    List<Textbook> findAll() throws SQLException;
}
