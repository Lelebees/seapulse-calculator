package com.lelebees.seapulsecalculator.benchmark;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.IngredientsOutOfBoundsException;
import com.lelebees.seapulsecalculator.domain.Recipe;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
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
    private final FileWriter fileWriter;
    private BigInteger iteration;


    public OriginalRecipeService(List<Ingredient> ingredientList, int requestedAmountOfIngredients, int minValue, int maxValue, List<Ingredient> whitelist, FileWriter fileWriter) throws IOException {
        this.ingredientList = ingredientList;
        this.requestedAmountOfIngredients = requestedAmountOfIngredients;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.whiteList = whitelist;
        this.iteration = BigInteger.ZERO;
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
     * This function allows us to iterate over a list and get each unique combination of items
     *
     * @param indexes   the indexes to be moved
     * @param maxLength the length of the list we're iterating over
     * @return if there are more combinations left.
     */
    private boolean move(List<Integer> indexes, int maxLength) {
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

        List<Integer> indexes = IntStream.range(0, requestedAmountOfIngredients).boxed().collect(Collectors.toList());
        testCombination(indexes);

        while (move(indexes, ingredientList.size())) {
            testCombination(indexes);
            System.out.print("\r" + iteration);
        }
        finish();
    }

    private void updateProgress() {
        iteration = iteration.add(BigInteger.ONE);
        progress.set(iteration.doubleValue() / totalResults.doubleValue());
    }

    private void finish() throws IOException {
        logger.debug("Finished calculation");
        progress.set(1);
        fileWriter.close();
    }

    /**
     * This function decides if we want to keep the generated option
     *
     * @param indexes the selected indexes
     * @throws IOException if output.txt cannot be written to
     */
    private void testCombination(List<Integer> indexes) throws IOException {
        Recipe tempRecipe = new Recipe();
        // Select the ingredients
        indexes.stream().map(ingredientList::get).forEach(tempRecipe::add);

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
