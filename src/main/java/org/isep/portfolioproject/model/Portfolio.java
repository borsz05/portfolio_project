package org.isep.portfolioproject.model;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.service.PriceProvider;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.Map;

public class Portfolio {

    private String id;
    private String name;
    private String description;
    private boolean isThirdPartyMonitor;
    private Currency referenceCurrency;
    private List<String> monitoredAddresses = new ArrayList<>();
    private List<String> monitoredBlockchains = new ArrayList<>();

    private List<Transaction> transactions = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private Map<Asset, Position> positions = new HashMap<>();

    public Portfolio() {
    }

    public Portfolio(String id, String name, String description, boolean isThirdPartyMonitor, Currency referenceCurrency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isThirdPartyMonitor = isThirdPartyMonitor;
        this.referenceCurrency = referenceCurrency;
    }

    public Position getOrCreatePosition(Asset asset) {
        // If the asset does not yet exist in the portfolio,
        // this creates a new Position for it and store it in the map.
        // https://docs.oracle.com/javase/8/docs/api/java/util/Map.html#computeIfAbsent-K-java.util.function.Function-
        return positions.computeIfAbsent(asset, Position::new);
    }

    //creating a method that handles one trade (a buy or a sell) and updates what you currenctly own (assets) and history of trades (transactions)
    public void apply(Transaction tx) {
        if (tx == null) throw new IllegalArgumentException("Transaction is null");

        if (tx.getType() == TransactionType.BUY || tx.getType() == TransactionType.SELL) {
            Asset asset = tx.getAsset();
            if (asset == null) throw new IllegalArgumentException("Asset missing");
            if (tx.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");

            Position position = getOrCreatePosition(asset);

            if (tx.getType() == TransactionType.BUY) {
                position.applyBuy(tx.getQuantity(), tx.getPrice());
            } else {
                position.applySell(tx.getQuantity());
                if (position.getQuantity() == 0) {
                    positions.remove(asset);
                }
            }
        }

        transactions.add(tx);
    }

    //this method calculates the total value of the portfolio and returns a double
    //the parameter currency c represents the currency the value should be calculated in
    public double calculateTotalValue(Currency c, PriceProvider priceProvider) {
        //a variael total is created to store the sum of all assets
        //it starts at 0.0 because no assets have been added
        double total = 0.0;

        for (Position position : positions.values()) {
            Asset asset = position.getAsset();

            double price;
            if (asset instanceof Stock)
                price = priceProvider.getStockPrice(asset.getSymbol(), c);
            else
                price = priceProvider.getCryptoPrice(asset.getSymbol(), c);

            total += position.getQuantity() * price;
        }

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
        copy.setReferenceCurrency(this.referenceCurrency);
        copy.setMonitoredAddresses(new ArrayList<>(this.monitoredAddresses));
        copy.setMonitoredBlockchains(new ArrayList<>(this.monitoredBlockchains));

        copy.setTransactions(new ArrayList<>(this.transactions));
        copy.setEvents(new ArrayList<>(this.events));

        Map<Asset, Position> positionsCopy = new HashMap<>();

        for (Asset asset : this.positions.keySet()) {

            Position original = this.positions.get(asset);

            Position cloned = new Position(asset);

            if (original.getQuantity() > 0) {
                cloned.applyBuy(original.getQuantity(), original.getAvgBuyPrice());
            }

            positionsCopy.put(asset, cloned);
        }

        copy.setPositions(positionsCopy);

        return copy;
    }

    public void writeToCsv() throws IOException {
        String fileName = "data/portfolio.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Portfolio name," + name + "\n");
            writer.write("Description," + description + "\n");
            writer.write("Third party monitor," + isThirdPartyMonitor + "\n");
            writer.write("Reference currency," + referenceCurrency + "\n");
            writer.write("\n");

            writer.write("POSITIONS\n");
            writer.write("Type,Symbol,Quantity,AvgBuyPrice\n");

            for (Position p : positions.values()) {
                writer.write(
                        p.getAsset().getClass().getSimpleName() + "," +
                                p.getAsset().getSymbol() + "," +
                                p.getQuantity() + "," +
                                p.getAvgBuyPrice() + "\n"
                );
            }

            writer.write("\n");
            writer.write("TRANSACTIONS\n");
            writer.write("Type,Asset,Quantity,UnitPrice\n");

            for (Transaction tx : transactions) {
                String sym = (tx.getAsset() != null) ? tx.getAsset().getSymbol() : "";
                writer.write(
                        tx.getType() + "," +
                                sym + "," +
                                tx.getQuantity() + "," +
                                tx.getPrice() + "\n"
                );
            }

            System.out.println("Csv file created");
        }
    }

    public void addEvent(Event event) {
        if (event == null) return;
        events.add(event);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) return;
        transactions.add(transaction);
    }


    //SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsThirdPartyMonitor(boolean isThirdPartyMonitor) {
        this.isThirdPartyMonitor = isThirdPartyMonitor;
    }
    public void setReferenceCurrency(Currency referenceCurrency) { this.referenceCurrency = referenceCurrency; }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    public void setPositions(Map<Asset, Position> positions) {
        this.positions = positions;
    }
    public void setMonitoredAddresses(List<String> monitoredAddresses) {
        this.monitoredAddresses = monitoredAddresses;
    }
    public void setMonitoredBlockchains(List<String> monitoredBlockchains) {
        this.monitoredBlockchains = monitoredBlockchains;
    }


    //GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public boolean getIsThirdPartyMonitor() {
        return isThirdPartyMonitor;
    }
    public Currency getReferenceCurrency() { return referenceCurrency; }

    public List<Event> getEvents() {
        return events;
    }
    public List<String> getMonitoredAddresses() {
        return monitoredAddresses;
    }
    public List<String> getMonitoredBlockchains() {
        return monitoredBlockchains;
    }
    public Map<Asset, Position> getPositions() {
        return positions;
    }
    public List<Transaction> getTransactions(){
        return transactions;
    }

    @Override
    public String toString() {
        return name == null ? "Portfolio" : name;
    }
}
