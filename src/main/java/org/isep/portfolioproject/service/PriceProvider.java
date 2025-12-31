package org.isep.portfolioproject.service;

import org.isep.portfolioproject.util.Currency;

public interface PriceProvider {

    double getCurrentPrice(String sym, Currency c);

}