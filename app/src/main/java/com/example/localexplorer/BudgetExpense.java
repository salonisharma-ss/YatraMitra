package com.example.localexplorer;

public class BudgetExpense {
    private double amount;
    private String category;
    private String place;
    private String date;

    public BudgetExpense() {}

    public BudgetExpense(double amount, String category, String place, String date) {
        this.amount = amount;
        this.category = category;
        this.place = place;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}