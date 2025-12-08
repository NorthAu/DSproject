package com.example.textbookmgmt.service;

import com.example.textbookmgmt.dao.PublisherDao;
import com.example.textbookmgmt.entity.Publisher;

import java.sql.SQLException;
import java.util.List;

public class PublisherService {
    private final PublisherDao publisherDao;

    public PublisherService(PublisherDao publisherDao) {
        this.publisherDao = publisherDao;
    }

    public Publisher save(Publisher publisher) {
        validate(publisher);
        try {
            if (publisher.getId() == null) {
                return publisherDao.save(publisher);
            }
            return publisherDao.update(publisher);
        } catch (SQLException e) {
            throw new RuntimeException("无法保存出版社", e);
        }
    }

    public void delete(long id) {
        try {
            publisherDao.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("无法删除出版社", e);
        }
    }

    public List<Publisher> list() {
        try {
            return publisherDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("无法查询出版社", e);
        }
    }

    private void validate(Publisher publisher) {
        if (publisher == null || publisher.getName() == null || publisher.getName().isBlank()) {
            throw new IllegalArgumentException("出版社名称不能为空");
        }
    }
}
