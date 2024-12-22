package com.example.smartcents;

import android.os.Build;

public class BudgetSettings {
    private String userName; //The name of the user
    private boolean weekly; //Is the budget set to weekly or monthly
    private double budget; //Set your budget amount
    private String budgetName; //Name the budget

    public BudgetSettings(String userName, boolean weekly, double budget, String budgetName) {
        this.userName = userName;
        this.weekly = weekly;
        this.budget = budget;
        this.budgetName = budgetName;
    }

    //Getters and setters
    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public double getAmount() {return budget;} //Get budget amount
    public void setAmount(double budget) {this.budget = budget;} //Set budget amount
    public String getBudgetName() {return budgetName;} //Get budget name
    public void setBudgetName(String budgetName) {this.budgetName = budgetName;} //Set budget name
    public boolean isWeekly() {return weekly;} //Return if budget is weekly or monthly
    public void setWeekly(boolean weekly) {this.weekly = weekly;} //Set whether budget is weekly or monthly
}
