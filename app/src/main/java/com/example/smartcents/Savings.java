package com.example.smartcents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Savings implements Serializable {
    private String name;
    private String category;
    private double goalAmount;
    private double currentAmount;

    private List<Transaction> transactions;

    public Savings(String name, String category, double goalAmount, double currentAmount) {
        this.name = name;
        this.category = category;
        this.goalAmount = goalAmount;
        this.currentAmount = currentAmount;
        this.transactions = new ArrayList<Transaction>();
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter and Setter for goalAmount
    public double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(double goalAmount) {
        this.goalAmount = goalAmount;
    }

    // Getter and Setter for currentAmount
    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
