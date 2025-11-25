package com.lelebees.seapulsecalculator.application;

import com.lelebees.seapulsecalculator.data.JSONReader;
import com.lelebees.seapulsecalculator.domain.Ingredient;

import java.io.IOException;
import java.util.List;

public class IngredientService {
    private List<Ingredient> ingredients;

    public IngredientService(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public IngredientService() throws IOException {
        getData();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    private void getData() throws IOException {
        ingredients = JSONReader.Read();
    }
}
