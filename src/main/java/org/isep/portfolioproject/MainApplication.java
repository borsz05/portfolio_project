package org.isep.portfolioproject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import org.isep.portfolioproject.service.ApiService;
import org.isep.portfolioproject.service.DataManager;


import java.io.IOException;
import java.util.Optional;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DataManager dataManager = new DataManager("data/portfolios.json");
        String passphrase = null;
        if (dataManager.isEncrypted()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Decrypt Data");
            dialog.setHeaderText("Enter passphrase to decrypt saved data");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                throw new IllegalStateException("Passphrase required for encrypted data");
            }
            passphrase = result.get();
        }
        //AppState.init(dataManager, new ApiService(), passphrase);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Portfolio Project");
        stage.setScene(scene);
        stage.show();
    }
}