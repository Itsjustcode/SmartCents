package com.example.smartcents;

import java.util.ArrayList;
import java.util.List;


public class TransactionRepository {

    // Singleton instance of the repository
    private static TransactionRepository instance;

    //list to store all transactions
    private List<Transaction> transactions;

    //private constructor to enforce the Singleton pattern
    private TransactionRepository() {
        transactions = new ArrayList<>(); //initialize the list of transactions
    }

    //retrieves the Singleton instance of the repository
    public static TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository(); //create a new instance if none exists
        }
        return instance; //return the existing instance
    }

    //adds a new transaction to the list
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    //returns the list of all stored transactions
    public List<Transaction> getTransactions() {
        return transactions;
    }

    //calculates and returns the total balance based on transactions
    public double getTotalIncome() {
        double totalIncome = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.Type.INCOME) {
                totalIncome += transaction.getAmount();
            }
        }
        return totalIncome;
    }

    public double getTotalExpenses() {
        double totalExpenses = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.Type.EXPENSE) {
                totalExpenses += transaction.getAmount();
            }
        }
        return totalExpenses;
    }
    public double getBalance() {
        return getTotalIncome() - getTotalExpenses();
    }

}
