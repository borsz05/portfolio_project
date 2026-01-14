package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.accounts.Account;

public class MoneyService {
    public void transfer(Account from, Account to, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        if (from.getCurrency() != to.getCurrency()) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        from.withdrawMoney(amount);
        to.depositMoney(amount);
    }
}
