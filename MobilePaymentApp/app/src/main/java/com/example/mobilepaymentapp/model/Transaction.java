package com.example.mobilepaymentapp.model;

import java.util.Date;

public class Transaction {
    private String id;
    private String description;
    private double amount;
    private Date date;
    private String type; // e.g., "DEBIT", "CREDIT", "TRANSFER_OUT", "TRANSFER_IN", "PAYMENT"
    private String status; // e.g., "COMPLETED", "PENDING", "FAILED"
    private String merchantName; // Optional, can be part of description

    // Constructor
    public Transaction(String id, String description, double amount, Date date, String type, String status, String merchantName) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.status = status;
        this.merchantName = merchantName;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getMerchantName() {
        return merchantName;
    }

    // Setters (optional, depending on whether transactions are mutable after creation)
    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
