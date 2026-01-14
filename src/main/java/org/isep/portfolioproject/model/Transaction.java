package org.isep.portfolioproject.model;

import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.util.TransactionType;
import java.time.LocalDate;

public class Transaction {

    private String id;
    private Asset asset;
    private double quantity;
    private double priceAtPurchase;
    private LocalDate date;
    private TransactionType type;

    public Transaction() {
    }

    public Transaction(String id, Asset asset, double quantity, double priceAtPurchase, LocalDate date, TransactionType type) {
        this.id = id;
        this.asset = asset;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.date = date;
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}