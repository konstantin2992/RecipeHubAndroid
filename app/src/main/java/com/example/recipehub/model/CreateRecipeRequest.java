package com.example.recipehub.model;

import com.google.gson.Gson;
import java.util.List;

public class CreateRecipeRequest {
    private int category_id;
    private String title;
    private String description;
    private String difficulty;
    private int prep_time;
    private int serving;
    private List<Step> steps;
    private List<Ingredient> ingredients;

    public CreateRecipeRequest(int category_id, String title, String description,
                               String difficulty, int prep_time, int serving,
                               List<Step> steps, List<Ingredient> ingredients) {
        this.category_id = category_id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.prep_time = prep_time;
        this.serving = serving;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    // Геттеры
    public int getCategoryId() { return category_id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getPrepTime() { return prep_time; }
    public int getServing() { return serving; }
    public List<Step> getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }

    // Методы для получения JSON
    public String getStepsJson() {
        return new Gson().toJson(steps);
    }

    public String getIngredientsJson() {
        return new Gson().toJson(ingredients);
    }
}