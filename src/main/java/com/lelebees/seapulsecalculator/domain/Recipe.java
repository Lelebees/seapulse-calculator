package com.lelebees.seapulsecalculator.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store the relevant data/behaviour for a recipe (like the value of its ingredients)
 */
public class Recipe extends ArrayList<Ingredient> {

    /**
     * Default constructor
     *
     * @param ingredients the {@link Ingredient}s of the recipe
     */
    public Recipe(List<Ingredient> ingredients) {
        super(ingredients);
    }
    public Recipe()
    {
        super();
    }

    /**
     * This function calculates the sum of the values of the recipe's ingredients.
     *
     * @return The total value of this recipe's {@link Ingredient}s
     */
    public int getSumOfValues() {
        return super.stream().mapToInt(Ingredient::getValue).sum();
    }

    @Override
    public String toString() {
        return super.toString() + ": "+getSumOfValues() +"\n";
    }
}
