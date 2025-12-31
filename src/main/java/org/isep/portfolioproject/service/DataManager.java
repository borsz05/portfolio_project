package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.Portfolio;
import java.util.List;

public class DataManager {

    private String storagePath;

    public DataManager() {
    }

    public DataManager(String storagePath) {
        this.storagePath = storagePath;
    }

    public void savePortfolios(List<Portfolio> list) {
    }

    public List<Portfolio> loadPortfolios(String passphrase) {
        return null;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}