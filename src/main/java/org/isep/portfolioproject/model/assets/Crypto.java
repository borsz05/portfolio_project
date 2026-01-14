package org.isep.portfolioproject.model.assets;

public class Crypto extends Asset {

    public Crypto(String symbol, String name) {
        super(symbol, name);
    }

    @Override
    public boolean isDivisible() {
        return true;
    }
}