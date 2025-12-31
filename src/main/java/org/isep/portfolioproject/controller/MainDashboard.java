package org.isep.portfolioproject.controller;

import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.WhaleAlert;
import java.util.List;

public class MainDashboard {

    private List<Portfolio> portfolios;

    public MainDashboard() {
    }

    public MainDashboard(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
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