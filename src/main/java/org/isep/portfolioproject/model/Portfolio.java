package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private String id;
    private String name;
    private String description;
    private boolean isThirdPartyMonitor;
    private List<Transaction> transactions = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Asset> assets = new ArrayList<>();

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


    //creating a method that handles one trade (a buy or a sell) and updates what you currenctly own (assets) and history of trades (transactions)
    public void addTransaction(Transaction t) {
        //first a if test is created to check if the transcation is actually there or valid, this if test
        //checks if the transaction received shows what asset and if it's buy or sell.
        //if it's not it will throw and show and error
        if (t == null || t.getAsset() == null || t.getType() == null) {
            throw new IllegalArgumentException("Invalid transaction");
        }

        //this if test checks if you are trying to buy or sell on a negative amount.
        //if your balance is less than or equal to 0 it will throw and give an error
        if (t.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        //getting the information of the asset that is stored inside the transaction
        Asset transactionAsset = t.getAsset();
        //a local variale used to store a reference to a matching asset in the portifolio, it's set to null as default (not found yet)
        Asset owenedAsset = null;


        //this for loop is looping through the current holdings of the portofolio
        //it checks each asset one by one
        for (Asset asset : assets ) {
           //checking if the current asset matches the asset from transaction
            //first it will compare symbol(example: BTC), ingoring case
            //then it will compare the class type (crypto or stock)
            //both conditions must be true for it to be consideres the same asset
            if (asset.getSymbol().equalsIgnoreCase(transactionAsset.getSymbol())
                && asset.getClass().equals(transactionAsset.getClass())) {

                //if a matching asset is found, a reference to it is stored
                owenedAsset = asset;

                //stopping the loop when the asset has been found
                break;
            }
        }


        //checking if the transaction is a BUY transaction
        if (t.getType() == TransactionType.BUY) {

            //if the asset is not already owned by the portfolio
            if(owenedAsset == null) {

                //if the asset is not cryptocurrency
                if(transactionAsset instanceof Crypto) {

                    //cast the asset to Crypto to access crypto-specific information
                    Crypto crypto = (Crypto) transactionAsset;

                    //create a new Crypto object and add it to the portfolio holdings
                    //the quantity is set to the amount bought in the transaction
                    assets.add(new Crypto(crypto.getSymbol(),t.getQuantity(), crypto.getBlockchainAddress()));

                    //if the asset is a stock
                } else if (transactionAsset instanceof Stock) {

                    //create a new Stock object and add it to the portfolio holding
                    assets.add(new Stock(transactionAsset.getSymbol(), t.getQuantity()));
                }

                //if the asset is already owned by the portfolio
            } else {

                //increase the quantity of the existing asset by adding the transaction quantity
                owenedAsset.setQuantity(owenedAsset.getQuantity() + t.getQuantity());
            }
        }


        //check if the transaction is a SELL
        if (t.getType() == TransactionType.SELL) {
            //if the asset is not owned, selling is made not allowed
            if (owenedAsset == null) {
                throw new IllegalArgumentException("Not enough to sell");
            }


            //decrease the quantity of the owned asset by the amount sold in the transaction
            owenedAsset.setQuantity(owenedAsset.getQuantity() - t.getQuantity());

        }
        //add the transaction to the transaction history
        transactions.add(t);
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