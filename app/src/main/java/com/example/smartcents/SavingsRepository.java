package com.example.smartcents;

import java.util.ArrayList;
import java.util.List;

public class SavingsRepository {
    private static SavingsRepository instance;
    private List<Savings> savingsList;

    private SavingsRepository() {
        savingsList = new ArrayList<>();
        savingsList.add(new Savings("Emergency Fund", "Emergency", 1000.0, 200.0));
        savingsList.add(new Savings("Vacation Fund", "Vacation", 3000.0, 800.0));
    }

    public static SavingsRepository getInstance() {
        if (instance == null) {
            instance = new SavingsRepository();
        }
        return instance;
    }

    public List<Savings> getSavingsList() {
        return savingsList;
    }

    public void addSavings(Savings savings) {
        savingsList.add(savings);
    }

    public void addSavings(String savingsGoalName, Transaction transaction) {
        for (Savings savings : savingsList) {
            if (savings.getName().equalsIgnoreCase(savingsGoalName)) {
                savings.addTransaction(transaction);
                break;
            }
        }
    }



    public void removeSavings(Savings savings) {
        savingsList.remove(savings);
    }

    public double getTotalSavings() {
        double total = 0;
        for (Savings savings : savingsList) {
            total += savings.getCurrentAmount();

        }
        return total;
    }
}
