package org.isep.portfolioproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.model.assets.Crypto;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiService implements PriceProvider {

    private static final String API_KEY = "MU1577NQFWWV2J2J";
    private static final long CACHE_TTL_MS = 90_000L;

    private static class CachedPrice {
        final double value;
        final long timestamp;

        CachedPrice(double value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private final java.util.Map<String, CachedPrice> cache = new java.util.HashMap<>();

    public ApiService() {
    }

    @Override
    public double getStockPrice(String sym, Currency c) {
        String cacheKey = "STOCK:" + sym.toUpperCase() + ":" + c.name();
        CachedPrice cached = cache.get(cacheKey);
        if (cached != null && (System.currentTimeMillis() - cached.timestamp) < CACHE_TTL_MS) {
            return cached.value;
        }

        try {
            String urlStr =
                    "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                            + sym + "&apikey=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            if (root.has("Note") || root.has("Error Message") || root.has("Information")) {
                return cached != null ? cached.value : 0.0;
            }
            JsonNode quote = root.get("Global Quote");

            if (quote == null || quote.isEmpty()) {
                return cached != null ? cached.value : 0.0;
            }

            double price = quote.get("05. price").asDouble();
            cache.put(cacheKey, new CachedPrice(price, System.currentTimeMillis()));
            return price;

        } catch (Exception e) {
            return cached != null ? cached.value : 0.0;
        }
    }

    public double getCryptoPrice(String symbol, Currency c) {
        String cacheKey = "CRYPTO:" + symbol.toUpperCase() + ":" + c.name();
        CachedPrice cached = cache.get(cacheKey);
        if (cached != null && (System.currentTimeMillis() - cached.timestamp) < CACHE_TTL_MS) {
            return cached.value;
        }

        try {
            String urlStr =
                    "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE"
                            + "&from_currency=" + symbol.toUpperCase()
                            + "&to_currency=" + c.name()
                            + "&apikey=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            if (root.has("Note") || root.has("Error Message") || root.has("Information")) {
                return cached != null ? cached.value : 0.0;
            }

            JsonNode rateObj = root.get("Realtime Currency Exchange Rate");
            if (rateObj == null || rateObj.isEmpty()) {
                double price = getStockPrice(symbol, c);
                cache.put(cacheKey, new CachedPrice(price, System.currentTimeMillis()));
                return price;
            }

            double price = rateObj.get("5. Exchange Rate").asDouble();
            cache.put(cacheKey, new CachedPrice(price, System.currentTimeMillis()));
            return price;

        } catch (Exception e) {
            try {
                double price = getStockPrice(symbol, c);
                cache.put(cacheKey, new CachedPrice(price, System.currentTimeMillis()));
                return price;
            } catch (Exception ignored) {
                return cached != null ? cached.value : 0.0;
            }
        }
    }

    public List<Transaction> importFromExchange(String exch) {
        File file = new File("data/" + exch.toLowerCase() + "_import.csv");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                LocalDateTime date = LocalDateTime.parse(parts[0].trim());
                TransactionType type = TransactionType.valueOf(parts[1].trim().toUpperCase());
                String assetType = parts[2].trim();
                String symbol = parts[3].trim();
                double quantity = Double.parseDouble(parts[4].trim());
                double price = Double.parseDouble(parts[5].trim());

                Asset asset = "CRYPTO".equalsIgnoreCase(assetType)
                        ? new Crypto(symbol, symbol)
                        : new Stock(symbol, symbol);

                Transaction tx = Transaction.trade(
                        UUID.randomUUID().toString(),
                        type,
                        asset,
                        quantity,
                        price,
                        date
                );
                transactions.add(tx);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import exchange file", e);
        }
        return transactions;
    }
}
