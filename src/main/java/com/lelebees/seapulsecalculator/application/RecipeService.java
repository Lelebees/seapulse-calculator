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

public class RecipeService {
    private final List<Ingredient> list;
    private final int amountOfIngredients;
    private final int targetValue;
    private final List<Ingredient> whiteList;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private final BigInteger totalResults;

    public RecipeService(List<Ingredient> list, int amountOfIngredients, int targetValue, List<Ingredient> whitelist) {
        this.list = list;
        this.amountOfIngredients = amountOfIngredients;
        this.targetValue = targetValue;
        this.whiteList = whitelist;
        this.totalResults = (BigIntegerMath.factorial(list.size()).divide(BigIntegerMath.factorial(amountOfIngredients).multiply(BigIntegerMath.factorial(list.size() - amountOfIngredients))));
    }

    // Thanks to Yanis MANSOUR's article on https://www.yanismansour.com/articles/20211210-Generate-all-combinations
    // For providing this logic in python format
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

    public void findCombinations() throws IOException {
        //Prepare the variables for the calculation
        int n = list.size();
        File outputFile = new File("/data/output.txt");
        FileWriter fileWriter = new FileWriter(outputFile);
        // Quickly end this if we've received bad input
        if (amountOfIngredients < 0 || amountOfIngredients > n) {
            throw new RuntimeException(amountOfIngredients + " must be equal to 0 or positive and less than or equal to " + n);
        }
        // No need to math nothing :)
        if (amountOfIngredients == 0) {
            return;
            // There's only one possible combination. a single recipe with all the ingredients.
        } else if (amountOfIngredients == list.size()) {
            fileWriter = new FileWriter(outputFile);
            list.addAll(whiteList);
            fileWriter.write(new ArrayList<>(List.of(new Recipe(list))).toString());
            fileWriter.close();
            // We can actually math!
        } else {
            // We need this to keep track of the things we've already had.
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < amountOfIngredients; i++) {
                indexes.add(i);
            }
            // Actually decide if the combination meets our requirements
            testCombination(indexes, amountOfIngredients, list, fileWriter, targetValue, whiteList);
            // Set the iteration so we can keep track of where we are
            BigInteger iteration = BigInteger.ONE;
            //CalculatorView.currentProgress = iteration;
            progress.set((iteration.divide(totalResults)).doubleValue());
            // Do the above for the rest of the combinations!
            while (move(indexes, n)) {
                testCombination(indexes, amountOfIngredients, list, fileWriter, targetValue, whiteList);
                iteration = iteration.add(BigInteger.ONE);
                //CalculatorView.currentProgress = iteration;
                progress.set(iteration.doubleValue() / totalResults.doubleValue());
                //Print the iteration over itself, so we know where we are in the process.
                System.out.print("\r" + iteration);
            }
        }
        //It is done. We can close the file and live happily ever after
        fileWriter.close();
    }

    public void testCombination(List<Integer> indexes, int k, List<Ingredient> ingredients, FileWriter fileWriter, int targetValue, List<Ingredient> whiteList) throws IOException {
        // Make a new recipe. We give it a set initial capacity to save time, considering we always "know" how long the list is going to be.
        Recipe tempRecipe = new Recipe(new ArrayList<>(k + 1));
        // Select the ingredients
        for (Integer i : indexes) {
            tempRecipe.addIngredient(ingredients.get(i));
        }
        //Decide if the recipe meets our criteria or not. If it does, we'll keep it
        //If it doesn't, I know a Garbage Collector that'll happily take it!
        if (tempRecipe.getSumOfValues() >= targetValue) {
            for (Ingredient i : whiteList) {
                tempRecipe.addIngredient(i);
            }
            fileWriter.append(tempRecipe.toString());
        }
    }

    public double getProgress() {
        return progressProperty().get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }
}
