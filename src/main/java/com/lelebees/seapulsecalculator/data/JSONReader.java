package com.lelebees.seapulsecalculator.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JSONReader {
    private static final Logger logger = LogManager.getLogger(JSONReader.class);

    public static List<Ingredient> Read() throws IOException {
        Gson gson = new Gson();
        logger.debug("Reading ingredients...");
        Reader reader = Files.newBufferedReader(Paths.get("data/Ingredients.json"));
        List<Ingredient> ingredients = gson.fromJson(reader, new TypeToken<List<Ingredient>>() {
        }.getType());
        reader.close();
        logger.debug("Ingredients read");
        return ingredients;
    }
}
