package com.lelebees.seapulsecalculator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    @Test
    @DisplayName("Correct Sum is returned")
    void givesCorrectSum(){
        List<Ingredient> ingredientList = List.of(new Ingredient("a", 1), new Ingredient("b", 3), new Ingredient("c", 5));
        Recipe recipe = new Recipe(ingredientList);
        assertEquals(9, recipe.getSumOfValues());
    }

}