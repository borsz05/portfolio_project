package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisService {

    public AnalysisService() {
    }

    public boolean isProfitable(Portfolio p) {
        return isProfitable(p, new ApiService(), p.getReferenceCurrency());
    }

    public double calculateROI(Portfolio p) {
        return calculateROI(p, new ApiService(), p.getReferenceCurrency());
    }

    public String getProfitabilityHistory(Portfolio p) {
        List<ValuePoint> points = buildValueHistory(p, new ApiService(), p.getReferenceCurrency(), 30);
        int positive = 0;
        for (ValuePoint point : points) {
            if (point.value > 0) positive++;
        }
        return "Profitable " + positive + "/" + points.size() + " days";
    }

    public Map getAllocationData(Portfolio p) {
        return getAllocationData(p, new ApiService(), p.getReferenceCurrency());
    }

    public boolean isProfitable(Portfolio p, PriceProvider priceProvider, Currency currency) {
        if (p == null) return false;
        double invested = calculateInvested(p);
        if (invested <= 0) return false;
        double current = p.calculateTotalValue(currency, priceProvider);
        return current > invested;
    }

    public double calculateROI(Portfolio p, PriceProvider priceProvider, Currency currency) {
        if (p == null) return 0.0;
        double invested = calculateInvested(p);
        if (invested <= 0) return 0.0;
        double current = p.calculateTotalValue(currency, priceProvider);
        return (current - invested) / invested;
    }

    public Map<String, Double> getAllocationData(Portfolio p, PriceProvider priceProvider, Currency currency) {
        Map<String, Double> data = new HashMap<>();
        if (p == null) return data;

        double total = p.calculateTotalValue(currency, priceProvider);
        if (total == 0) return data;

        p.getPositions().values().forEach(pos -> {
            Asset asset = pos.getAsset();
            double price = asset.isDivisible()
                    ? priceProvider.getCryptoPrice(asset.getSymbol(), currency)
                    : priceProvider.getStockPrice(asset.getSymbol(), currency);
            double value = pos.getQuantity() * price;
            data.put(asset.getSymbol(), value);
        });

        return data;
    }

    public List<ValuePoint> buildValueHistory(Portfolio p, PriceProvider provider, Currency currency, int days) {
        List<ValuePoint> points = new ArrayList<>();
        if (p == null || days <= 0) return points;

        List<Transaction> txs = new ArrayList<>(p.getTransactions());
        txs.removeIf(tx -> tx.getDate() == null);
        txs.sort(Comparator.comparing(Transaction::getDate));

        Map<Asset, Double> quantities = new HashMap<>();
        Map<Asset, Double> basePrices = new HashMap<>();
        for (Asset asset : p.getPositions().keySet()) {
            double current = asset.isDivisible()
                    ? provider.getCryptoPrice(asset.getSymbol(), currency)
                    : provider.getStockPrice(asset.getSymbol(), currency);
            basePrices.put(asset, current);
        }
        LocalDate start = LocalDate.now().minusDays(days - 1L);
        int txIndex = 0;

        for (int i = 0; i < days; i++) {
            LocalDate date = start.plusDays(i);
            while (txIndex < txs.size() && !txs.get(txIndex).getDate().toLocalDate().isAfter(date)) {
                Transaction tx = txs.get(txIndex);
                if (tx.getType() == TransactionType.BUY) {
                    quantities.merge(tx.getAsset(), tx.getQuantity(), Double::sum);
                } else if (tx.getType() == TransactionType.SELL) {
                    quantities.merge(tx.getAsset(), -tx.getQuantity(), Double::sum);
                }
                txIndex++;
            }

            double value = 0.0;
            for (Map.Entry<Asset, Double> entry : quantities.entrySet()) {
                Asset asset = entry.getKey();
                double quantity = entry.getValue();
                if (quantity <= 0) continue;
                double current = basePrices.getOrDefault(asset, 0.0);
                double factor = 1.0 + syntheticWave(asset.getSymbol(), date, days);
                value += quantity * current * factor;
            }
            points.add(new ValuePoint(date.toString(), value));
        }
        return points;
    }

    private double calculateInvested(Portfolio p) {
        double invested = 0.0;
        for (Transaction tx : p.getTransactions()) {
            if (tx.getType() == TransactionType.BUY) {
                invested += tx.getQuantity() * tx.getPrice();
            } else if (tx.getType() == TransactionType.SELL) {
                invested -= tx.getQuantity() * tx.getPrice();
            }
        }
        return invested;
    }

    private double syntheticWave(String symbol, LocalDate date, int days) {
        int seed = Math.abs(symbol.hashCode() % 10);
        double wave = Math.sin((date.toEpochDay() + seed) / 7.0) * 0.04;
        double drift = (date.toEpochDay() % days) * 0.0003;
        return wave + drift;
    }

    public static class ValuePoint {
        public final String label;
        public final double value;

        public ValuePoint(String label, double value) {
            this.label = label;
            this.value = value;
        }
    }
}