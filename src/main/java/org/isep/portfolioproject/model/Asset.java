package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public abstract class Asset {

    //BTC, AAPL
    protected String symbol;
    //Bitcoin, Apple inc.
    protected String name;

    protected Asset(String symbol, String name) {

    }

    public abstract boolean isDivisible();

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return symbol.equals(asset.symbol);
    }
}