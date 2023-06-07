package com.lelebees.seapulsecalculator.application;

import com.lelebees.seapulsecalculator.domain.Ingredient;
import com.lelebees.seapulsecalculator.domain.IngredientsOutOfBoundsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServiceTest {

    Path path = Path.of("data/test.txt");

    @Test
    @DisplayName("Calculation cannot start if ingredients < 0")
    public void throwsIfLessThen0() {
        assertThrows(IngredientsOutOfBoundsException.class, () -> new RecipeService(new ArrayList<>(), -1, 1, 1, new ArrayList<>(), new FileWriter("data/output.txt")));
    }

    @Test
    @DisplayName("Calculation can start if ingredients = 0")
    public void doesNotThrowIf0() {
        assertDoesNotThrow(() -> new RecipeService(new ArrayList<>(), 0, 1, 1, new ArrayList<>(), new FileWriter("data/output.txt")));
    }

    @ParameterizedTest
    @DisplayName("Calculation Happy Path")
    @MethodSource("mainTestData")
    void calcWorks(List<Ingredient> ingredientList, int amount, int min, int max, List<Ingredient> whiteList, long expectedSize) throws IOException {

        FileWriter fileWriter = new FileWriter(path.toString());
        RecipeService recipeService = new RecipeService(ingredientList, amount, min, max, whiteList, fileWriter);
        recipeService.findCombinations();
        assertEquals(expectedSize, Files.lines(path).count());
    }

    private static Stream<Arguments> mainTestData() {
        return Stream.of(
                Arguments.of(List.of(new Ingredient("test1", 1), new Ingredient("test2", 2)), 1, 1, 5, new ArrayList<>(), 2L),
                Arguments.of(List.of(
                        new Ingredient("test3", 1),
                        new Ingredient("test4", 2),
                        new Ingredient("test5", 3)),
                        2, 4, 100, new ArrayList<>(), 2L),
                Arguments.of(List.of(
                        new Ingredient("test1", 4),
                        new Ingredient("test2", 2),
                        new Ingredient("test3", 6),
                        new Ingredient("test4", 3),
                        new Ingredient("test5", 1)),
                        2, 4, 100, new ArrayList<>(), 9L
                )
        );
    }

    @AfterEach
    void deleteStuff() throws IOException {
        Files.deleteIfExists(path);
    }
}