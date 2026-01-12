package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public class CryptoWallet extends Account{


    private String walletAddress;
    private String blockchain;

    public CryptoWallet(String walletAddress, String blockchain, String id, String label, Currency currency, double balance) {
        super(id, label, currency, balance);
        this.walletAddress = walletAddress;
        this.blockchain = blockchain;

    }


    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String id) {
        this.blockchain = blockchain;
    }
}
