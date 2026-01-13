package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public class BrokerAccount extends Account {

    private String brokerName;
    private String accountNumber;


    public BrokerAccount(String brokerName, String accountNumber, String id, String label, Currency currency, double balance) {
        super(id, label, currency, balance);
        this.brokerName = brokerName;
        this.accountNumber = accountNumber;

    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {this.brokerName = brokerName;}


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
