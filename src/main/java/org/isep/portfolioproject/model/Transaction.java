package org.isep.portfolioproject.model;

import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.util.TransactionType;

import java.time.LocalDateTime;

public class Transaction {

    private String id;
    private LocalDateTime date;
    private TransactionType type;

    // BUY/SELL
    private Asset asset;
    private double quantity;
    private double price;

    // DEPOSIT/WITHDRAW/TRANSFER
    private double amount;

    public Transaction() {
    }

    // BUY/SELL constructor
    public static Transaction trade(String id, TransactionType type, Asset asset, double quantity, double price, LocalDateTime date) {
        if (type != TransactionType.BUY && type != TransactionType.SELL) {
            throw new IllegalArgumentException("Trade transaction must be BUY or SELL");
        }
        Transaction tx = new Transaction();
        tx.id = id;
        tx.type = type;
        tx.asset = asset;
        tx.quantity = quantity;
        tx.price = price;
        tx.date = date;
        return tx;
    }

    // DEPOSIT/WITHDRAW/TRANSFER constructor
    public static Transaction cash(String id, TransactionType type, double amount, LocalDateTime date) {
        if (type == TransactionType.BUY || type == TransactionType.SELL) {
            throw new IllegalArgumentException("Cash transaction cannot be BUY/SELL");
        }
        Transaction tx = new Transaction();
        tx.id = id;
        tx.type = type;
        tx.amount = amount;
        tx.date = date;
        return tx;
    }


    // GETTERS/SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double unitPrice) { this.price = unitPrice; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}