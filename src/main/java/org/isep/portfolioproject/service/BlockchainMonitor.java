package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.WhaleAlert;
import java.util.List;

public class BlockchainMonitor {

    private double whaleThreshold;

    public BlockchainMonitor() {
    }

    public BlockchainMonitor(double whaleThreshold) {
        this.whaleThreshold = whaleThreshold;
    }

    public List<WhaleAlert> scanForWhaleTransactions() {
        return null;
    }

    public double trackThirdPartyBalance(String addr) {
        return 0.0;
    }

    public double getWhaleThreshold() {
        return whaleThreshold;
    }

    public void setWhaleThreshold(double whaleThreshold) {
        this.whaleThreshold = whaleThreshold;
    }
}