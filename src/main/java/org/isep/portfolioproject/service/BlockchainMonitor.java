package org.isep.portfolioproject.service;

import org.isep.portfolioproject.model.WhaleAlert;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlockchainMonitor {

    private double whaleThreshold;

    public BlockchainMonitor() {
    }

    public BlockchainMonitor(double whaleThreshold) {
        this.whaleThreshold = whaleThreshold;
    }

    public List<WhaleAlert> scanForWhaleTransactions() {
        List<WhaleAlert> alerts = new ArrayList<>();
        File file = new File("data/whale_alerts.csv");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;
                    String chain = parts[0].trim();
                    double amount = Double.parseDouble(parts[1].trim());
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2].trim());
                    if (amount >= whaleThreshold) {
                        alerts.add(new WhaleAlert(chain, amount, timestamp));
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (alerts.isEmpty()) {
            alerts.add(new WhaleAlert("bitcoin", whaleThreshold * 2.5, LocalDateTime.now().minusHours(5)));
            alerts.add(new WhaleAlert("ethereum", whaleThreshold * 1.6, LocalDateTime.now().minusHours(2)));
        }
        return alerts;
    }

    public double trackThirdPartyBalance(String addr) {
        File file = new File("data/third_party_balances.csv");
        if (!file.exists()) return 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                if (parts[0].trim().equalsIgnoreCase(addr)) {
                    return Double.parseDouble(parts[1].trim());
                }
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public double getWhaleThreshold() {
        return whaleThreshold;
    }

    public void setWhaleThreshold(double whaleThreshold) {
        this.whaleThreshold = whaleThreshold;
    }
}