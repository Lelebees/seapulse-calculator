package com.lelebees.seapulsecalculator.presentation;

import com.lelebees.seapulsecalculator.application.IngredientService;
import com.lelebees.seapulsecalculator.application.RecipeService;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class CalculatorView {
    @FXML
    private Spinner<Integer> amountOfIngredientsInput;
    @FXML
    private Spinner<Integer> totalValueInput;
    @FXML
    private Button startButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ListView<Ingredient> ingredientsListView;
    @FXML
    private ListView<Ingredient> blackListView;
    @FXML
    private Button removeAllFromBlacklist;
    @FXML
    private Button removeSelectedFromBlacklist;
    @FXML
    private Button moveSelectedToBlacklist;
    @FXML
    private ListView<Ingredient> whiteListView;
    @FXML
    private Button removeAllFromWhitelist;
    @FXML
    private Button removeSelectedFromWhitelist;
    @FXML
    private Button moveSelectedToWhitelist;


    @FXML
    protected void initialize() throws IOException {
        // load data
        IngredientService iService = new IngredientService();
        iService.getData();
        // prepare visual components with data
        ingredientsListView.setItems(FXCollections.observableArrayList(IngredientService.getIngredients()));
        progressBar.setProgress(0);
        // Set the max amount of ingredients
        updateIngredientInput();
    }

    @FXML
    protected void onStartButtonClick() {
        //Disable the start button, so we don't run the calculation twice.
        startButton.setDisable(true);
        // Get all the relevant items. we don't look at the blacklist, because we only need the main list to *not* have
        // the blacklisted items
        List<Ingredient> ingredients = ingredientsListView.getItems();
        List<Ingredient> whiteListIngredients = whiteListView.getItems();

        // ensure we don't get wonky generation
        int amountOfIngredients = amountOfIngredientsInput.getValue() - whiteListIngredients.size();
        int whiteListValue = 0;
        for (Ingredient i : whiteListIngredients) {
            whiteListValue += i.getValue();
        }
        int totalValue = totalValueInput.getValue() - whiteListValue;

        //Start a task, so we're using a different thread.
        Task<Void> calculateOptions = new Task<>() {
            @Override
            protected Void call() throws IOException {
                // prepare the calculation
                RecipeService rService = new RecipeService(ingredients, amountOfIngredients, totalValue, whiteListIngredients);
                // allow the progress var in rService to be updated here
                rService.progressProperty().addListener((obs, oldProgress, newProgress) ->
                        updateProgress(newProgress.doubleValue(), 1));
                //Start the calculation!
                rService.findCombinations();
                startButton.setDisable(false);
                return null;
            }

        };
        // connect the progress bar and the progress property from our running task
        progressBar.progressProperty().bind(calculateOptions.progressProperty());

        //tell our system to start the calculation
        Thread calcThread = new Thread(calculateOptions);
        calcThread.setDaemon(true);
        calcThread.start();
    }

    // Probably could have done this drier. cannot be arsed.
    @FXML
    protected void resetBlackList() {
        resetList(blackListView, removeSelectedFromBlacklist, removeAllFromBlacklist, moveSelectedToBlacklist);
    }

    @FXML
    protected void removeSelectedFromBlacklist() {
        removeSelectedFromList(blackListView, removeSelectedFromBlacklist, removeAllFromBlacklist, moveSelectedToBlacklist);
    }

    @FXML
    protected void moveSelectedToBlacklist() {
        moveSelectedItemToList(blackListView, removeSelectedFromBlacklist, removeAllFromBlacklist, moveSelectedToBlacklist);
    }

    //Whitelist operations
    @FXML
    protected void resetWhitelist() {
        resetList(whiteListView, removeSelectedFromWhitelist, removeAllFromWhitelist, moveSelectedToWhitelist);
    }

    @FXML
    protected void removeSelectedFromWhitelist() {
        removeSelectedFromList(whiteListView, removeSelectedFromWhitelist, removeAllFromWhitelist, moveSelectedToWhitelist);
    }

    @FXML
    protected void moveSelectedToWhitelist() {
        moveSelectedItemToList(whiteListView, removeSelectedFromWhitelist, removeAllFromWhitelist, moveSelectedToWhitelist);
    }

    private void resetList(ListView<Ingredient> ListView, Button removeSelectedFromList, Button removeAllFromList, Button moveSelectedToList) {
        ingredientsListView.getItems().addAll(ListView.getItems());
        ListView.setItems(FXCollections.observableArrayList());
        ingredientsListView.getItems().sort(Comparator.comparing(Ingredient::getName));

        //Activate and deactivate the correct buttons to prevent illegal inputs
        removeSelectedFromList.setDisable(true);
        removeAllFromList.setDisable(true);
        // This *shouldn't* ever be true, but I don't trust myself, and not trusting myself has
        // proven to be the best course of action one too many times.
        if (ingredientsListView.getItems().size() > 0) {
            moveSelectedToList.setDisable(false);
        }
        // Update the amount of available ingredients
        updateIngredientInput();
    }

    private void removeSelectedFromList(ListView<Ingredient> ListView, Button removeSelectedFromList, Button removeAllFromList, Button moveSelectedToList) {
        Ingredient itemToMove = ListView.getSelectionModel().getSelectedItem();
        // In theory, we don't need this, but it can never hurt having extra failsafes.
        if (itemToMove == null) {
            return;
        }
        ingredientsListView.getItems().add(itemToMove);
        ListView.getItems().remove(itemToMove);
        ingredientsListView.getItems().sort(Comparator.comparing(Ingredient::getName));

        //Activate and deactivate the correct buttons to prevent illegal inputs
        if (ListView.getItems().size() == 0) {
            removeSelectedFromList.setDisable(true);
            removeAllFromList.setDisable(true);
        }
        if (ingredientsListView.getItems().size() > 0) {
            moveSelectedToList.setDisable(false);
        }
        // Update the amount of available ingredients
       updateIngredientInput();
    }

    private void moveSelectedItemToList(ListView<Ingredient> whiteListView, Button removeSelectedFromWhitelist, Button removeAllFromWhiteList, Button moveSelectedToWhitelist) {
        Ingredient itemToMove = ingredientsListView.getSelectionModel().getSelectedItem();
        // In theory, we don't need this, but it can never hurt having extra failsafes.
        if (itemToMove == null) {
            return;
        }
        whiteListView.getItems().add(itemToMove);
        ingredientsListView.getItems().remove(itemToMove);
        whiteListView.getItems().sort(Comparator.comparing(Ingredient::getName));

        //Activate and deactivate the correct buttons to prevent illegal inputs
        if (whiteListView.getItems().size() > 0) {
            removeSelectedFromWhitelist.setDisable(false);
            removeAllFromWhiteList.setDisable(false);
        }
        if (ingredientsListView.getItems().size() == 0) {
            moveSelectedToWhitelist.setDisable(true);
        }
        // Update the amount of available ingredients
        updateIngredientInput();
    }

    private void updateIngredientInput()
    {
        amountOfIngredientsInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, ingredientsListView.getItems().size()+whiteListView.getItems().size()));
    }
}
