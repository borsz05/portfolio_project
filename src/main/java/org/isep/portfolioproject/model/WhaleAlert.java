package org.isep.portfolioproject.model;

import java.time.LocalDateTime;

public class WhaleAlert {

    private String blockchain;
    private double amount;
    private LocalDateTime timestamp;

    public WhaleAlert() {
    }

    public WhaleAlert(String blockchain, double amount, LocalDateTime timestamp) {
        this.blockchain = blockchain;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String when = timestamp == null ? "" : timestamp.toLocalDate().toString();
        return when + " " + blockchain + " " + String.format("%.2f", amount);
    }
}