package com.lelebees.seapulsecalculator.domain;

import java.util.Objects;

/**
 * Class to store the data of an Ingredient.
 */
public final class Ingredient {
    private final String name;
    private final int value;


    /**
     * @param name  name of the Ingredient
     * @param value value of the Ingredient
     */
    public Ingredient(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Ingredient) obj;
        return Objects.equals(this.name, that.name) && this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        // Debating on whether I should remove the value display to save some megabytes off big solutions
        return name + ";" + value;
    }

}
