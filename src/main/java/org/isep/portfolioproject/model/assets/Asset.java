package org.isep.portfolioproject.model.assets;

import java.util.Objects;

public abstract class Asset {

    //BTC, AAPL
    protected String symbol;
    //Bitcoin, Apple inc.
    protected String name;

    protected Asset(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public abstract boolean isDivisible();

    public String getSymbol() {
        return symbol;
    }
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return symbol.equals(asset.symbol);
    }

    // Asset objects are used as keys in a HashMap (Portfolio positions).
    // Overriding hashCode ensures that assets with the same symbol
    // are treated as the same key and not as separate positions.
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}