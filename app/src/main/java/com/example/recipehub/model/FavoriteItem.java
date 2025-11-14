package com.example.recipehub.model;

public class FavoriteItem {
    private int favorite_id;
    private int user_id;
    private int recipe_id;
    private String createdAt;
    private String updatedAt;
    private Recipe favorite_recipe; // Измените поле с recipe на favorite_recipe

    // Getters and Setters
    public int getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(int favorite_id) {
        this.favorite_id = favorite_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Измените геттер на favorite_recipe
    public Recipe getFavorite_recipe() {
        return favorite_recipe;
    }

    public void setFavorite_recipe(Recipe favorite_recipe) {
        this.favorite_recipe = favorite_recipe;
    }

    // Добавьте этот метод для обратной совместимости
    public Recipe getRecipe() {
        return favorite_recipe;
    }
}