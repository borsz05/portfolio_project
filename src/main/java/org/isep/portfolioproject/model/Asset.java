package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;

public abstract class Asset {

    protected String symbol;
    protected double quantity;

    public Asset() {
    }


    public Asset(String symbol, double quantity) {
        this.symbol = symbol;
        this.quantity = quantity;
    }

    //this method increases the quantity of the asset when a BUY is made
    //it only handles quantity logic and basic valditation
    public void buy(double quantity) {

        //checks if the quantity to buy is ok
        //buying 0 or a negative amount will give error
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be postive");

        //increase the current quantity of the asset
        setQuantity(getQuantity() + quantity);
    }


    //this method decreases the quantity of the asset when a SELL is made
    //it can't sell more than it owns
    public void sell (double quantity) {

        //checks to see if the quantity to sell is valid
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be postive");
        if (getQuantity() < quantity) throw new IllegalArgumentException("Not enough quantity");
        setQuantity(getQuantity() - quantity);
    }

    public abstract Asset copyWithQuantity(double quantity);

    public abstract double getCurrentValue(Currency c);

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}