package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.Transaction;
import org.isep.portfolioproject.model.accounts.Account;
import org.isep.portfolioproject.service.AppState;
import org.isep.portfolioproject.service.MoneyService;
import org.isep.portfolioproject.util.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransferController {

    @FXML private ComboBox<Account> fromAccountCombo;
    @FXML private ComboBox<Account> toAccountCombo;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;
    @FXML private Label fromBalanceLabel;
    @FXML private Label toBalanceLabel;

    private final MoneyService moneyService = new MoneyService();

    @FXML
    public void initialize() {
        fromAccountCombo.getItems().addAll(
                AppState.get().getCheckingAccount(),
                AppState.get().getSavingAccount(),
                AppState.get().getBrokerAccount()
        );
        toAccountCombo.getItems().addAll(
                AppState.get().getCheckingAccount(),
                AppState.get().getSavingAccount(),
                AppState.get().getBrokerAccount()
        );

        fromAccountCombo.setOnAction(e -> updateBalances());
        toAccountCombo.setOnAction(e -> updateBalances());
        updateBalances();
    }

    @FXML
    private void onTransfer() {
        Account from = fromAccountCombo.getValue();
        Account to = toAccountCombo.getValue();
        if (from == null || to == null) {
            statusLabel.setText("Select both accounts.");
            return;
        }
        if (from == to) {
            statusLabel.setText("Choose different accounts.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (Exception e) {
            statusLabel.setText("Enter a valid amount.");
            return;
        }

        try {
            moneyService.transfer(from, to, amount);
            updateBalances();

            Portfolio portfolio = AppState.get().getSelectedPortfolio();
            if (portfolio != null) {
                Transaction tx = Transaction.cash(
                        UUID.randomUUID().toString(),
                        TransactionType.TRANSFER,
                        amount,
                        LocalDateTime.now()
                );
                portfolio.addTransaction(tx);
            }

            AppState.get().save();
            statusLabel.setText("Transferred " + String.format("%.2f", amount) + " from " + from + " to " + to);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void updateBalances() {
        Account from = fromAccountCombo.getValue();
        Account to = toAccountCombo.getValue();
        fromBalanceLabel.setText(from == null ? "" : String.format("%.2f", from.getBalance()));
        toBalanceLabel.setText(to == null ? "" : String.format("%.2f", to.getBalance()));
    }
}
