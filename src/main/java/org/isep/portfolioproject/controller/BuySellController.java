package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BuySellController {

    @FXML private ComboBox<String> assetTypeCombo;
    @FXML private TextField symbolField;
    @FXML private TextField quantityField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        assetTypeCombo.getItems().addAll("STOCK", "CRYPTO");
        assetTypeCombo.setValue("STOCK");
    }

    @FXML
    private void onBuy() {
        statusLabel.setText("BUY " + quantityField.getText() + " " + symbolField.getText()
                + " (" + assetTypeCombo.getValue() + ")");
    }

    @FXML
    private void onSell() {
        statusLabel.setText("SELL " + quantityField.getText() + " " + symbolField.getText()
                + " (" + assetTypeCombo.getValue() + ")");
    }
}
