package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.TextbookDao;
import com.example.textbookmgmt.entity.Textbook;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TextbookService {
    private final TextbookDao textbookDao;

    public TextbookService(TextbookDao textbookDao) {
        this.textbookDao = textbookDao;
    }

    public Textbook add(Textbook textbook) {
        try {
            return textbookDao.save(textbook);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存教材", e);
        }
    }

    public Textbook update(Textbook textbook) {
        try {
            return textbookDao.update(textbook);
        } catch (SQLException e) {
            throw new RuntimeException("无法更新教材", e);
        }
    }

    public void delete(long id) {
        try {
            textbookDao.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("无法删除教材", e);
        }
    }

    public List<Textbook> list() {
        try {
            return textbookDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("无法查询教材列表", e);
        }
    }

    public Optional<Textbook> findById(long id) {
        try {
            return textbookDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("无法按ID查询教材", e);
        }
    }
}
