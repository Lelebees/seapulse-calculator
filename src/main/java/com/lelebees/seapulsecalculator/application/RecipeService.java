package com.lelebees.seapulsecalculator.application;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.Recipe;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.lelebees.seapulsecalculator.AppLauncher.log;

public class RecipeService {
    private final List<Ingredient> list;
    private final int amountOfIngredients;
    private final int minValue;
    private final int maxValue;
    private final List<Ingredient> whiteList;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private final BigInteger totalResults;

    public RecipeService(List<Ingredient> list, int amountOfIngredients, int minValue, int maxValue, List<Ingredient> whitelist) {
        this.list = list;
        this.amountOfIngredients = amountOfIngredients;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.whiteList = whitelist;
        this.totalResults = (BigIntegerMath.factorial(list.size()).divide(BigIntegerMath.factorial(amountOfIngredients).multiply(BigIntegerMath.factorial(list.size() - amountOfIngredients))));
    }

    // Thanks to Yanis MANSOUR's article on https://www.yanismansour.com/articles/20211210-Generate-all-combinations
    // For providing this logic in python format

    /**
     * This function allows us to iterate over a list and get each unique combination of items
     *
     * @param indexes   the indexes to be moved
     * @param maxLength the length of the list we're iterating over
     * @return if there are more combinations left.
     */
    public boolean move(List<Integer> indexes, int maxLength) {
        int length = indexes.size();
        int indexToMove = -1;

        for (int i = 0; i < length; i++) {
            if (indexes.get(length - 1 - i) >= maxLength) {
                throw new RuntimeException("Indexes must be less then the max length");
            }
            if (indexes.get(length - 1 - i) != maxLength - 1 - i) {
                indexToMove = length - 1 - i;
                break;
            }
        }
        if (indexToMove == -1) {
            return false;
        }

        Integer index = indexes.get(indexToMove);
        indexes.set(indexToMove, index + 1);
        for (int i = indexToMove + 1; i < length; i++) {
            indexes.set(i, indexes.get(i - 1) + 1);
        }
        return true;
    }

    /**
     * this function prepares our list for generation, and then calls the move function to generate all possible combinations
     * for given parameters (see constructor)
     *
     * @throws IOException When writing to output file fails, originates from testCombination() calls
     */
    public void findCombinations() throws IOException {
        //Prepare the variables for the calculation
        int n = list.size();
        log("Creating output file...");
        File outputFile = new File("data/output.txt");
        FileWriter fileWriter = new FileWriter(outputFile);
        log("Check if we can start calculation...");
        // Quickly end this if we've received bad input
        if (amountOfIngredients < 0 || amountOfIngredients > n) {
            throw new RuntimeException(amountOfIngredients + " must be equal to 0 or positive and less than or equal to " + n);
        }
        // No need to math nothing :)
        if (amountOfIngredients == 0) {
            fileWriter.write(new ArrayList<>(List.of(new Recipe(whiteList))).toString());
        // There's only one possible combination. a single recipe with all the ingredients.
        } else if (amountOfIngredients == list.size()) {
            list.addAll(whiteList);
            fileWriter.write(new ArrayList<>(List.of(new Recipe(list))).toString());
        // We can actually math!
        } else {
            log("Calculation starting");
            // We need this to keep track of the things we've already had.
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < amountOfIngredients; i++) {
                indexes.add(i);
            }
            // Actually decide if the combination meets our requirements
            testCombination(indexes, amountOfIngredients, list, fileWriter, minValue, maxValue, whiteList);

            // Set the iteration so we can keep track of where we are
            BigInteger iteration = BigInteger.ONE;
            progress.set(iteration.doubleValue() / totalResults.doubleValue());
            log("Expected amount of calculations: " + totalResults);
            // Do the above for the rest of the combinations!
            while (move(indexes, n)) {
                testCombination(indexes, amountOfIngredients, list, fileWriter, minValue, maxValue, whiteList);
                iteration = iteration.add(BigInteger.ONE);
                //CalculatorView.currentProgress = iteration;
                progress.set(iteration.doubleValue() / totalResults.doubleValue());
                //Print the iteration over itself, so we know where we are in the process.
                System.out.print("\r" + iteration);
            }
        }
        //It is done. We can close the file and live happily ever after
        log("Finished calculation");
        fileWriter.close();
        progress.set(1);
    }

    /**
     * This function decides if we want to keep the generated option
     *
     * @param indexes     the selected indexes
     * @param k           the amount of ingredients there are in a {@link Recipe}
     * @param ingredients all the ingredients we can choose from
     * @param fileWriter  the file to be written to (default /data/output.txt)
     * @param minValue    the minimum value for a recipe to be accepted
     * @param whiteList   any whitelisted ingredients to be added to the total
     * @throws IOException if output.txt cannot be written to
     */
    public void testCombination(List<Integer> indexes, int k, List<Ingredient> ingredients, FileWriter fileWriter, int minValue, int maxValue, List<Ingredient> whiteList) throws IOException {
        // Make a new recipe. We give it a set initial capacity to save time, considering we always "know" how long the list is going to be.
        Recipe tempRecipe = new Recipe(new ArrayList<>(k + 1));
        // Select the ingredients
        for (Integer i : indexes) {
            tempRecipe.addIngredient(ingredients.get(i));
        }
        //Decide if the recipe meets our criteria or not. If it does, we'll keep it
        //If it doesn't, I know a Garbage Collector that'll happily take it!
        int valSum = tempRecipe.getSumOfValues();
        if (valSum >= minValue && valSum <= maxValue) {
            for (Ingredient i : whiteList) {
                tempRecipe.addIngredient(i);
            }
            fileWriter.append(tempRecipe.toString());
        }
    }

    // Logic for updating a progress bar
    public double getProgress() {
        return progressProperty().get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }
}
