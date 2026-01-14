package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.WhaleAlert;

import java.util.ArrayList;
import java.util.List;

public class MainDashboard {

    @FXML private ComboBox<Portfolio> portfolioCombo;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private ComboBox<String> periodCombo;
    @FXML private Button refreshButton;

    @FXML private LineChart<String, Number> valueLineChart;
    @FXML private PieChart allocationPieChart;

    @FXML private ListView<String> eventsList;
    @FXML private ListView<WhaleAlert> whaleAlertsList;

    @FXML private Label totalValueLabel;
    @FXML private Label cashLabel;
    @FXML private Label changeLabel;
    @FXML private Label plLabel;

    private List<Portfolio> portfolios;

    public MainDashboard() {
    }

    public MainDashboard(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    @FXML
    private void initialize() {

        currencyCombo.getItems().setAll("USD", "EUR");
        currencyCombo.getSelectionModel().selectFirst();

        periodCombo.getItems().setAll("1D", "1W", "1M", "1Y");
        periodCombo.getSelectionModel().select("1M");

        if (portfolios == null) {
            portfolios = new ArrayList<>();
        }

        if (!portfolios.isEmpty()) {
            portfolioCombo.getItems().setAll(portfolios);
            portfolioCombo.getSelectionModel().selectFirst();
        }

        portfolioCombo.setOnAction(e -> onRefresh());
        currencyCombo.setOnAction(e -> onRefresh());
        periodCombo.setOnAction(e -> onRefresh());

        onRefresh();
    }

    @FXML
    private void onRefresh() {

        String cur = currencyCombo.getValue() == null ? "USD" : currencyCombo.getValue();

        totalValueLabel.setText("12 345.67 " + cur);
        cashLabel.setText("2 000.00 " + cur);
        changeLabel.setText("+1.8%");
        plLabel.setText("+320.50 " + cur);

        renderLineChart();
        renderPieChart();

        eventsList.getItems().setAll(
                "Bought 0.10 BTC",
                "Sold 2 AAPL",
                "Transfer 200 " + cur + " Checking -> Saving"
        );
    }

    @FXML
    private void openTransfer() {
        openWindow("transfer.fxml", "Transfer");
    }

    @FXML
    private void openBuySell() {
        openWindow("buysell.fxml", "Buy / Sell");
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

    public void renderLineChart() {

        valueLineChart.getData().clear();

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Value");

        s.getData().add(new XYChart.Data<>("T-5", 11800));
        s.getData().add(new XYChart.Data<>("T-4", 11950));
        s.getData().add(new XYChart.Data<>("T-3", 12120));
        s.getData().add(new XYChart.Data<>("T-2", 12010));
        s.getData().add(new XYChart.Data<>("T-1", 12300));
        s.getData().add(new XYChart.Data<>("Now", 12345));

        valueLineChart.getData().add(s);
    }

    public void renderPieChart() {

        allocationPieChart.getData().clear();

        allocationPieChart.getData().add(
                new PieChart.Data("Stocks", 65)
        );
        allocationPieChart.getData().add(
                new PieChart.Data("Crypto", 35)
        );
    }

    public void showWhaleAlerts(List<WhaleAlert> alerts) {
        if (alerts == null) return;
        whaleAlertsList.getItems().setAll(alerts);
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {

        this.portfolios = portfolios;

        if (portfolioCombo != null) {
            portfolioCombo.getItems().setAll(portfolios);
            if (!portfolios.isEmpty()) {
                portfolioCombo.getSelectionModel().selectFirst();
            }
        }
    }
}
