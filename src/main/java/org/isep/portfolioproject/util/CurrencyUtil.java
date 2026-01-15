package org.isep.portfolioproject.util;

import java.util.HashMap;
import java.util.Map;

public class CurrencyUtil {

    private static final Map<Currency, Double> USD_RATES = new HashMap<>();

    static {
        USD_RATES.put(Currency.USD, 1.0);
        USD_RATES.put(Currency.EUR, 0.92);
        USD_RATES.put(Currency.GBP, 0.79);
    }

    private CurrencyUtil() {
    }

    public static double fromUsd(double amount, Currency currency) {
        return amount * USD_RATES.getOrDefault(currency, 1.0);
    }
}
