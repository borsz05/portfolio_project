package org.isep.portfolioproject.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import org.isep.portfolioproject.model.Event;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Position;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.WhaleAlert;
import org.isep.portfolioproject.service.AnalysisService;
import org.isep.portfolioproject.service.ApiService;
import org.isep.portfolioproject.service.AppState;
import org.isep.portfolioproject.service.PriceProvider;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.CurrencyUtil;
import org.isep.portfolioproject.util.EventType;
import org.isep.portfolioproject.util.TransactionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MainDashboard {

    @FXML private ComboBox<Portfolio> portfolioCombo;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private ComboBox<String> periodCombo;

    @FXML private LineChart<String, Number> valueLineChart;
    @FXML private PieChart allocationPieChart;

    @FXML private ListView<String> eventsList;
    @FXML private ListView<WhaleAlert> whaleAlertsList;

    @FXML private Label totalValueLabel;
    @FXML private Label cashLabel;
    @FXML private Label changeLabel;
    @FXML private Label plLabel;

    @FXML private TableView<PositionRow> positionsTable;
    @FXML private TableColumn<PositionRow, String> typeCol;
    @FXML private TableColumn<PositionRow, String> symbolCol;
    @FXML private TableColumn<PositionRow, String> quantityCol;
    @FXML private TableColumn<PositionRow, String> priceCol;
    @FXML private TableColumn<PositionRow, String> valueCol;

    @FXML private Label savingsTitleLabel;
    @FXML private TableView<SavingsRow> savingsTable;
    @FXML private TableColumn<SavingsRow, String> periodCol;
    @FXML private TableColumn<SavingsRow, String> interestCol;
    @FXML private TableColumn<SavingsRow, String> expectedCol;

    private static final double SAVINGS_ANNUAL_RATE = 0.03;

    private final AnalysisService analysisService = AppState.get().getAnalysisService();
    private final PriceProvider priceProvider = AppState.get().getPriceProvider();
    private final ApiService apiService = new ApiService();

    @FXML
    private void initialize() {
        currencyCombo.getItems().setAll("USD", "EUR", "GBP");
        currencyCombo.getSelectionModel().selectFirst();

        periodCombo.getItems().setAll("1W", "1M", "3M", "1Y");
        periodCombo.getSelectionModel().select("1M");

        setupPositionsTable();
        setupSavingsTable();
        refreshPortfolioCombo();

        portfolioCombo.setOnAction(e -> {
            AppState.get().setSelectedPortfolio(portfolioCombo.getValue());
            onRefresh();
        });
        currencyCombo.setOnAction(e -> onRefresh());
        periodCombo.setOnAction(e -> onRefresh());

        onRefresh();
    }

    @FXML
    private void onRefresh() {
        Portfolio portfolio = getSelectedPortfolio();
        if (portfolio == null) {
            totalValueLabel.setText("--");
            cashLabel.setText("--");
            changeLabel.setText("--");
            plLabel.setText("--");
            valueLineChart.getData().clear();
            allocationPieChart.getData().clear();
            eventsList.getItems().clear();
            whaleAlertsList.getItems().clear();
            positionsTable.getItems().clear();
            renderSavingsTable(currencyCombo.getValue());
            return;
        }

        Currency currency = getSelectedCurrency();
        try {
            double totalValue = portfolio.calculateTotalValue(currency, priceProvider);
            double cashValue = CurrencyUtil.fromUsd(AppState.get().getBrokerAccount().getBalance(), currency);

            double invested = calculateInvested(portfolio);
            double pnl = totalValue - invested;
            double roi = invested <= 0 ? 0.0 : pnl / invested;

            totalValueLabel.setText(String.format("%.2f %s", totalValue, currency));
            cashLabel.setText(String.format("%.2f %s", cashValue, currency));
            changeLabel.setText(String.format("%+.2f%%", roi * 100.0));
            plLabel.setText(String.format("%+.2f %s", pnl, currency));

            renderLineChart(portfolio, currency);
            renderPieChart(portfolio, currency);
            renderEvents(portfolio);
            renderWhaleAlerts(portfolio);
            renderPositionsTable(portfolio, currency);
            renderSavingsTable(currency.name());
        } catch (Exception e) {
            totalValueLabel.setText("--");
            cashLabel.setText("--");
            changeLabel.setText("--");
            plLabel.setText("--");
            valueLineChart.getData().clear();
            allocationPieChart.getData().clear();
            eventsList.getItems().clear();
            whaleAlertsList.getItems().clear();
            positionsTable.getItems().clear();
            renderSavingsTable(currencyCombo.getValue());
            new Alert(Alert.AlertType.ERROR, "API error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void openTransfer() {
        openWindow("transfer.fxml", "Transfer");
    }

    @FXML
    private void openBuySell() {
        openWindow("buysell.fxml", "Buy / Sell");
    }

    @FXML
    private void logout() {
        try {
            AppState.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/org/isep/portfolioproject/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            totalValueLabel.getScene().getWindow().hide();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not open login view").showAndWait();
        }
    }

    @FXML
    private void createPortfolio() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("New Portfolio");
        nameDialog.setHeaderText("Portfolio name");
        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().isBlank()) return;

        TextInputDialog descDialog = new TextInputDialog();
        descDialog.setTitle("New Portfolio");
        descDialog.setHeaderText("Portfolio description");
        Optional<String> descResult = descDialog.showAndWait();

        ChoiceDialog<Currency> currencyDialog = new ChoiceDialog<>(Currency.USD, Currency.values());
        currencyDialog.setTitle("Reference Currency");
        currencyDialog.setHeaderText("Choose reference currency");
        Optional<Currency> currencyResult = currencyDialog.showAndWait();
        Currency reference = currencyResult.orElse(Currency.USD);

        Portfolio portfolio = new Portfolio(
                UUID.randomUUID().toString(),
                nameResult.get().trim(),
                descResult.orElse(""),
                false,
                reference
        );

        if (confirm("Enable third-party monitoring for this portfolio?")) {
            portfolio.setIsThirdPartyMonitor(true);
            TextInputDialog addrDialog = new TextInputDialog();
            addrDialog.setTitle("Monitor Addresses");
            addrDialog.setHeaderText("Enter addresses (comma separated)");
            addrDialog.showAndWait().ifPresent(text -> {
                for (String addr : text.split(",")) {
                    if (!addr.isBlank()) portfolio.getMonitoredAddresses().add(addr.trim());
                }
            });

            TextInputDialog chainDialog = new TextInputDialog("bitcoin,ethereum");
            chainDialog.setTitle("Monitor Blockchains");
            chainDialog.setHeaderText("Enter blockchains (comma separated)");
            chainDialog.showAndWait().ifPresent(text -> {
                for (String chain : text.split(",")) {
                    if (!chain.isBlank()) portfolio.getMonitoredBlockchains().add(chain.trim().toLowerCase());
                }
            });
        }

        AppState.get().getPortfolios().add(portfolio);
        AppState.get().setSelectedPortfolio(portfolio);
        AppState.get().save();
        refreshPortfolioCombo();
    }

    @FXML
    private void clonePortfolio() {
        Portfolio portfolio = getSelectedPortfolio();
        if (portfolio == null) return;

        TextInputDialog nameDialog = new TextInputDialog(portfolio.getName() + " Copy");
        nameDialog.setTitle("Clone Portfolio");
        nameDialog.setHeaderText("New name for the clone");
        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().isBlank()) return;

        Portfolio copy = portfolio.copyObject();
        copy.setId(UUID.randomUUID().toString());
        copy.setName(nameResult.get().trim());

        AppState.get().getPortfolios().add(copy);
        AppState.get().setSelectedPortfolio(copy);
        AppState.get().save();
        refreshPortfolioCombo();
    }

    @FXML
    private void deletePortfolio() {
        Portfolio portfolio = getSelectedPortfolio();
        if (portfolio == null) return;
        if (!confirm("Delete portfolio \"" + portfolio.getName() + "\"?")) return;

        AppState.get().getPortfolios().remove(portfolio);
        AppState.get().setSelectedPortfolio(AppState.get().getPortfolios().isEmpty() ? null : AppState.get().getPortfolios().get(0));
        AppState.get().save();
        refreshPortfolioCombo();
        onRefresh();
    }

    @FXML
    private void addEvent() {
        Portfolio portfolio = getSelectedPortfolio();
        if (portfolio == null) return;

        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Add Event");
        titleDialog.setHeaderText("Event title");
        Optional<String> title = titleDialog.showAndWait();
        if (title.isEmpty() || title.get().isBlank()) return;

        ChoiceDialog<EventType> typeDialog = new ChoiceDialog<>(EventType.CUSTOM, EventType.values());
        typeDialog.setTitle("Add Event");
        typeDialog.setHeaderText("Choose event type");
        Optional<EventType> typeResult = typeDialog.showAndWait();
        EventType type = typeResult.orElse(EventType.CUSTOM);

        Event event = new Event(
                UUID.randomUUID().toString(),
                title.get().trim(),
                "",
                type,
                LocalDateTime.now(),
                portfolio.getId()
        );
        portfolio.addEvent(event);
        AppState.get().save();
        renderEvents(portfolio);
    }

    @FXML
    private void importExchange() {
        Portfolio portfolio = getSelectedPortfolio();
        if (portfolio == null) return;

        ChoiceDialog<String> dialog = new ChoiceDialog<>("coinbase", "coinbase", "binance");
        dialog.setTitle("Import Exchange");
        dialog.setHeaderText("Select exchange file to import");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        List<Transaction> imported = apiService.importFromExchange(result.get());
        if (imported.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No transactions found.").showAndWait();
            return;
        }

        for (Transaction tx : imported) {
            portfolio.apply(tx);
        }
        AppState.get().save();
        onRefresh();
    }

    @FXML
    private void setEncryption() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Encryption");
        dialog.setHeaderText("Set a passphrase (leave empty to disable)");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String passphrase = result.get().trim();
        AppState.get().setPassphrase(passphrase.isEmpty() ? null : passphrase);
        AppState.get().save();
    }

    private void renderLineChart(Portfolio portfolio, Currency currency) {
        valueLineChart.getData().clear();
        int days = getSelectedDays();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        List<AnalysisService.ValuePoint> points =
                analysisService.buildValueHistory(portfolio, priceProvider, currency, days);
        for (AnalysisService.ValuePoint point : points) {
            series.getData().add(new XYChart.Data<>(point.label, point.value));
        }
        valueLineChart.getData().add(series);
    }

    private void renderPieChart(Portfolio portfolio, Currency currency) {
        allocationPieChart.getData().clear();
        var data = analysisService.getAllocationData(portfolio, priceProvider, currency);
        List<PieChart.Data> entries = new ArrayList<>();
        for (var entry : data.entrySet()) {
            entries.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        allocationPieChart.setData(FXCollections.observableArrayList(entries));
    }

    private void renderEvents(Portfolio portfolio) {
        List<String> items = new ArrayList<>();
        for (Event event : portfolio.getEvents()) {
            if (event.getPortfolioId() == null || event.getPortfolioId().equals(portfolio.getId())) {
                items.add(event.toString());
            }
        }

        List<Transaction> txs = new ArrayList<>(portfolio.getTransactions());
        txs.sort(Comparator.comparing(Transaction::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
        for (Transaction tx : txs) {
            if (tx.getType() == null) continue;
            String when = tx.getDate() == null ? "" : tx.getDate().toLocalDate().toString();
            String label = when + " " + tx.getType();
            if (tx.getAsset() != null) {
                label += " " + tx.getAsset().getSymbol();
            }
            items.add(label);
        }

        if (portfolio.getIsThirdPartyMonitor()) {
            for (String addr : portfolio.getMonitoredAddresses()) {
                double balance = AppState.get().getBlockchainMonitor().trackThirdPartyBalance(addr);
                items.add("Monitor " + addr + ": " + balance);
            }
        }

        eventsList.getItems().setAll(items);
    }

    private void renderWhaleAlerts(Portfolio portfolio) {
        List<WhaleAlert> alerts = AppState.get().getBlockchainMonitor().scanForWhaleTransactions();
        if (!portfolio.getMonitoredBlockchains().isEmpty()) {
            alerts.removeIf(alert -> !portfolio.getMonitoredBlockchains()
                    .contains(alert.getBlockchain() == null ? "" : alert.getBlockchain().toLowerCase()));
        }
        whaleAlertsList.getItems().setAll(alerts);
    }

    private void setupPositionsTable() {
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        symbolCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSymbol()));
        quantityCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getQuantity()));
        priceCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrice()));
        valueCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getValue()));
        positionsTable.getItems().clear();
    }

    private void setupSavingsTable() {
        savingsTitleLabel.setText(String.format("Savings Forecast (%.1f%% APR)", SAVINGS_ANNUAL_RATE * 100.0));
        periodCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPeriod()));
        interestCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getInterest()));
        expectedCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getExpected()));
        savingsTable.getItems().clear();
    }

    private void renderPositionsTable(Portfolio portfolio, Currency currency) {
        List<PositionRow> rows = new ArrayList<>();
        if (portfolio.getPositions() != null && !portfolio.getPositions().isEmpty()) {
            for (Position position : portfolio.getPositions().values()) {
                var asset = position.getAsset();
                if (asset == null) continue;
                String type = asset.isDivisible() ? "CRYPTO" : "STOCK";
                double price = asset.isDivisible()
                        ? priceProvider.getCryptoPrice(asset.getSymbol(), currency)
                        : priceProvider.getStockPrice(asset.getSymbol(), currency);
                double qty = position.getQuantity();
                double value = qty * price;

                rows.add(new PositionRow(
                        type,
                        asset.getSymbol(),
                        String.format("%.4f", qty),
                        String.format("%.2f %s", price, currency),
                        String.format("%.2f %s", value, currency)
                ));
            }
        } else {
            rows.addAll(buildRowsFromTransactions(portfolio, currency));
        }

        rows.sort(Comparator.comparing(PositionRow::getSymbol));
        positionsTable.getItems().setAll(rows);
    }

    private List<PositionRow> buildRowsFromTransactions(Portfolio portfolio, Currency currency) {
        List<PositionRow> rows = new ArrayList<>();
        if (portfolio.getTransactions() == null) return rows;

        class TempPos {
            String symbol;
            boolean crypto;
            double quantity;
            TempPos(String symbol, boolean crypto) {
                this.symbol = symbol;
                this.crypto = crypto;
            }
        }

        java.util.Map<String, TempPos> map = new java.util.HashMap<>();
        for (Transaction tx : portfolio.getTransactions()) {
            if (tx.getType() == null || tx.getAsset() == null) continue;
            if (tx.getType() != TransactionType.BUY && tx.getType() != TransactionType.SELL) continue;
            String symbol = tx.getAsset().getSymbol();
            boolean crypto = tx.getAsset().isDivisible();
            String key = symbol + "|" + (crypto ? "CRYPTO" : "STOCK");
            TempPos temp = map.computeIfAbsent(key, k -> new TempPos(symbol, crypto));
            double delta = tx.getType() == TransactionType.BUY ? tx.getQuantity() : -tx.getQuantity();
            temp.quantity += delta;
        }

        for (TempPos temp : map.values()) {
            if (temp.quantity <= 0) continue;
            double price = temp.crypto
                    ? priceProvider.getCryptoPrice(temp.symbol, currency)
                    : priceProvider.getStockPrice(temp.symbol, currency);
            double value = temp.quantity * price;
            rows.add(new PositionRow(
                    temp.crypto ? "CRYPTO" : "STOCK",
                    temp.symbol,
                    String.format("%.4f", temp.quantity),
                    String.format("%.2f %s", price, currency),
                    String.format("%.2f %s", value, currency)
            ));
        }
        return rows;
    }

    private void renderSavingsTable(String currencyCode) {
        Currency currency = getSelectedCurrency();
        if (currencyCode != null) {
            try {
                currency = Currency.valueOf(currencyCode);
            } catch (Exception ignored) {
            }
        }

        double balanceUsd = AppState.get().getSavingAccount().getBalance();
        double balance = CurrencyUtil.fromUsd(balanceUsd, currency);

        double[] months = new double[]{0, 1, 3, 6, 12};
        String[] labels = new String[]{"Today", "1M", "3M", "6M", "1Y"};

        List<SavingsRow> rows = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            double interest = balance * SAVINGS_ANNUAL_RATE * (months[i] / 12.0);
            double expected = balance + interest;
            rows.add(new SavingsRow(
                    labels[i],
                    String.format("%.2f %s", interest, currency),
                    String.format("%.2f %s", expected, currency)
            ));
        }

        savingsTable.getItems().setAll(rows);
    }

    private Portfolio getSelectedPortfolio() {
        Portfolio selected = portfolioCombo.getValue();
        if (selected == null) {
            selected = AppState.get().getSelectedPortfolio();
        }
        return selected;
    }

    private Currency getSelectedCurrency() {
        try {
            return Currency.valueOf(currencyCombo.getValue());
        } catch (Exception e) {
            return Currency.USD;
        }
    }

    private int getSelectedDays() {
        String period = periodCombo.getValue();
        if ("1W".equals(period)) return 7;
        if ("3M".equals(period)) return 90;
        if ("1Y".equals(period)) return 365;
        return 30;
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/isep/portfolioproject/" + fxmlFile)
            );

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshPortfolioCombo() {
        List<Portfolio> list = AppState.get().getPortfolios();
        portfolioCombo.getItems().setAll(list);
        if (!list.isEmpty()) {
            Portfolio selected = AppState.get().getSelectedPortfolio();
            if (selected == null) {
                selected = list.get(0);
            }
            portfolioCombo.getSelectionModel().select(selected);
            AppState.get().setSelectedPortfolio(selected);
        }
    }

    private boolean confirm(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private double calculateInvested(Portfolio portfolio) {
        double invested = 0.0;
        for (Transaction tx : portfolio.getTransactions()) {
            if (tx.getType() == null || tx.getAsset() == null) continue;
            if (tx.getType() == TransactionType.BUY) {
                invested += tx.getQuantity() * tx.getPrice();
            } else if (tx.getType() == TransactionType.SELL) {
                invested -= tx.getQuantity() * tx.getPrice();
            }
        }
        return invested;
    }

    private static class PositionRow {
        private final String type;
        private final String symbol;
        private final String quantity;
        private final String price;
        private final String value;

        private PositionRow(String type, String symbol, String quantity, String price, String value) {
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.value = value;
        }

        public String getType() { return type; }
        public String getSymbol() { return symbol; }
        public String getQuantity() { return quantity; }
        public String getPrice() { return price; }
        public String getValue() { return value; }
    }

    private static class SavingsRow {
        private final String period;
        private final String interest;
        private final String expected;

        private SavingsRow(String period, String interest, String expected) {
            this.period = period;
            this.interest = interest;
            this.expected = expected;
        }

        public String getPeriod() { return period; }
        public String getInterest() { return interest; }
        public String getExpected() { return expected; }
    }
}