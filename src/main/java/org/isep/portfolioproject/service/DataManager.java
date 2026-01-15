package org.isep.portfolioproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.isep.portfolioproject.model.Event;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Position;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.model.assets.Crypto;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.EncryptionUtil;
import org.isep.portfolioproject.util.EventType;
import org.isep.portfolioproject.util.TransactionType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private String storagePath;
    private String passphrase;
    private boolean seedDemo = true;
  
    public DataManager() {
    }

    public DataManager(String storagePath) {
        this.storagePath = storagePath;
    }

    public void savePortfolios(List<Portfolio> list) {
        if (list == null) return;

        try {
            ObjectMapper mapper = new ObjectMapper();

            List<PortfolioData> payload = new ArrayList<>();
            for (Portfolio p : list) {
                payload.add(PortfolioData.fromPortfolio(p));
            }

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            byte[] content;
            if (passphrase != null && !passphrase.isBlank()) {
                content = EncryptionUtil.encrypt(json.getBytes(StandardCharsets.UTF_8), passphrase);
            } else {
                content = json.getBytes(StandardCharsets.UTF_8);
            }

            File out = new File(storagePath);
            if (out.getParentFile() != null) {
                out.getParentFile().mkdirs();
            }
            Files.write(out.toPath(), content);
        } catch (Exception e) {
            throw new RuntimeException("Could not save portfolios", e);
        }
    }

    public List<Portfolio> loadPortfolios(String passphrase) {
        this.passphrase = passphrase;
        File file = new File(storagePath);
        if (!file.exists() || file.length() == 0) {
            return seedDemo ? demoPortfolios() : new ArrayList<>();
        }

        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            byte[] jsonBytes;
            if (EncryptionUtil.isEncrypted(raw)) {
                if (passphrase == null || passphrase.isBlank()) {
                    throw new IllegalArgumentException("Passphrase required for encrypted data");
                }
                jsonBytes = EncryptionUtil.decrypt(raw, passphrase);
            } else {
                jsonBytes = raw;
            }

            ObjectMapper mapper = new ObjectMapper();
            PortfolioData[] data = mapper.readValue(jsonBytes, PortfolioData[].class);

            List<Portfolio> portfolios = new ArrayList<>();
            for (PortfolioData d : data) {
                portfolios.add(d.toPortfolio());
            }

            if (portfolios.isEmpty()) {
                return seedDemo ? demoPortfolios() : new ArrayList<>();
            }
            return portfolios;
        } catch (Exception e) {
            throw new RuntimeException("Could not load portfolios", e);
        }
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public boolean isEncrypted() {
        File file = new File(storagePath);
        if (!file.exists()) return false;
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            return EncryptionUtil.isEncrypted(raw);
        } catch (Exception e) {
            return false;
        }
    }

    public void setSeedDemo(boolean seedDemo) {
        this.seedDemo = seedDemo;
    }

    private List<Portfolio> demoPortfolios() {
        List<Portfolio> list = new ArrayList<>();

        Portfolio demo = new Portfolio(
                UUID.randomUUID().toString(),
                "Starter Portfolio",
                "Sample data loaded automatically",
                false,
                Currency.USD
        );

        demo.getMonitoredBlockchains().add("bitcoin");
        demo.getMonitoredBlockchains().add("ethereum");

        Transaction t1 = Transaction.trade(
                UUID.randomUUID().toString(),
                TransactionType.BUY,
                new Stock("AAPL", "Apple Inc."),
                3,
                175.0,
                LocalDateTime.now().minusDays(25)
        );
        Transaction t2 = Transaction.trade(
                UUID.randomUUID().toString(),
                TransactionType.BUY,
                new Crypto("BTC", "Bitcoin"),
                0.15,
                41000.0,
                LocalDateTime.now().minusDays(12)
        );

        demo.apply(t1);
        demo.apply(t2);

        demo.addEvent(new Event(
                UUID.randomUUID().toString(),
                "Market news",
                "Inflation report released",
                EventType.NEWS,
                LocalDateTime.now().minusDays(4),
                null
        ));

        list.add(demo);
        return list;
    }

    public static class PortfolioData {
        public String id;
        public String name;
        public String description;
        public boolean thirdPartyMonitor;
        public String referenceCurrency;
        public List<String> monitoredAddresses = new ArrayList<>();
        public List<String> monitoredBlockchains = new ArrayList<>();
        public List<PositionData> positions = new ArrayList<>();
        public List<TransactionData> transactions = new ArrayList<>();
        public List<EventData> events = new ArrayList<>();

        public static PortfolioData fromPortfolio(Portfolio p) {
            PortfolioData data = new PortfolioData();
            data.id = p.getId();
            data.name = p.getName();
            data.description = p.getDescription();
            data.thirdPartyMonitor = p.getIsThirdPartyMonitor();
            data.referenceCurrency = p.getReferenceCurrency() == null ? null : p.getReferenceCurrency().name();
            data.monitoredAddresses = new ArrayList<>(p.getMonitoredAddresses());
            data.monitoredBlockchains = new ArrayList<>(p.getMonitoredBlockchains());

            for (Position pos : p.getPositions().values()) {
                data.positions.add(PositionData.fromPosition(pos));
            }
            for (Transaction tx : p.getTransactions()) {
                data.transactions.add(TransactionData.fromTransaction(tx));
            }
            for (Event event : p.getEvents()) {
                data.events.add(EventData.fromEvent(event));
            }
            return data;
        }

        public Portfolio toPortfolio() {
            Currency currency = referenceCurrency == null ? Currency.USD : Currency.valueOf(referenceCurrency);
            Portfolio p = new Portfolio(id, name, description, thirdPartyMonitor, currency);
            p.setMonitoredAddresses(monitoredAddresses == null ? new ArrayList<>() : monitoredAddresses);
            p.setMonitoredBlockchains(monitoredBlockchains == null ? new ArrayList<>() : monitoredBlockchains);

            Map<String, Asset> assetIndex = new HashMap<>();
            Map<Asset, Position> positionMap = new HashMap<>();
            if (positions != null) {
                for (PositionData pos : positions) {
                    Asset asset = assetIndex.computeIfAbsent(pos.symbol, k -> pos.toAsset());
                    Position position = new Position(asset);
                    if (pos.quantity > 0) {
                        position.applyBuy(pos.quantity, pos.avgBuyPrice);
                    }
                    positionMap.put(asset, position);
                }
            }
            p.setPositions(positionMap);

            List<Transaction> txs = new ArrayList<>();
            if (transactions != null) {
                for (TransactionData tx : transactions) {
                    Asset asset = null;
                    if (tx.symbol != null && !tx.symbol.isBlank()) {
                        asset = assetIndex.computeIfAbsent(tx.symbol, k -> tx.toAsset());
                    }
                    txs.add(tx.toTransaction(asset));
                }
            }
            p.setTransactions(txs);

            List<Event> evs = new ArrayList<>();
            if (events != null) {
                for (EventData ev : events) {
                    evs.add(ev.toEvent());
                }
            }
            p.setEvents(evs);

            return p;
        }
    }

    public static class PositionData {
        public String assetType;
        public String symbol;
        public String name;
        public double quantity;
        public double avgBuyPrice;

        public static PositionData fromPosition(Position position) {
            PositionData data = new PositionData();
            data.assetType = position.getAsset() instanceof Crypto ? "CRYPTO" : "STOCK";
            data.symbol = position.getAsset().getSymbol();
            data.name = position.getAsset().getName();
            data.quantity = position.getQuantity();
            data.avgBuyPrice = position.getAvgBuyPrice();
            return data;
        }

        public Asset toAsset() {
            if ("CRYPTO".equalsIgnoreCase(assetType)) {
                return new Crypto(symbol, name);
            }
            return new Stock(symbol, name);
        }
    }

    public static class TransactionData {
        public String id;
        public String type;
        public String assetType;
        public String symbol;
        public String name;
        public double quantity;
        public double price;
        public double amount;
        public String timestamp;

        public static TransactionData fromTransaction(Transaction tx) {
            TransactionData data = new TransactionData();
            data.id = tx.getId();
            data.type = tx.getType() == null ? null : tx.getType().name();
            if (tx.getAsset() != null) {
                data.assetType = tx.getAsset() instanceof Crypto ? "CRYPTO" : "STOCK";
                data.symbol = tx.getAsset().getSymbol();
                data.name = tx.getAsset().getName();
            }
            data.quantity = tx.getQuantity();
            data.price = tx.getPrice();
            data.amount = tx.getAmount();
            data.timestamp = tx.getDate() == null ? null : tx.getDate().toString();
            return data;
        }

        public Asset toAsset() {
            if ("CRYPTO".equalsIgnoreCase(assetType)) {
                return new Crypto(symbol, name);
            }
            return new Stock(symbol, name);
        }

        public Transaction toTransaction(Asset asset) {
            TransactionType txType = type == null ? TransactionType.DEPOSIT : TransactionType.valueOf(type);
            LocalDateTime date = timestamp == null ? LocalDateTime.now() : LocalDateTime.parse(timestamp);
            if (txType == TransactionType.BUY || txType == TransactionType.SELL) {
                return Transaction.trade(id, txType, asset, quantity, price, date);
            }
            Transaction tx = Transaction.cash(id, txType, amount, date);
            tx.setAsset(asset);
            tx.setQuantity(quantity);
            tx.setPrice(price);
            return tx;
        }
    }

    public static class EventData {
        public String id;
        public String title;
        public String description;
        public String type;
        public String timestamp;
        public String portfolioId;

        public static EventData fromEvent(Event event) {
            EventData data = new EventData();
            data.id = event.getId();
            data.title = event.getTitle();
            data.description = event.getDescription();
            data.type = event.getType() == null ? null : event.getType().name();
            data.timestamp = event.getTimestamp() == null ? null : event.getTimestamp().toString();
            data.portfolioId = event.getPortfolioId();
            return data;
        }

        public Event toEvent() {
            EventType evType = type == null ? EventType.CUSTOM : EventType.valueOf(type);
            LocalDateTime date = timestamp == null ? LocalDateTime.now() : LocalDateTime.parse(timestamp);
            return new Event(id, title, description, evType, date, portfolioId);
        }
    }
}
