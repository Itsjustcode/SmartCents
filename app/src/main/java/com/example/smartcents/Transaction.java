package com.example.smartcents;

public class Transaction {
    private String type; // "income" or "expense"
    private String category; // E.g., "Food", "Salary", "Entertainment"
    private double amount; // Transaction amount
    private String date; // Date of the transaction
    private String notes; // Optional notes

    // Constructor
    public Transaction(String type, String category, double amount, String date, String notes) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
