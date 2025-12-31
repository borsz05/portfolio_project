package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public abstract class Asset {

    protected String symbol;
    protected double quantity;

    public Asset() {
    }

    public Asset(String symbol, double quantity) {
        this.symbol = symbol;
        this.quantity = quantity;
    }

    public abstract double getCurrentValue(Currency c);

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}