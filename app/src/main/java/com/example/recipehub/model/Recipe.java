package com.example.recipehub.model;

import java.util.List;
public class Recipe {
    private int recipe_id;
    private int category_id;
    private String title;
    private String description;
    private String difficulty;
    private int prep_time;
    private int serving;
    private String image_url;

    private Category category;
    private List<Step> steps;
    private List<Ingredient> ingredients;

    public int getRecipe_id() { return recipe_id; }
    public int getCategory_id() { return category_id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getPrep_time() { return prep_time; }
    public int getServing() { return serving; }
    public String getImage_url() { return image_url; }
    public Category getCategory() { return category; }
    public List<Step> getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }
}
