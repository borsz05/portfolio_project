package org.isep.portfolioproject.model;

import org.isep.portfolioproject.model.assets.Asset;

public class Position {

    private final Asset asset;
    private double quantity;
    private double avgBuyPrice;

    public Position(Asset asset) {
        this.asset = asset;
        this.quantity = 0.0;
        this.avgBuyPrice = 0.0;
    }

    public Asset getAsset() {
        return asset;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    //BUY
    public void applyBuy(double quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!asset.isDivisible() && quantity % 1 != 0) {
            throw new IllegalArgumentException("Stock quantity must be an integer");
        }

        double totalCostBefore = this.quantity * avgBuyPrice;
        double totalCostAfter = totalCostBefore + quantity * unitPrice;

        this.quantity += quantity;
        avgBuyPrice = totalCostAfter / this.quantity;
    }

    //SELL
    public void applySell(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!asset.isDivisible() && quantity % 1 != 0) {
            throw new IllegalArgumentException("Stock quantity must be an integer");
        }

        if (quantity > this.quantity) {
            throw new IllegalArgumentException("Not enough quantity to sell");
        }

        this.quantity -= quantity;

        if (this.quantity == 0) {
            avgBuyPrice = 0.0;
        }
    }
}
