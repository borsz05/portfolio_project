package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.isep.portfolioproject.model.Event;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.assets.Asset;
import org.isep.portfolioproject.model.assets.Crypto;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.service.AppState;
import org.isep.portfolioproject.service.PriceProvider;
import org.isep.portfolioproject.service.TradingService;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class BuySellController {

    @FXML private ComboBox<String> assetTypeCombo;
    @FXML private ComboBox<String> symbolCombo;
    @FXML private TextField amountUsdField;
    @FXML private Label priceLabel;
    @FXML private Label qtyLabel;
    @FXML private Label brokerBalanceLabel;
    @FXML private Label statusLabel;

    private final TradingService tradingService = new TradingService();

    @FXML
    public void initialize() {
        assetTypeCombo.getItems().addAll("STOCK", "CRYPTO");
        assetTypeCombo.setValue("STOCK");
        symbolCombo.setEditable(true);

        assetTypeCombo.setOnAction(e -> refreshSymbols());
        symbolCombo.setOnAction(e -> updatePriceAndQty());
        symbolCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> updatePriceAndQty());
        amountUsdField.textProperty().addListener((obs, oldVal, newVal) -> updatePriceAndQty());

        refreshSymbols();
        updatePriceAndQty();
        updateBrokerBalance();
    }

    @FXML
    private void onBuy() {
        handleTrade(true);
    }

    @FXML
    private void onSell() {
        handleTrade(false);
    }

    private void refreshSymbols() {
        Portfolio portfolio = AppState.get().getSelectedPortfolio();
        symbolCombo.getItems().clear();
        if (portfolio == null) return;

        portfolio.getPositions().values().forEach(pos -> {
            Asset asset = pos.getAsset();
            if (assetTypeCombo.getValue().equals("CRYPTO") == asset.isDivisible()) {
                symbolCombo.getItems().add(asset.getSymbol());
            }
        });
    }

    private void updatePriceAndQty() {
        String symbol = getSymbol();
        if (symbol == null || symbol.isBlank()) {
            priceLabel.setText("--");
            qtyLabel.setText("--");
            return;
        }

        PriceProvider provider = AppState.get().getPriceProvider();
        double price = isCrypto()
                ? provider.getCryptoPrice(symbol, Currency.USD)
                : provider.getStockPrice(symbol, Currency.USD);
        priceLabel.setText(String.format("%.2f USD", price));

        double amount = parseAmount();
        if (amount <= 0 || price <= 0) {
            qtyLabel.setText("--");
            return;
        }
        double qty = amount / price;
        if (!isCrypto()) {
            qty = Math.floor(qty);
        }
        qtyLabel.setText(String.format("%.4f", qty));
    }

    private void updateBrokerBalance() {
        double balance = AppState.get().getBrokerAccount().getBalance();
        brokerBalanceLabel.setText(String.format("%.2f USD", balance));
    }

    private void handleTrade(boolean isBuy) {
        Portfolio portfolio = AppState.get().getSelectedPortfolio();
        if (portfolio == null) {
            statusLabel.setText("Select a portfolio first.");
            return;
        }

        String symbol = getSymbol();
        if (symbol == null || symbol.isBlank()) {
            statusLabel.setText("Choose an asset symbol.");
            return;
        }

        double amount = parseAmount();
        if (amount <= 0) {
            statusLabel.setText("Enter a valid amount.");
            return;
        }

        PriceProvider provider = AppState.get().getPriceProvider();
        double price = isCrypto()
                ? provider.getCryptoPrice(symbol, Currency.USD)
                : provider.getStockPrice(symbol, Currency.USD);
        if (price <= 0) {
            statusLabel.setText("Price unavailable for " + symbol);
            return;
        }

        double qty = amount / price;
        if (!isCrypto()) {
            qty = Math.floor(qty);
        }
        if (qty <= 0) {
            statusLabel.setText("Amount too small for a trade.");
            return;
        }

        Asset asset = isCrypto() ? new Crypto(symbol, symbol) : new Stock(symbol, symbol);
        try {
            Transaction tx;
            if (isBuy) {
                tx = tradingService.buy(portfolio, AppState.get().getBrokerAccount(), asset, qty, price);
            } else {
                tx = tradingService.sell(portfolio, AppState.get().getBrokerAccount(), asset, qty, price);
            }

            portfolio.addEvent(new Event(
                    UUID.randomUUID().toString(),
                    (isBuy ? "Buy " : "Sell ") + String.format("%.4f", qty) + " " + symbol,
                    "Trade executed",
                    EventType.CUSTOM,
                    LocalDateTime.now(),
                    portfolio.getId()
            ));

            AppState.get().save();
            updateBrokerBalance();
            statusLabel.setText((isBuy ? "BUY " : "SELL ") + String.format("%.4f", qty) + " " + symbol);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private String getSymbol() {
        String value = symbolCombo.getEditor().getText();
        if (value == null || value.isBlank()) {
            value = symbolCombo.getValue();
        }
        return value == null ? null : value.trim().toUpperCase();
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amountUsdField.getText().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private boolean isCrypto() {
        return "CRYPTO".equalsIgnoreCase(assetTypeCombo.getValue());
    }
}
