package org.isep.portfolioproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.util.Currency;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ApiService implements PriceProvider {

    private static final String API_KEY = "MU1577NQFWWV2J2J";

    public ApiService() {
    }

    @Override
    public double getStockPrice(String sym, Currency c) {
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
            JsonNode quote = root.get("Global Quote");

            if (quote == null || quote.isEmpty()) {
                throw new RuntimeException("No data returned");
            }

            return quote.get("05. price").asDouble();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch price for " + sym, e);
        }
    }

    public double getCryptoPrice(String symbol, Currency c) {
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

            JsonNode rateObj = root.get("Realtime Currency Exchange Rate");
            if (rateObj == null || rateObj.isEmpty()) {
                //what we get back
                throw new RuntimeException(response.toString());
            }

            return rateObj.get("5. Exchange Rate").asDouble();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch crypto price for " + symbol, e);
        }
    }

    public List<Transaction> importFromExchange(String exch) {
        return null;
    }
}