package org.isep.portfolioproject.model;

public class CryptoWallet{

    private String id;
    private String label;
    private String walletAddress;
    private String blockchain;

    public CryptoWallet(String id, String label, String walletAddress, String blockchain) {
        this.id = id;
        this.label = label;
        this.walletAddress = walletAddress;
        this.blockchain = blockchain;
    }

    // GETTER/SETTER
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getBlockchain() {
        return blockchain;
    }
    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }
}
