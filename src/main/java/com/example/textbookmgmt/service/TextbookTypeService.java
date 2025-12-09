package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.TextbookTypeDao;
import com.example.textbookmgmt.entity.TextbookType;

import java.sql.SQLException;
import java.util.List;

public class TextbookTypeService {
    private final TextbookTypeDao dao;

    public TextbookTypeService(TextbookTypeDao dao) {
        this.dao = dao;
    }

    public TextbookType save(TextbookType type) {
        validate(type);
        try {
            if (type.getId() == null) {
                return dao.save(type);
            }
            return dao.update(type);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存教材类型", e);
        }
    }

    public void delete(long id) {
        try {
            dao.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("无法删除教材类型", e);
        }
    }

    public List<TextbookType> list() {
        try {
            return dao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("无法查询教材类型", e);
        }
    }

    private void validate(TextbookType type) {
        if (type == null || type.getName() == null || type.getName().isBlank()) {
            throw new IllegalArgumentException("教材类别名称不能为空");
        }
    }
}
