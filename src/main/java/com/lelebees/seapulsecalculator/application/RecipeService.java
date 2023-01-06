package com.lelebees.seapulsecalculator.application;

import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.Recipe;

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

    public List<Recipe> findCombinations(List<Ingredient> list, int k, int targetValue) {
        int n = list.size();
        List<Recipe> combinations = new ArrayList<>();
        if (k < 0 || k > n) {
            throw new RuntimeException(k + " must be equal to 0 or positive and less than or equal to " + n);
        }
        if (k == 0) {
            return new ArrayList<>(k);
        } else if (k == 1) {
            return null;
        } else if (k == list.size()) {
            return new ArrayList<>(List.of(new Recipe(list)));
        } else {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                indexes.add(i);
            }
            Recipe tempRecipe = new Recipe(new ArrayList<>(k+1));
            for (Integer i : indexes) {
                tempRecipe.addIngredient(list.get(i));
            }
            if (tempRecipe.getSumOfValues() >= targetValue) {
                combinations.add(tempRecipe);
            }
            long totalResults = (long) Math.pow(list.size(), k);
            long iteration = 1;
            System.out.print("\r");
            System.out.print("1/"+totalResults);
            while (move(indexes, n)) {
                Recipe tempRecipe2 = new Recipe(new ArrayList<>(k+1));
                for (Integer i : indexes) {
                    tempRecipe2.addIngredient(list.get(i));
                }
                if (tempRecipe2.getSumOfValues() >= targetValue) {
                    combinations.add(tempRecipe2);
                }
                iteration +=1;
                System.out.print("\r");
                System.out.print(iteration+"/"+totalResults);
                if (iteration % 1000000 == 0) {
                    System.gc();
                }
            }
            return combinations;
        }
    }
}
