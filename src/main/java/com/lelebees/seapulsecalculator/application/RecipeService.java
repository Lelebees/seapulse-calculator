package com.lelebees.seapulsecalculator.application;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.IngredientsOutOfBoundsException;
import com.lelebees.seapulsecalculator.domain.Recipe;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.lelebees.seapulsecalculator.AppLauncher.logger;

public class RecipeService {
    private final List<Ingredient> ingredientList;
    private final int requestedAmountOfIngredients;
    private final int minValue;
    private final int maxValue;
    private final List<Ingredient> whiteList;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private final BigInteger totalResults;
    private final FileWriter fileWriter;
    private long iteration;


    public RecipeService(List<Ingredient> ingredientList, int requestedAmountOfIngredients, int minValue, int maxValue, List<Ingredient> whitelist, FileWriter fileWriter) {
        this.ingredientList = ingredientList;
        this.requestedAmountOfIngredients = requestedAmountOfIngredients;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.whiteList = whitelist;
        this.iteration = 0;
        this.fileWriter = fileWriter;

        logger.debug("Checking if we can start calculation...");
        if (requestedAmountOfIngredients < 0 || requestedAmountOfIngredients > ingredientList.size()) {
            throw new IngredientsOutOfBoundsException(requestedAmountOfIngredients + " must be equal to 0 or positive and less than or equal to " + ingredientList.size());
        }

        // (iList.size()!) / (amnt! * (iList.size() - amnt)!)
        this.totalResults = (BigIntegerMath.factorial(ingredientList.size())
                .divide(BigIntegerMath.factorial(requestedAmountOfIngredients)
                        .multiply(BigIntegerMath.factorial(ingredientList.size() - requestedAmountOfIngredients))
                )
        );
    }

    // Thanks to Yanis MANSOUR's article on https://www.yanismansour.com/articles/20211210-Generate-all-combinations
    // For providing this logic in python format

    /**
     * this function prepares our list for generation, and then calls the move function to generate all possible combinations
     * for given parameters (see constructor)
     *
     * @throws IOException When writing to output file fails, originates from testCombination() calls, and log calls
     */
    public void findCombinations() throws IOException {
        logger.debug("Calculation starting");
        logger.info("Expected amount of calculations: " + totalResults);

        if (requestedAmountOfIngredients == 0) {
            fileWriter.write(List.of(new Recipe(whiteList)).toString());
            finish();
            return;
        } else if (requestedAmountOfIngredients == ingredientList.size()) {
            ingredientList.addAll(whiteList);
            fileWriter.write(List.of(new Recipe(ingredientList)).toString());
            finish();
            return;
        }

        List<Ingredient> currentCombination = new ArrayList<>();
        generateCombinations(currentCombination, 0);

        finish();
    }

    /**
     * Recursive function to generate combinations using backtracking
     *
     * @param currentCombination The current combination being generated
     * @param start              The index to start considering ingredients from
     * @throws IOException When writing to output file fails
     */
    private void generateCombinations(List<Ingredient> currentCombination, int start) throws IOException {
        if (currentCombination.size() == requestedAmountOfIngredients) {
            testCombination(currentCombination);
            return;
        }

        for (int i = start; i < ingredientList.size(); i++) {
            currentCombination.add(ingredientList.get(i));
            generateCombinations(currentCombination, i + 1);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    private void updateProgress() {
        iteration++;
        progress.set((double) iteration / totalResults.doubleValue());
    }

    private void finish() throws IOException {
        logger.debug("Finished calculation");
        progress.set(1);
        fileWriter.close();
    }

    /**
     * This function decides if we want to keep the generated option
     *
     * @param ingredients the selected indexes
     * @throws IOException if output.txt cannot be written to
     */
    private void testCombination(List<Ingredient> ingredients) throws IOException {
        Recipe tempRecipe = new Recipe(ingredients);

        int valSum = tempRecipe.getSumOfValues();
        if (valSum >= minValue && valSum <= maxValue) {
            tempRecipe.addAll(whiteList);
            fileWriter.append(tempRecipe.toString());
        }

        updateProgress();
    }

    // Logic for updating a progress bar
    @SuppressWarnings("unused")
    public double getProgress() {
        return progressProperty().get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }
}
