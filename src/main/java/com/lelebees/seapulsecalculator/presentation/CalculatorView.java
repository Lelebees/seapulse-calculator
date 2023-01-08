package com.lelebees.seapulsecalculator.presentation;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.application.IngredientService;
import com.lelebees.seapulsecalculator.application.RecipeService;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;

import java.io.IOException;
import java.math.BigInteger;
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
    private Label progressLabel;

    public static BigInteger currentProgress = BigInteger.valueOf(0);

    @FXML
    protected void onStartButtonClick() throws IOException {
        //Disable the start button, so we don't do an oopsie.
        startButton.setDisable(true);
        IngredientService iService = new IngredientService();
        iService.getData();
        List<Ingredient> ingredients = IngredientService.getIngredients();
        int amountOfIngredients = amountOfIngredientsInput.getValue();
        BigInteger totalResults = (BigIntegerMath.factorial(ingredients.size()).divide(BigIntegerMath.factorial(amountOfIngredients).multiply(BigIntegerMath.factorial(ingredients.size() - amountOfIngredients))));
        RecipeService rService = new RecipeService(ingredients, amountOfIngredients, totalValueInput.getValue());
        rService.start();
        while(rService.isAlive())
        {
            progressBar.setProgress(currentProgress.divide(totalResults).doubleValue());
        }
        startButton.setDisable(false);
    }
}
