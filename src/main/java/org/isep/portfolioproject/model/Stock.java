package org.isep.portfolioproject.model;

public class Stock extends Asset {

    public Stock(String symbol, String name) {
        super(symbol, name);
    }

    @Override
    public boolean isDivisible() {
        return false;
    }
}