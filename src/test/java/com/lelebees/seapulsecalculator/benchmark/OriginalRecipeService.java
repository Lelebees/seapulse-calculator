package com.lelebees.seapulsecalculator.benchmark;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.IngredientsOutOfBoundsException;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.lelebees.seapulsecalculator.AppLauncher.logger;

public class OriginalRecipeService {
    private final List<Ingredient> ingredientList;
    private final int requestedAmountOfIngredients;
    private final int minValue;
    private final int maxValue;
    private final List<Ingredient> whiteList;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private final BigInteger totalResults;
    private Writer fileWriter;
    private BigInteger iteration;
    private final int whiteListValue;


    public OriginalRecipeService(List<Ingredient> ingredientList, int requestedAmountOfIngredients, int minValue, int maxValue, List<Ingredient> whitelist, Writer writer) {
        this.ingredientList = ingredientList;
        this.whiteList = whitelist;
        this.requestedAmountOfIngredients = requestedAmountOfIngredients - whitelist.size();
        this.whiteListValue = whitelist.stream().mapToInt(Ingredient::getValue).sum();
        this.minValue = minValue - whiteListValue;
        this.maxValue = maxValue - whiteListValue;
        this.iteration = BigInteger.ZERO;
        this.fileWriter = writer;

        logger.debug("Checking if we can start calculation...");
        if (requestedAmountOfIngredients < 0 || requestedAmountOfIngredients > ingredientList.size()) {
            throw new IngredientsOutOfBoundsException(requestedAmountOfIngredients + " must be equal to 0 or positive and less than or equal to " + ingredientList.size());
        }

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
        logger.info("Expected amount of calculations: {}", totalResults);
        try {
            if (requestedAmountOfIngredients == 0) {
                writeRecipe(whiteList);
                return;
            }
            if (requestedAmountOfIngredients == ingredientList.size()) {
                ingredientList.addAll(whiteList);
                writeRecipe(ingredientList);
                return;
            }

            IntStream.range(0, ingredientList.size())
                    .parallel()
                    .forEach(i -> {
                        List<Ingredient> currentCombination = new ArrayList<>();
                        currentCombination.add(ingredientList.get(i));
                        try {
                            generateCombinations(currentCombination, i + 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } finally {
            logger.debug("Finished calculation");
            progress.set(1);
            fileWriter.close();
        }
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
        iteration = iteration.add(BigInteger.ONE);
        if (iteration.mod(BigInteger.valueOf(1000)).equals(BigInteger.ZERO))
        {
            progress.set(iteration.doubleValue() / totalResults.doubleValue());
        }
    }

    /**
     * This function decides if we want to keep the generated option
     *
     * @param ingredients the selected ingredients
     * @throws IOException if output.txt cannot be written to
     */
    private void testCombination(List<Ingredient> ingredients) throws IOException {
        int sumOfValues = ingredients.stream().mapToInt(Ingredient::getValue).sum();
        if (sumOfValues < minValue || sumOfValues > maxValue) {
            return;
        }
        ingredients.addAll(whiteList);
        write(ingredients + ": " + (sumOfValues + whiteListValue));
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

    private void writeRecipe(List<Ingredient> ingredients) throws IOException {
        write(ingredients + ": " + ingredients.stream().mapToInt(Ingredient::getValue).sum());
    }

    private void write(String text) throws IOException {
        fileWriter.append(text).append("\n");
    }

    public void setFileWriter(Writer fileWriter){
        this.fileWriter = fileWriter;
    }
}
