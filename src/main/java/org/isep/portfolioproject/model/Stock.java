package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public class Stock extends Asset {

    public Stock() {
    }

    public Stock(String symbol, double quantity) {
        super(symbol, quantity);
    }

    @Override
    public double getCurrentValue(Currency c) {
        return 0;
    }
}