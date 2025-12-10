package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.TextbookDao;
import com.example.textbookmgmt.entity.Textbook;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class TextbookService {
    private final TextbookDao textbookDao;
    private static final Pattern ISBN_PATTERN = Pattern.compile("^ISBN\\d{10}$");

    public TextbookService(TextbookDao textbookDao) {
        this.textbookDao = textbookDao;
    }

    public Textbook create(Textbook textbook) {
        validate(textbook);
        try {
            Optional<Textbook> existing = textbookDao.findByIsbn(textbook.getIsbn());
            if (existing.isPresent()) {
                Textbook merged = existing.get();
                merged.setTitle(textbook.getTitle());
                merged.setAuthor(textbook.getAuthor());
                merged.setPublisher(textbook.getPublisher());
                merged.setPublisherId(textbook.getPublisherId());
                merged.setTypeId(textbook.getTypeId());
                merged.setStock(Math.max(0, merged.getStock()) + textbook.getStock());
                return textbookDao.update(merged);
            }
            return textbookDao.save(textbook);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存教材", e);
        }
    }

    public Textbook update(Textbook textbook) {
        if (textbook.getId() == null) {
            throw new IllegalArgumentException("修改前请先选择要更新的教材");
        }
        validate(textbook);
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

    private void validate(Textbook textbook) {
        if (isBlank(textbook.getTitle()) || isBlank(textbook.getAuthor())
                || isBlank(textbook.getPublisher()) || isBlank(textbook.getIsbn())) {
            throw new IllegalArgumentException("书名、作者、出版社、ISBN 均不能为空");
        }
        if (textbook.getStock() < 0) {
            throw new IllegalArgumentException("库存必须大于等于 0");
        }
        if (!ISBN_PATTERN.matcher(textbook.getIsbn()).matches()) {
            throw new IllegalArgumentException("ISBN 格式必须以ISBN开头，后跟10位数字，例如：ISBN1234567890");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
