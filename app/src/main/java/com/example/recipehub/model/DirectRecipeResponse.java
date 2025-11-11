package com.example.recipehub.model;

import java.util.List;

public class DirectRecipeResponse {
    private int recipe_id;
    private String title;
    private String description;
    private String difficulty;
    private int prep_time;
    private int serving;
    private String image_url;
    private Category category;
    private List<Step> steps;
    private List<Ingredient> ingredients;
    // добавьте другие поля которые возвращает сервер

    // Геттеры и сеттеры
    public int getRecipe_id() { return recipe_id; }
    public void setRecipe_id(int recipe_id) { this.recipe_id = recipe_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getPrep_time() { return prep_time; }
    public void setPrep_time(int prep_time) { this.prep_time = prep_time; }

    public int getServing() { return serving; }
    public void setServing(int serving) { this.serving = serving; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) { this.steps = steps; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}