package com.lelebees.seapulsecalculator.application;

import com.google.common.collect.ImmutableList;
import com.lelebees.seapulsecalculator.data.JSONReader;
import com.lelebees.seapulsecalculator.domain.Ingredient;

import java.io.IOException;
import java.util.List;

public class IngredientService {
    private static List<Ingredient> ingredients;

    /**
     * Create and return an immutable list of all ingredients
     *
     * @return Immutable list of all ingredients
     */
    // We're using an immutable list, so we can shunt stuff out of the list in the RecipeService, without fucking us over!
    public ImmutableList<Ingredient> getAllIngredients() {
        return ImmutableList.copyOf(ingredients);
    }

    //Get all the ingredients in local storage.
    public void getData() throws IOException {
        ingredients = JSONReader.Read();
    }

    public static List<Ingredient> getIngredients() {
        return ingredients;
    }
}
