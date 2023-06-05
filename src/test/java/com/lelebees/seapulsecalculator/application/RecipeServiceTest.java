package com.lelebees.seapulsecalculator.application;

import com.lelebees.seapulsecalculator.domain.IngredientsOutOfBoundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServiceTest {

    @Test
    @DisplayName("Calculation cannot start if ingredients < 0")
    public void throwsIfLessThen0() {
        assertThrows(IngredientsOutOfBoundsException.class, () -> new RecipeService(new ArrayList<>(), -1, 1, 1, new ArrayList<>()));
    }
}