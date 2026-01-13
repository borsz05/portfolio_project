package org.isep.portfolioproject.model;


//sources:

import org.isep.portfolioproject.util.Currency;
//https://stackoverflow.com/questions/66851361/adding-money-from-one-account-to-another-using-multithreading
//https://www.youtube.com/watch?v=hYjqZHs5U6M&list=PLuji25yj7oIJ5JUNOtgMZQJNWEBew7Tdo
//https://www.youtube.com/watch?v=49bIIa6id08&t=644s
//https://www.youtube.com/watch?v=EhOzXZrgOLI


public abstract class Account {


    //attributes that identify the account hold the value of how much money
    //is currently in account
    private String id;
    private String label;
    private Currency currency;
    private double balance;



    //constructor
    public Account(String id, String label, Currency currency, double balance) {
        this.id = id;
        this.currency = currency;
        this.balance = balance;
        this.label = label;
    }


    //a method to deposit money into the account
    //checks if the amount being deposited is positive, and adds the amount to balance
    //(example account.depositMoney(100);
    public void depositMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be + ");
        }
        balance += amount;
    }

    //this method is to withdraw money from the acocunt
    //first it checks in the the deposit is more than 0, you can only withdraw over 0
    public void withdrawMoney (double amount) {
        if (amount < 0 ) {
            throw new IllegalArgumentException("Amount must be +");
        }

        //then it checks that there is enough money in this account
        //if the balance is less than what you want to withdraw, if will not let you
        if (amount > balance) {
            throw new IllegalArgumentException("Not enough balance");
        }

        //lastely if both if tests pass, the amount will be subtracted from the balance
        balance -= amount;
    }



    public double getBalance() {
        return balance;
    }


    //source:
    //https://medium.com/@kiana.proudmoore/understanding-java-oop-in-a-real-world-banking-system-de4192848b52
    protected void setBalance(double balance) {
        this.balance = balance;
    }


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
}

