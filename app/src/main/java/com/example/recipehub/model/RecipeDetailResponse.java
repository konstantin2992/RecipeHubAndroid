package com.example.recipehub.model;


public class RecipeDetailResponse {
    // Измените поле с recipe на data или используйте то что возвращает сервер
    private Recipe data;
    private String message;

    public Recipe getRecipe() {
        return data;
    }

    public void setData(Recipe data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}