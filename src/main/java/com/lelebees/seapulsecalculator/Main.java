package com.lelebees.seapulsecalculator;

import com.lelebees.seapulsecalculator.application.IngredientService;
import com.lelebees.seapulsecalculator.application.RecipeService;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        IngredientService iService = new IngredientService();
        iService.getData();
        RecipeService rService = new RecipeService();
        File output = rService.findCombinations(IngredientService.getIngredients(), 5, 30);
        System.out.println("\n"+output);
    }
}
