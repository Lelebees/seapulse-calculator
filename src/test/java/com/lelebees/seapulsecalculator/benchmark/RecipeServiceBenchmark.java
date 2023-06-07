package com.lelebees.seapulsecalculator.benchmark;

import com.lelebees.seapulsecalculator.application.IngredientService;
import com.lelebees.seapulsecalculator.domain.Ingredient;
import org.openjdk.jmh.annotations.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecipeServiceBenchmark {

    @Benchmark
    @Fork(value = 1, warmups = 2)
    @BenchmarkMode(Mode.Throughput)
    public void original(Context context) throws IOException {
        try (FileWriter writer = new FileWriter(context.originalPath.toString())) {
            context.original.setOutputWriter(writer);
            context.original.findCombinations();
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 2)
    @BenchmarkMode(Mode.Throughput)
    public void experiment(Context context) throws IOException {
        try (FileWriter writer = new FileWriter(context.experimentPath.toString())) {
            context.experiment.setOutputWriter(writer);
            context.experiment.findCombinations();
        }
    }

    @State(Scope.Benchmark)
    public static class Context {

        public Path originalPath = Path.of("data/originalOut.txt");
        public Path experimentPath = Path.of("data/experimentOut.txt");

        public OriginalRecipeService original;
        public ExperimentRecipeService experiment;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            IngredientService iService = new IngredientService();
            iService.getData();
            List<Ingredient> ingredients = IngredientService.getIngredients();
            this.original = new OriginalRecipeService(new ArrayList<>(ingredients), 3, 1, 50, new ArrayList<>());
            this.experiment = new ExperimentRecipeService(new ArrayList<>(ingredients), 3, 1, 50, new ArrayList<>());
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            Files.deleteIfExists(originalPath);
            Files.deleteIfExists(experimentPath);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
