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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lelebees.seapulsecalculator.AppLauncher.logger;

public class CalculatorView {
    Map<ListView<Ingredient>, List<Button>> listButtons;
    List<Button> whiteListRemoveButtons;
    List<Button> blackListRemoveButtons;
    List<Button> mainListRemoveButtons;
    @FXML
    private Spinner<Integer> amountOfIngredientsInput;
    @FXML
    private Spinner<Integer> totalValueInput;
    @FXML
    private Spinner<Integer> maxValueInput;
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
        whiteListRemoveButtons = List.of(removeAllFromWhitelist, removeSelectedFromWhitelist);
        blackListRemoveButtons = List.of(removeAllFromBlacklist, removeSelectedFromBlacklist);
        mainListRemoveButtons = List.of(moveSelectedToBlacklist, moveSelectedToWhitelist);
        listButtons = new HashMap<>();
        listButtons.put(ingredientsListView, mainListRemoveButtons);
        listButtons.put(whiteListView, whiteListRemoveButtons);
        listButtons.put(blackListView, blackListRemoveButtons);

        IngredientService iService = new IngredientService();
        iService.getData();
        logger.debug("Writing to visual components...");
        ingredientsListView.setItems(FXCollections.observableArrayList(IngredientService.getIngredients()));
        progressBar.setProgress(0);
        amountOfIngredientsInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, ingredientsListView.getItems().size() + whiteListView.getItems().size()));
    }

    @FXML
    protected void onStartButtonClick() {
        startButton.setDisable(true);
        logger.debug("asked to start Calculation");
        // Get all the relevant items. we don't look at the blacklist, because we only need the main list to *not* have
        // the blacklisted items
        List<Ingredient> ingredients = ingredientsListView.getItems();
        List<Ingredient> whiteListIngredients = whiteListView.getItems();

        int amountOfIngredients = amountOfIngredientsInput.getValue() - whiteListIngredients.size();
        int whiteListValue = whiteListIngredients.stream().mapToInt(Ingredient::getValue).sum();
        int minValue = totalValueInput.getValue() - whiteListValue;
        int maxValue = maxValueInput.getValue() - whiteListValue;
        logger.info("Preparing calculation with ingredients: " + ingredients + "; amount: " + amountOfIngredients + "; min:" + minValue + "; max:" + maxValue + "; whitelist: " + whiteListIngredients);
        Task<Void> calculateOptions = new Task<>() {
            @Override
            protected Void call() throws IOException {
                RecipeService rService = new RecipeService(ingredients, amountOfIngredients, minValue, maxValue, whiteListIngredients);
                rService.progressProperty().addListener((obs, oldProgress, newProgress) ->
                        updateProgress(newProgress.doubleValue(), 1));
                rService.findCombinations();
                startButton.setDisable(false);
                return null;
            }

        };
        logger.debug("Progress bar bound to task.");
        progressBar.progressProperty().bind(calculateOptions.progressProperty());

        Thread calcThread = new Thread(calculateOptions);
        calcThread.setDaemon(true);
        logger.debug("starting calculation");
        calcThread.start();
    }

    @FXML
    protected void resetBlackList() {
        moveAllItems(blackListView, ingredientsListView);
    }

    @FXML
    protected void removeSelectedFromBlacklist() {
        moveSelectedItem(blackListView, ingredientsListView);
    }

    @FXML
    protected void moveSelectedToBlacklist() {
        moveSelectedItem(ingredientsListView, blackListView);
    }

    //Whitelist operations
    @FXML
    protected void resetWhitelist() {
        moveAllItems(whiteListView, ingredientsListView);
    }

    @FXML
    protected void removeSelectedFromWhitelist() {
        moveSelectedItem(whiteListView, ingredientsListView);
    }

    @FXML
    protected void moveSelectedToWhitelist() {
        moveSelectedItem(ingredientsListView, whiteListView);
    }

    private void updateIngredientInput() {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = (SpinnerValueFactory.IntegerSpinnerValueFactory) amountOfIngredientsInput.getValueFactory();
        factory.setMax(ingredientsListView.getItems().size() + whiteListView.getItems().size());
    }


    private void moveSelectedItem(ListView<Ingredient> origin, ListView<Ingredient> destination) {
        Ingredient item = origin.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        destination.getItems().add(item);
        destination.getItems().sort(Comparator.comparing(Ingredient::getName));
        origin.getItems().remove(item);
        updateMoveButtons(origin, destination);
        updateIngredientInput();
    }

    private void moveAllItems(ListView<Ingredient> origin, ListView<Ingredient> destination) {
        List<Ingredient> ingredients = origin.getItems();
        destination.getItems().addAll(ingredients);
        destination.getItems().sort(Comparator.comparing(Ingredient::getName));
        origin.setItems(FXCollections.observableArrayList());
        updateMoveButtons(origin, destination);
        updateIngredientInput();
    }

    private void updateMoveButtons(ListView<Ingredient> origin, ListView<Ingredient> destination) {
        if (origin.getItems().isEmpty()) {
            List<Button> originRemoveButtons = listButtons.get(origin);
            originRemoveButtons.forEach(button -> button.setDisable(true));
        }
        if (!destination.getItems().isEmpty()) {
            List<Button> destinationRemoveButtons = listButtons.get(destination);
            destinationRemoveButtons.forEach(button -> button.setDisable(false));
        }
    }
}
