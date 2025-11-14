package com.example.recipehub.model;

import java.util.ArrayList;
import java.util.List;

public class FavoritesResponse {
    private List<FavoriteItem> favorites;

    public List<FavoriteItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteItem> favorites) {
        this.favorites = favorites;
    }

    // Обновите метод для использования favorite_recipe
    public List<Recipe> getFavoriteRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        if (favorites != null) {
            for (FavoriteItem favorite : favorites) {
                if (favorite != null && favorite.getFavorite_recipe() != null) {
                    recipes.add(favorite.getFavorite_recipe());
                }
            }
        }
        return recipes;
    }
}