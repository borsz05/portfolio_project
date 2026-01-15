package org.isep.portfolioproject.model.accounts;

import org.isep.portfolioproject.util.Currency;
//https://stackoverflow.com/questions/66851361/adding-money-from-one-account-to-another-using-multithreading
//https://www.youtube.com/watch?v=hYjqZHs5U6M&list=PLuji25yj7oIJ5JUNOtgMZQJNWEBew7Tdo
//https://www.youtube.com/watch?v=49bIIa6id08&t=644s
//https://www.youtube.com/watch?v=EhOzXZrgOLI


public abstract class Account {

    private String id;
    private String label;
    private Currency currency;
    private double balance;

    public Account(String id, String label, Currency currency, double balance) {
        this.id = id;
        this.currency = currency;
        this.balance = balance;
        this.label = label;
    }

    public void depositMoney(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    public void withdrawMoney (double amount) {
        if (amount <= 0 ) throw new IllegalArgumentException("Amount must be +");
        if (amount > balance) throw new IllegalArgumentException("Not enough balance");
        balance -= amount;
    }


    // GETTER/SETTER
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return label;
    }
}

