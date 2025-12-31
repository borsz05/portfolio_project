package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.util.Currency;
import java.util.List;

public class ApiService implements PriceProvider {

    public ApiService() {
    }

    @Override
    public double getCurrentPrice(String sym, Currency c) {
        return 0.0;
    }

    public List<Transaction> importFromExchange(String exch) {
        return null;
    }
}