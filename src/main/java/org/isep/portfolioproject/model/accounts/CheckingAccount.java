package org.isep.portfolioproject.model.accounts;

import org.isep.portfolioproject.util.Currency;

public class CheckingAccount extends Account {
    private String iban;
    private String bankName;


    public CheckingAccount(String iban, String bankName, String id, String label, Currency currency, double balance) {
        super(id, label, currency, balance);
        this.iban = iban;
        this.bankName = bankName;

    }

    //this method is to move/transfer money by using withdraw method to take out
    //an amount from the source account and then using depositmethod into the target account
    //after validatation
    public void transferTo (Account destination, double amount) {


        //if there is no destination account, there will be no transfer
        if (destination == null) {
            return;
    }

        //if the amount is equal or less than zero, there will be no transfer
        if (amount <= 0) {
            return;
        }


        //then the money is taken from the this account (the one initiating the transfer) and them remove
        //before giving it to another account. this has behavior from the
        //withdraw method which throws exception if there is not enough money
        this.withdrawMoney(amount);

        //when it passes all the checks the amount of money passed is transferred to the destination accoumt
        destination.depositMoney(amount);

    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
