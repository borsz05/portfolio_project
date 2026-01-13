package org.isep.portfolioproject.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import  java.io.*;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;


    @FXML
    private void handleCreate() {
        String name = nameField.getText();
        String lastname = lastnameField.getText();
        String password = passwordField.getText();
        String password2 = confirmPasswordField.getText();

        if(name.isEmpty() || lastname.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Fill all fields");
            alert.showAndWait();
            return;
        }

        if(!password.equals(password2)) {
            new Alert(Alert.AlertType.ERROR, "Make sure passwords match").showAndWait();
            return;
        }

        File file = new File("data/logindetails.csv");
        boolean newFile = !file.exists() || file.length() == 0;

        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            if (newFile) {
                out.println("name,lastname,password");
            }

            out.println(name + "," + lastname + "," + password);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save the user").showAndWait();
            return;
        }

        new Alert(Alert.AlertType.INFORMATION, "Account created");

        nameField.getScene().getWindow().hide();
    }
}
