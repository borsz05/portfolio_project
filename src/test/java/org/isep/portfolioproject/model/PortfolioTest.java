package org.isep.portfolioproject.model;

import org.isep.portfolioproject.model.accounts.CheckingAccount;
import org.isep.portfolioproject.model.accounts.SavingAccount;
import org.isep.portfolioproject.model.assets.Crypto;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.util.Currency;
import org.isep.portfolioproject.util.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class PortfolioTest {

    @Test
    void buyCreateAssetIfNotOwned() {
        Portfolio portfolio = new Portfolio();

        portfolio.setId("1");
        portfolio.setName("test");
        portfolio.setDescription("testing the portfolio");
        portfolio.setIsThirdPartyMonitor(false);

        Transaction transaction = new Transaction(
                "t1",
                new Stock("AAPL", 0),
                10,
                150,
                LocalDateTime.now(),
                TransactionType.BUY
        );

        portfolio.addTransaction(transaction);

        assertEquals(1, portfolio.getAssets().size());
        assertEquals("AAPL", portfolio.getAssets().get(0).getSymbol());
        assertEquals(10, portfolio.getAssets().get(0).getQuantity());
    }

    @Test
    void calculateReturnSum() {
        Portfolio portfolio = new Portfolio();

        Stock stock1 = new Stock("AAPL", 10);
        Stock stock2 = new Stock("AAPL", 5);

        portfolio.getAssets().add(stock1);
        portfolio.getAssets().add(stock2);

        double total = portfolio.calculateTotalValue(Currency.EUR);

        assertEquals(
                stock1.getCurrentValue(Currency.EUR) +
                        stock2.getCurrentValue(Currency.EUR),
                total,
                0.0001
        );
    }

    @Test
    void copyObject() {
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
        Portfolio portfolio = new Portfolio();

        portfolio.getAssets().add(new Stock("AAPL", 5));
        portfolio.removeAsset(new Stock("AAPL", 999));

        assertEquals(0, portfolio.getAssets().size());
    }

    @Test
    void removeAsset_null_noChange() {
        Portfolio portfolio = new Portfolio();

        portfolio.getAssets().add(new Stock("AAPL", 5));
        portfolio.removeAsset(null);

        assertEquals(1, portfolio.getAssets().size());
    }

    @Test
    void removeAsset_notFound() {
        Portfolio portfolio = new Portfolio();

        portfolio.getAssets().add(new Stock("APLL", 8));
        portfolio.removeAsset(new Stock("BLA", 1));

        assertEquals(1, portfolio.getAssets().size());
    }

    @Test
    void removeAsset_sameSymbol_notClass() {
        Portfolio portfolio = new Portfolio();

        portfolio.getAssets().add(new Stock("APLL", 8));
        portfolio.removeAsset(new Crypto("APLL", 1, "aaaa"));

        assertEquals(1, portfolio.getAssets().size());
    }

    @Test
    void transferTo_moveMoneyCheckingToSavings() {
        CheckingAccount checkingAccount =
                new CheckingAccount("NO123", "Sparebank1", "23", "check", Currency.EUR, 1000);

        SavingAccount savingAccount =
                new SavingAccount("45", "Savings", Currency.EUR, 200);

        checkingAccount.transferTo(savingAccount, 300);

        assertEquals(700, checkingAccount.getBalance());
        assertEquals(500, savingAccount.getBalance());
    }
}
