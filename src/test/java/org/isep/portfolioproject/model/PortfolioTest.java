package org.isep.portfolioproject.model;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

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


    @Test
    void calculateReturnSum() {
        Portfolio portfolio1 = new Portfolio();

        Stock stock1 = new Stock("AAPL", 10);
        Stock stock2 = new Stock("AAPL", 5);

        portfolio1.getAssets().add(stock1);
        portfolio1.getAssets().add(stock2);

        double total = portfolio1.calculateTotalValue(Currency.EUR);

          assertEquals(
                  stock1.getCurrentValue(Currency.EUR) +
                          stock2.getCurrentValue(Currency.EUR),
                  total,
                  0.0001);

      }


      @Test
        void copyObject(){
            Portfolio original = new Portfolio();

            original.setId("1");
            original.setName("Test");

            original.getAssets().add(new Stock("AAPL", 10));

            Portfolio copy = original.copyObject();

            assertNotSame(original, copy);

            assertEquals("1", copy.getId());
            assertEquals("Test", copy.getName());

            copy.getAssets().clear();
            assertEquals(1, original.getAssets().size());

  }

  @Test
    void removeAsset() {
      Portfolio portfolio2 = new Portfolio();

      Stock owned = new Stock("AAPL", 5);
      portfolio2.getAssets().add(owned);

      Stock remove = new Stock("AAPL", 999);
      portfolio2.removeAsset(remove);

      assertEquals(0, portfolio2.getAssets().size());
  }

  }
