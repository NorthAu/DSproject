package com.example.textbookmgmt.entity;

import java.util.Objects;

public class Textbook {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private Long publisherId;
    private Long typeId;
    private String typeName;
    private String isbn;
    private int stock;

    public Textbook() {
    }

    public Textbook(Long id, String title, String author, String publisher, Long publisherId, Long typeId, String isbn, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publisherId = publisherId;
        this.typeId = typeId;
        this.isbn = isbn;
        this.stock = stock;
    }

    public Textbook(String title, String author, String publisher, String isbn, int stock) {
        this(null, title, author, publisher, null, null, isbn, stock);
    }

    public Textbook(Long id, String title, String author, String publisher, String isbn, int stock) {
        this(id, title, author, publisher, null, null, isbn, stock);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return title + " (" + isbn + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Textbook textbook = (Textbook) o;
        return Objects.equals(id, textbook.id) && Objects.equals(isbn, textbook.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }
}
