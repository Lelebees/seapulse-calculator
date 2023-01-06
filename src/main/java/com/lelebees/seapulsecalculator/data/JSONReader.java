package com.lelebees.seapulsecalculator.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lelebees.seapulsecalculator.domain.Ingredient;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JSONReader {

    public static List<Ingredient> Read() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/com/lelebees/seapulsecalculator/data/Ingredients.json"));
        List<Ingredient> ingredients = gson.fromJson(reader, new TypeToken<List<Ingredient>>() {
        }.getType());
        reader.close();
        return ingredients;
    }
}
