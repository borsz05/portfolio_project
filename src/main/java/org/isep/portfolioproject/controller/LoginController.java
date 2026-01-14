package org.isep.portfolioproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if(username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Fill in both fields").showAndWait();
            return;
        }

        boolean ok = false;
        String passwordHash = sha256(password);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data/logindetails.csv"))) {

            bufferedReader.readLine();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 3) continue;

                String fileName = parts[0].trim();
                String filePasswordHash = parts [2].trim();

                if (fileName.equals(username) && filePasswordHash.equals(passwordHash)) {
                    ok = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not read the data").showAndWait();
            return;
        }

        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Wrong username or password").showAndWait();
            return;
        }


        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/isep/portfolioproject/dashboard.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            usernameField.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "could not open dashboard view").showAndWait();
        }
    }

    @FXML
    private void handleOpenRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/isep/portfolioproject/register.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not open register view").showAndWait();
        }
    }

    // SHA-256 hash
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
