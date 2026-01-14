package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.accounts.Account;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.util.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradingService {

    // BUY
    public Transaction buy(Portfolio portfolio, Account brokerAccount, Asset asset, double quantity, double unitPrice) {
        if (portfolio == null) throw new IllegalArgumentException("Portfolio is null");
        if (brokerAccount == null) throw new IllegalArgumentException("Broker account is null");
        if (asset == null) throw new IllegalArgumentException("Asset is null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice <= 0) throw new IllegalArgumentException("Unit price must be positive");

        double cost = quantity * unitPrice;

        brokerAccount.withdrawMoney(cost);

        Transaction tx = Transaction.trade(
                UUID.randomUUID().toString(),
                TransactionType.BUY,
                asset,
                quantity,
                unitPrice,
                LocalDateTime.now()
        );

        portfolio.apply(tx);
        return tx;
    }

    // SELL
    public Transaction sell(Portfolio portfolio, Account brokerAccount, Asset asset, double quantity, double unitPrice) {
        if (portfolio == null) throw new IllegalArgumentException("Portfolio is null");
        if (brokerAccount == null) throw new IllegalArgumentException("Broker account is null");
        if (asset == null) throw new IllegalArgumentException("Asset is null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice <= 0) throw new IllegalArgumentException("Unit price must be positive");

        double proceeds = quantity * unitPrice;

        Transaction tx = Transaction.trade(
                UUID.randomUUID().toString(),
                TransactionType.SELL,
                asset,
                quantity,
                unitPrice,
                LocalDateTime.now()
        );

        portfolio.apply(tx);

        brokerAccount.depositMoney(proceeds);

        return tx;
    }
}