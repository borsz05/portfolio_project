module org.isep.portfolioproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;

    opens org.isep.portfolioproject to javafx.fxml;
    opens org.isep.portfolioproject.controller to javafx.fxml;
    exports org.isep.portfolioproject;
}