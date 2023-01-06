package com.lelebees.seapulsecalculator.domain;

import java.util.List;

/**
 * Class to store the relevant data/behaviour for a recipe (like the value of its ingredients)
 */
public class Recipe {

    private final List<Ingredient> ingredients;

    /**
     * Default constructor
     *
     * @param ingredients the {@link Ingredient}s of the recipe
     */
    public Recipe(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * This function calculates the sum of the values of the recipe's ingredients.
     *
     * @return The total value of this recipe's {@link Ingredient}s
     */
    public int getSumOfValues() {
        int value = 0;
        for (Ingredient i : this.ingredients) {
            value += i.getValue();
        }
        return value;
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    @Override
    public String toString() {
        return "Ingredients: " + ingredients + ", Total value: "+getSumOfValues();
    }
}
