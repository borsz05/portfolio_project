package org.isep.portfolioproject.model;

public class SavingAccount extends BankAccount {

    private static final int MAX = 3;
    private int counterWithdrawel = 0;

    public SavingAccount(String iban, double balance) {
        super(iban, balance);
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
