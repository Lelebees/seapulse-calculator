package com.lelebees.seapulsecalculator.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lelebees.seapulsecalculator.domain.Ingredient;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.lelebees.seapulsecalculator.AppLauncher.log;

public class JSONReader {

    public static List<Ingredient> Read() throws IOException {
        Gson gson = new Gson();
        log("Reading ingredients...");
        Reader reader = Files.newBufferedReader(Paths.get("data/Ingredients.json"));
        List<Ingredient> ingredients = gson.fromJson(reader, new TypeToken<List<Ingredient>>() {
        }.getType());
        reader.close();
        log("Ingredients read");
        return ingredients;
    }
}
