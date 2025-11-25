package com.lelebees.seapulsecalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("Starting UI...");
        // Create and show the UI. Nothing fancy, as it should be.
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("calculator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 630, 361);
        stage.setTitle("Seapulse-Webkinz Calculator");
        stage.setScene(scene);
        stage.show();
    }
}
