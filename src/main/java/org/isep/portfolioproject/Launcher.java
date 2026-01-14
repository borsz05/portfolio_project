package org.isep.portfolioproject;

import javafx.application.Application;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.assets.Stock;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.util.TransactionType;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Launcher {
    public static void main(String[] args) throws IOException {

        Portfolio p = new Portfolio();
        p.setName("Test Portfolio");
        p.setDescription("Just to test CSV");



        Transaction transaction = new Transaction();
        transaction.setId("O1");
        transaction.setAsset(new Stock("AAPL", 0));
        transaction.setQuantity(5);
        transaction.setPrice(0.0);
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.BUY);

        p.addTransaction(transaction);

        p.writeToCsv();

        Application.launch(MainApplication.class, args);

    }
}