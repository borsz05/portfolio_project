package org.isep.portfolioproject.model;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.util.TransactionType;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

  @Test
    void buyCreateAssetIfNotOwned() {
  Portfolio portfolio = new Portfolio();

  portfolio.setId("1");
  portfolio.setName("test");
  portfolio.setDescription("testing the portofolio");
  portfolio.setIsThirdPartyMonitor(false);

  Transaction transaction = new Transaction("t1", new Stock("AAPL", 0),
          10,
          150,
          LocalDate.now(),
          TransactionType.BUY
  );

  portfolio.addTransaction(transaction);

  assertEquals(1, portfolio.getAssets().size());
  assertEquals("AAPL", portfolio.getAssets().get(0).getSymbol());
  assertEquals(10, portfolio.getAssets().get(0).getQuantity());


  }
}