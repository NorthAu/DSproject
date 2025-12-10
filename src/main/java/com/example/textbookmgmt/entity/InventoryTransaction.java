package com.example.textbookmgmt.entity;

import java.time.LocalDateTime;

public class InventoryTransaction {
    private Long id;
    private Long textbookId;
    private int quantity;
    private String direction;
    private String reason;
    private LocalDateTime occurredAt;

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}
