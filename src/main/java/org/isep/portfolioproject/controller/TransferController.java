package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TransferController {

    @FXML private ComboBox<String> fromAccountCombo;
    @FXML private ComboBox<String> toAccountCombo;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // MINIMÁL demo adatok
        fromAccountCombo.getItems().addAll("Checking", "Saving", "Broker");
        toAccountCombo.getItems().addAll("Checking", "Saving" , "Broker");
    }

    @FXML
    private void onTransfer() {
        String from = fromAccountCombo.getValue();
        String to = toAccountCombo.getValue();
        String amount = amountField.getText();

        statusLabel.setText("Transfer: " + amount + " from " + from + " to " + to);
    }
}
