package com.lelebees.seapulsecalculator.application;

import com.lelebees.seapulsecalculator.data.JSONReader;
import com.lelebees.seapulsecalculator.domain.Ingredient;

import java.io.IOException;
import java.util.List;

public class IngredientService {
    private static List<Ingredient> ingredients;

    public void getData() throws IOException {
        ingredients = JSONReader.Read();
    }

    public static List<Ingredient> getIngredients() {
        return ingredients;
    }
}
