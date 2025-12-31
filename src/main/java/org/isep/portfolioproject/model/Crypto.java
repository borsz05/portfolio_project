package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public class Crypto extends Asset {

    private String blockchainAddress;

    public Crypto() {
    }

    public Crypto(String symbol, double quantity, String blockchainAddress) {
        super(symbol, quantity);
        this.blockchainAddress = blockchainAddress;
    }

    @Override
    public double getCurrentValue(Currency c) {
        return 0;
    }

    public String getBlockchainAddress() {
        return blockchainAddress;
    }

    public void setBlockchainAddress(String blockchainAddress) {
        this.blockchainAddress = blockchainAddress;
    }
}