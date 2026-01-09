package org.isep.portfolioproject.model;


//sources:

//https://www.youtube.com/watch?v=hYjqZHs5U6M&list=PLuji25yj7oIJ5JUNOtgMZQJNWEBew7Tdo
//https://www.youtube.com/watch?v=49bIIa6id08&t=644s
public class BankAccount {


    //attributes that identify the account hold the value of how much money
    //is currently in account
    private String iban;
    private double balance;



    //constructor
    public BankAccount(String iban, double balance) {
        this.iban = iban;
        this.balance = balance;
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

    public String getIban() {
        return iban;
    }

    public double getBalance() {
        return balance;
    }


    //source:
    //https://medium.com/@kiana.proudmoore/understanding-java-oop-in-a-real-world-banking-system-de4192848b52
    protected void setBalance(double balance) {
        this.balance = balance;
    }


}
