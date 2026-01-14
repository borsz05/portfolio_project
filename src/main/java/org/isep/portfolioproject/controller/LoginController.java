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

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data/logindetails.csv"))) {

            bufferedReader.readLine();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 3) continue;

                String fileName = parts[0].trim();
                String filePassword = parts [2].trim();

                if (fileName.equals(username) && filePassword.equals(password)) {
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
            Parent root = FXMLLoader.load(getClass().getResource("/org/isep/portfolioproject/view/mainDashboard.fxml"));
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
            Parent root = FXMLLoader.load(getClass().getResource("/org/isep/portfolioproject/view/register.fmxl"));
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not open register view").showAndWait();
        }
    }
}
