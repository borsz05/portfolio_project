package org.isep.portfolioproject.model.accounts;

import org.isep.portfolioproject.util.Currency;

import java.time.LocalDate;

public class SavingAccount extends Account {

    private static final int MAX_WITHDRAWALS_PER_YEAR = 3;

    private int withdrawalCountThisYear = 0;
    private int trackedYear = LocalDate.now().getYear();

    public SavingAccount(String id, String label, Currency currency, double balance) {
        super(id, label, currency, balance);
    }

    @Override
    public void withdrawMoney(double amount) {
        int currentYear = LocalDate.now().getYear();
        if (currentYear != trackedYear) {
            trackedYear = currentYear;
            withdrawalCountThisYear = 0;
        }

        if (withdrawalCountThisYear >= MAX_WITHDRAWALS_PER_YEAR) {
            throw new IllegalArgumentException("You have reached the maximum withdrawals for this year");
        }

        super.withdrawMoney(amount);
        withdrawalCountThisYear++;
    }

    public int getWithdrawalCountThisYear() {
        return withdrawalCountThisYear;
    }

    public int getMaxWithdrawalsPerYear() {
        return MAX_WITHDRAWALS_PER_YEAR;
    }
}
