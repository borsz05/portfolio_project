package org.isep.portfolioproject.service;

import org.isep.portfolioproject.util.Currency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class LocalPriceProvider implements PriceProvider {

    private final Map<String, Double> stockPricesUsd = new HashMap<>();
    private final Map<String, Double> cryptoPricesUsd = new HashMap<>();
    private final Map<Currency, Double> fxRates = new HashMap<>();

    public LocalPriceProvider() {
        fxRates.put(Currency.USD, 1.0);
        fxRates.put(Currency.EUR, 0.92);
        fxRates.put(Currency.GBP, 0.79);
        loadPricesFromCsv("data/prices.csv");
        if (stockPricesUsd.isEmpty() && cryptoPricesUsd.isEmpty()) {
            seedDefaults();
        }
    }

    @Override
    public double getStockPrice(String sym, Currency c) {
        return convert(stockPricesUsd.getOrDefault(sym.toUpperCase(), 0.0), c);
    }

    @Override
    public double getCryptoPrice(String sym, Currency c) {
        return convert(cryptoPricesUsd.getOrDefault(sym.toUpperCase(), 0.0), c);
    }

    private double convert(double usdPrice, Currency currency) {
        return usdPrice * fxRates.getOrDefault(currency, 1.0);
    }

    private void loadPricesFromCsv(String path) {
        File file = new File(path);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String type = parts[0].trim().toUpperCase();
                String symbol = parts[1].trim().toUpperCase();
                double price = Double.parseDouble(parts[2].trim());

                if ("CRYPTO".equals(type)) {
                    cryptoPricesUsd.put(symbol, price);
                } else if ("STOCK".equals(type)) {
                    stockPricesUsd.put(symbol, price);
                }
            }
        } catch (Exception e) {
            seedDefaults();
        }
    }

    private void seedDefaults() {
        stockPricesUsd.put("AAPL", 189.3);
        stockPricesUsd.put("AMD", 148.1);
        stockPricesUsd.put("TSLA", 222.5);
        cryptoPricesUsd.put("BTC", 42000.0);
        cryptoPricesUsd.put("ETH", 2200.0);
        cryptoPricesUsd.put("SOL", 96.0);
    }
}
