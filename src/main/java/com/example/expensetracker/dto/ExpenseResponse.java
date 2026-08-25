package com.example.expensetracker.dto;

import java.time.LocalDateTime;

public class ExpenseResponse {
    private Long id;
    private String title;
    private Double amount;
    private String category;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;
    public ExpenseResponse() {
    }

    public ExpenseResponse(Long id, String title, Double amount, String category, LocalDateTime createdDate,LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.createdDate=createdDate;
        this.updatedAt = updatedAt;
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
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
