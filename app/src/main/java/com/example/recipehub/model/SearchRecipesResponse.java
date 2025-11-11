package com.example.recipehub.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchRecipesResponse {
    // Разные возможные названия поля
    @SerializedName("recipes")
    private List<Recipe> recipes;

    @SerializedName("data")
    private List<Recipe> data;

    @SerializedName("results")
    private List<Recipe> results;

    @SerializedName("items")
    private List<Recipe> items;

    public List<Recipe> getRecipes() {
        if (recipes != null) return recipes;
        if (data != null) return data;
        if (results != null) return results;
        if (items != null) return items;
        return null;
    }
}