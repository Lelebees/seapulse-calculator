package com.lelebees.seapulsecalculator;

import com.lelebees.seapulsecalculator.application.IngredientService;
import com.lelebees.seapulsecalculator.application.RecipeService;
import com.lelebees.seapulsecalculator.domain.Recipe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        IngredientService iService = new IngredientService();
        iService.getData();
        RecipeService rService = new RecipeService();
        List<Recipe> recipeList = rService.findCombinations(IngredientService.getIngredients(), 5, 30);
        System.out.println(recipeList.size());
        System.out.println(recipeList.get(2));
        File resultFile = new File("result.txt");
        FileWriter writer = new FileWriter("result.txt");
        writer.write(recipeList.toString());
        writer.close();
    }
}
