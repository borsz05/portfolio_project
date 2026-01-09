package org.isep.portfolioproject.model;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

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
                assets.add(transactionAsset.copyWithQuantity(t.getQuantity()));
            } else {
                owenedAsset.buy(t.getQuantity());
            }
        }


        //check if the transaction is a SELL
        if (t.getType() == TransactionType.SELL) {
            //if the asset is not owned, selling is made not allowed
            if (owenedAsset == null) {
                throw new IllegalArgumentException("Asset not owned");
            }

            if (owenedAsset.getQuantity() < t.getQuantity()) {
                throw new IllegalArgumentException("Not enough quantity to sell");
            }

            //decrease the quantity of the owned asset by the amount sold in the transaction
            owenedAsset.setQuantity(owenedAsset.getQuantity() - t.getQuantity());


            if (owenedAsset.getQuantity() == 0 ) {
                removeAsset(owenedAsset);
            }
        }
        //add the transaction to the transaction history
        transactions.add(t);
    }


    //this method calculates the total value of the portfolio and returns a double
    //the parameter currency c represents the currency the value should be calculated in
    public double calculateTotalValue(Currency c) {
        //a variael total is created to store the sum of all assets
        //it starts at 0.0 because no assets have been added
        double total = 0.0;

        //a for each loop is used to go through each asset in the portfolio,
        //assets in the list that contains all owned assets
        for (Asset asset : assets) {

            //for each asset, it get's current value in the given currency and adds that value to the total
            total += asset.getCurrentValue(c);
        }

        //after all the assets have been looped through the method returns the total value of the portofolio
        return total;
    }


    //https://docs.oracle.com/javase/tutorial/collections/interfaces/list.html
    //https://www.geeksforgeeks.org/java/java-program-to-show-shallow-cloning-and-deep-cloning/
    //https://stackoverflow.com/questions/52386697/does-copy-constructor-makes-a-shallow-copy
    //aiming to copy a current portfolio object but creating a copy in own object
    //the purpose is to be able to test, simulate, see changes and run calculations on the object without changing the original portfolio
    public Portfolio copyObject() {

        //creating a new portfolio object which will be the copy of the current portfolio
        Portfolio copy = new Portfolio();

        //copying the basic information from the original
        copy.setId(this.id);
        copy.setName(this.name);
        copy.setDescription(this.description);
        copy.setIsThirdPartyMonitor(this.isThirdPartyMonitor);

        //creating a new list of assets, the list can be coped but the asset objects inside are shared
        copy.setAssets(new ArrayList<>(this.assets));

        //creating a new list of transactions
        copy.setTransactions(new ArrayList<>(this.transactions));

        //Creating a new list of events
        copy.setEvents(new ArrayList<>(this.events));
        return copy;
    }

    public void removeAsset(Asset asset) {
        if (asset == null) {
            return;
        }

       for (int i = 0; i < assets.size(); i++) {
           Asset asset1 = assets.get(i);

           if (asset.getSymbol().equalsIgnoreCase(asset1.getSymbol()) && asset1.getClass().equals(asset.getClass())) {
               assets.remove(i);
               return;
           }
       }
    }

    public void writeToCsv() throws IOException {
        String fileName = "data/portfolio.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Portfolio name," + name + "\n");
            writer.write("Description," + description + "\n");
            writer.write("Third pard monitor" + isThirdPartyMonitor + "\n");
            writer.write("\n");

            writer.write("ASSETS /n");
            writer.write("Type, Symbol, Quantity\n");

                for (Asset asset : assets) {
                    writer.write(
                            asset.getClass().getSimpleName() + "," +
                                    asset.getSymbol() + "," +
                                    asset.getQuantity() + "n"
                    );
                }

                writer.write("\n");

                writer.write("TRANSACTIONS\n");
                writer.write("Type, Asset, Quantity\n");

                for (Transaction transaction : transactions) {
                    writer.write(transaction.getType() + "," + transaction.getAsset().getSymbol() + "," + transaction.getQuantity() + "\n");
                }

            System.out.println("Csv file created");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
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