package org.isep.portfolioproject.service;

import org.isep.portfolioproject.util.Currency;

public interface PriceProvider {

    double getStockPrice(String sym, Currency c);
    double getCryptoPrice(String sym, Currency c);
}