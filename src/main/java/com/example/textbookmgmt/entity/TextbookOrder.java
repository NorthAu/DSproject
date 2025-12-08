package com.example.textbookmgmt.entity;

import java.time.LocalDate;

public class TextbookOrder {
    private Long id;
    private Long textbookId;
    private int quantity;
    private int arrivedQuantity;
    private String status;
    private LocalDate orderedDate;
    private LocalDate arrivalDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTextbookId() {
        return textbookId;
    }

    public void setTextbookId(Long textbookId) {
        this.textbookId = textbookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getArrivedQuantity() {
        return arrivedQuantity;
    }

    public void setArrivedQuantity(int arrivedQuantity) {
        this.arrivedQuantity = arrivedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(LocalDate orderedDate) {
        this.orderedDate = orderedDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
