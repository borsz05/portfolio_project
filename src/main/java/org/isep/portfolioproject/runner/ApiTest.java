package org.isep.portfolioproject.runner;

import org.isep.portfolioproject.service.ApiService;
import org.isep.portfolioproject.util.Currency;

public class ApiTest {
    public static void main(String[] args) {
        //we can only test 1 in one running ,bc the limit
        ApiService api = new ApiService();

        //double aapl = api.getStockPrice("AAPL", Currency.USD);
        //System.out.println("AAPL price = " + aapl);

        double btc = api.getCryptoPrice("BTC", Currency.USD);
        System.out.println("BTC price = " + btc);

        //double eth = api.getCryptoPrice("ETH", Currency.USD);
        //System.out.println("ETH price = " + eth);
    }
}
