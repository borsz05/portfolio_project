package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public class SavingAccount extends Account {

    private static final int MAX = 3;
    private int counterWithdrawel = 0;

    public SavingAccount(String id, String label, Currency currency, double balance) {
        super(id, label, currency, balance);
    }


    @Override
    public void withdrawMoney(double amount) {
        if (counterWithdrawel >= MAX) {
            throw new IllegalArgumentException(
                    "You have reached the maximum of withdrawels on this savingsaccount"
            );
        }

        super.withdrawMoney(amount);

        counterWithdrawel++;
    }

    public int getMax() {
        return getMax();
    }
}
