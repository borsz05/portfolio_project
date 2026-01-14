package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.User;
import org.isep.portfolioproject.model.accounts.Account;
import org.isep.portfolioproject.model.assets.Asset;

public class UserService {

    private final TradingService tradingService;

    public UserService(TradingService tradingService) {
        if (tradingService == null) throw new IllegalArgumentException("TradingService is null");
        this.tradingService = tradingService;
    }

    public Portfolio getPortfolioById(User user, String portfolioId) {
        if (user == null) throw new IllegalArgumentException("User is null");
        if (portfolioId == null) {
            throw new IllegalArgumentException("Portfolio id is empty");
        }

        for (Portfolio p : user.getPortfolios()) {
            if (portfolioId.equals(p.getId())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
    }

    public Transaction buy(User user, String portfolioId, Account brokerAccount, Asset asset, double quantity, double unitPrice) {
        Portfolio portfolio = getPortfolioById(user, portfolioId);
        return tradingService.buy(portfolio, brokerAccount, asset, quantity, unitPrice);
    }

    public Transaction sell(User user, String portfolioId, Account brokerAccount, Asset asset, double quantity, double unitPrice) {
        Portfolio portfolio = getPortfolioById(user, portfolioId);
        return tradingService.sell(portfolio, brokerAccount, asset, quantity, unitPrice);
    }
}
