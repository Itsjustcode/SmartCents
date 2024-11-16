package com.example.smartcents;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private List<Transaction> transactions; //list to store all transactions

    public TransactionRepository() {
        transactions = new ArrayList<>();
    }

    //add a transaction
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    //retrieve all transactions
    public List<Transaction> getTransactions() {
        return transactions;
    }

    //get the total balance
    public double getBalance() {
        double balance = 0;
        for (Transaction transaction : transactions) {
            if ("income".equalsIgnoreCase(transaction.getType())) {
                balance += transaction.getAmount();
            } else if ("expense".equalsIgnoreCase(transaction.getType())) {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }
}
