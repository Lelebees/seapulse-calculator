package com.lelebees.seapulsecalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.lelebees.seapulsecalculator.AppLauncher.log;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        log("Starting UI...");
        // Create and show the UI. Nothing fancy, as it should be.
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("calculator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 630, 361);
        stage.setTitle("Seapulse-Webkinz Calculator");
        stage.setScene(scene);
        stage.show();
    }
}
