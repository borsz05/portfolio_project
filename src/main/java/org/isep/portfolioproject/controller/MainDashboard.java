package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.WhaleAlert;
import java.util.List;

public class MainDashboard {

    // UI parts from dashboard.fxml (with these we can access and update the UI elements)
    @FXML private ComboBox<Portfolio> portfolioCombo;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private ComboBox<String> periodCombo;
    @FXML private Button refreshButton;

    @FXML private LineChart<String, Number> valueLineChart;
    @FXML private PieChart allocationPieChart;

    @FXML private ListView<String> eventsList;
    @FXML private ListView<WhaleAlert> whaleAlertsList;

    private List<Portfolio> portfolios;

    public MainDashboard() {
    }

    public MainDashboard(List<Portfolio> portfolios) { //useless !?
        this.portfolios = portfolios;
    }

    // run automatically
    @FXML
    private void initialize() {
        // - ComboBox upload
        // - chart draw?
    }

    public void renderLineChart() {
    }

    public void renderPieChart() {
    }

    public void showWhaleAlerts(List<WhaleAlert> a) {
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }
}