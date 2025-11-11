package com.example.recipehub.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.model.Category;
import com.example.recipehub.model.DirectRecipeResponse;
import com.example.recipehub.model.Ingredient;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.model.RecipeDetailResponse;
import com.example.recipehub.model.Step;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvDescription, tvDifficulty, tvCategory, tvPrepTime, tvServings;
    private RecyclerView rvIngredients, rvSteps;
    private ImageView ivRecipeImage;
    private ImageButton btnBack;
    private ApiService api;
    private static final String TAG = "RecipeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initViews();
        api = RetrofitClient.getInstance().create(ApiService.class);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Log.d(TAG, "Received recipe_id: " + recipeId);

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        } else {
            Toast.makeText(this, "Recipe ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrepTime = findViewById(R.id.tvPrepTime);
        tvServings = findViewById(R.id.tvServings);
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        rvIngredients = findViewById(R.id.rvIngredients);
        rvSteps = findViewById(R.id.rvSteps);
        btnBack = findViewById(R.id.btnBack);

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvSteps.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRecipeDetails(int recipeId) {
        Log.d(TAG, "Loading recipe details for ID: " + recipeId);

        api.getRecipeDetails(recipeId).enqueue(new Callback<DirectRecipeResponse>() {
            @Override
            public void onResponse(Call<DirectRecipeResponse> call, Response<DirectRecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DirectRecipeResponse directResponse = response.body();

                    // Логируем для отладки
                    try {
                        String fullResponse = new Gson().toJson(directResponse);
                        Log.d(TAG, "Full response: " + fullResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error logging response: " + e.getMessage());
                    }

                    if (directResponse.getRecipe_id() > 0) {
                        displayRecipe(directResponse);
                    } else {
                        showError("Invalid recipe data received");
                    }
                } else {
                    showError("Failed to load recipe: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DirectRecipeResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayRecipe(DirectRecipeResponse recipe) {
        Log.d(TAG, "=== RECIPE DEBUG ===");
        Log.d(TAG, "Recipe ID: " + recipe.getRecipe_id());
        Log.d(TAG, "Title: " + recipe.getTitle());
        Log.d(TAG, "Main image URL: " + recipe.getImage_url());

        // Проверяем шаги
        if (recipe.getSteps() != null) {
            Log.d(TAG, "Steps count: " + recipe.getSteps().size());
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                Step step = recipe.getSteps().get(i);
                Log.d(TAG, "Step " + step.getStep_number() +
                        ": " + step.getDescription() +
                        ", Image URL: " + step.getImage_url());
            }
        } else {
            Log.d(TAG, "Steps is NULL");
        }

        Log.d(TAG, "===================");
        // Основная информация
        tvTitle.setText(recipe.getTitle() != null ? recipe.getTitle() : "No title");
        tvDescription.setText(recipe.getDescription() != null ? recipe.getDescription() : "No description");

        // Мета-информация
        tvDifficulty.setText("Difficulty: " + (recipe.getDifficulty() != null ? recipe.getDifficulty() : "Unknown"));

        String categoryName = "—";
        if (recipe.getCategory() != null && recipe.getCategory().getCategory_name() != null) {
            categoryName = recipe.getCategory().getCategory_name();
        }
        tvCategory.setText("Category: " + categoryName);

        tvPrepTime.setText("Preparation time: " + recipe.getPrep_time() + " min");
        tvServings.setText("Servings: " + recipe.getServing());

        // Изображение
        if (recipe.getImage_url() != null && !recipe.getImage_url().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImage_url())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(ivRecipeImage);
        }

        // Ингредиенты
        List<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            rvIngredients.setAdapter(new IngredientsAdapter(ingredients));
        } else {
            rvIngredients.setAdapter(new IngredientsAdapter(new ArrayList<>()));
        }

        // Шаги приготовления
        List<Step> steps = recipe.getSteps();
        if (steps != null && !steps.isEmpty()) {
            // Сортируем шаги по номеру
            steps.sort((s1, s2) -> Integer.compare(s1.getStep_number(), s2.getStep_number()));
            rvSteps.setAdapter(new StepsAdapter(steps));
        } else {
            rvSteps.setAdapter(new StepsAdapter(new ArrayList<>()));
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }
}