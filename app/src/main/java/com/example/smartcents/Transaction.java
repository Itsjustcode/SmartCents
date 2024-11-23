package com.example.smartcents;

import android.os.Build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Transaction {
    public enum Type{
        INCOME, EXPENSE
    }
    private Type type; // "income" or "expense"
    private String category; // E.g., "Food", "Salary", "Entertainment"
    private double amount; // Transaction amount
    private String date; // Date of the transaction
    private String notes; // Optional notes

    // Constructor
    public Transaction(Type type, String category, double amount, String date, String notes) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    // Getters and setters
    public Transaction.Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public LocalDate getDateAsLocalDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        } else {
            throw new UnsupportedOperationException("LocalDate is not supported on this Android version");
        }
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
