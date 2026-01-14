package org.isep.portfolioproject.model;

import org.isep.portfolioproject.service.ApiService;
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
        ApiService api = new ApiService();
        double price = api.getCryptoPrice(getSymbol(), c);
        return price * getQuantity();
    }

    @Override
    public Asset copyWithQuantity(double quantity) {
        return new Crypto(getSymbol(), quantity, blockchainAddress);
    }

    public String getBlockchainAddress() {
        return blockchainAddress;
    }

    public void setBlockchainAddress(String blockchainAddress) {
        this.blockchainAddress = blockchainAddress;
    }
}