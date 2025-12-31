package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;
import java.util.List;

public class Portfolio {

    private String id;
    private String name;
    private String description;
    private boolean isThirdPartyMonitor;
    private List<Transaction> transactions;
    private List<Event> events;
    private List<Asset> assets;

    public Portfolio() {
    }

    public Portfolio(String id, String name, String description, boolean isThirdPartyMonitor, List<Transaction> transactions, List<Event> events, List<Asset> assets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isThirdPartyMonitor = isThirdPartyMonitor;
        this.transactions = transactions;
        this.events = events;
        this.assets = assets;
    }

    public void addTransaction(Transaction t) {
    }

    public double calculateTotalValue(Currency c) {
        return 0;
    }

    public Portfolio clone() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsThirdPartyMonitor() {
        return isThirdPartyMonitor;
    }

    public void setIsThirdPartyMonitor(boolean isThirdPartyMonitor) {
        this.isThirdPartyMonitor = isThirdPartyMonitor;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}