package org.isep.portfolioproject.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleCreate() {
        String name = nameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String password = passwordField.getText();
        String password2 = confirmPasswordField.getText();

        if (name.isEmpty() || lastname.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Fill all fields").showAndWait();
            return;
        }

        if (!password.equals(password2)) {
            new Alert(Alert.AlertType.ERROR, "Make sure passwords match").showAndWait();
            return;
        }

        File file = new File("data/logindetails.csv");
        boolean newFile = !file.exists() || file.length() == 0;

        String passwordHash = sha256(password);

        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            if (newFile) {
                out.println("name,lastname,passwordHash");
            }
            out.println(name + "," + lastname + "," + passwordHash);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save the user").showAndWait();
            return;
        }

        try {
            String userKey = sanitize(name);
            Path userDir = Path.of("data", "users", userKey);
            Files.createDirectories(userDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Alert(Alert.AlertType.INFORMATION, "Account created").showAndWait();
        nameField.getScene().getWindow().hide();
    }

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

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
