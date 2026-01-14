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

    // GETTER/SETTER
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
