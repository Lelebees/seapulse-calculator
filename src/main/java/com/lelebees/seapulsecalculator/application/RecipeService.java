package com.lelebees.seapulsecalculator.application;

import com.google.common.math.BigIntegerMath;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.Recipe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RecipeService {
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

    public File findCombinations(List<Ingredient> list, int amountOfIngredients, int targetValue) throws IOException {
        int n = list.size();
        File outputFile = new File("output.txt");
        FileWriter fileWriter = new FileWriter(outputFile);
        if (amountOfIngredients < 0 || amountOfIngredients > n) {
            throw new RuntimeException(amountOfIngredients + " must be equal to 0 or positive and less than or equal to " + n);
        }
        if (amountOfIngredients == 0) {
            return outputFile;
        } else if (amountOfIngredients == 1) {
            return null;
        } else if (amountOfIngredients == list.size()) {
            fileWriter = new FileWriter(outputFile);
            fileWriter.write(new ArrayList<>(List.of(new Recipe(list))).toString());
            fileWriter.close();
        } else {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < amountOfIngredients; i++) {
                indexes.add(i);
            }
            testCombination(indexes, amountOfIngredients, list, fileWriter, targetValue);
            BigInteger totalResults = (BigIntegerMath.factorial(list.size()).divide(BigIntegerMath.factorial(amountOfIngredients).multiply(BigIntegerMath.factorial(list.size() - amountOfIngredients))));
            long iteration = 1;
            System.out.print("\r");
            System.out.print("1/" + totalResults);
            while (move(indexes, n)) {
                testCombination(indexes, amountOfIngredients, list, fileWriter, targetValue);
                iteration += 1;
                System.out.print("\r");
                System.out.print(iteration + "/" + totalResults);
            }
        }
        fileWriter.close();
        return outputFile;
    }

    public void testCombination(List<Integer> indexes, int k, List<Ingredient> ingredients, FileWriter fileWriter, int targetValue) throws IOException {
        Recipe tempRecipe = new Recipe(new ArrayList<>(k + 1));
        for (Integer i : indexes) {
            tempRecipe.addIngredient(ingredients.get(i));
        }
        if (tempRecipe.getSumOfValues() >= targetValue) {
            fileWriter.append(tempRecipe.toString());
        }
    }
}
