package org.isep.portfolioproject.model;

import org.isep.portfolioproject.service.ApiService;
import org.isep.portfolioproject.util.Currency;

public class Crypto extends Asset {

    public Crypto(String symbol, String name) {
        super(symbol, name);
    }

    @Override
    public boolean isDivisible() {
        return true;
    }
}