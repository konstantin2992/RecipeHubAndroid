package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.model.Category;
import com.example.recipehub.model.CategoryResponse;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.model.SearchRecipesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRecipesFragment extends Fragment {
    private RecyclerView recyclerRecipes;
    private ProgressBar progressBar;
    private EditText etSearch, etIngredients;
    private Spinner spinnerCategory, spinnerDifficulty;
    private TextView tvMaxPrepTime;
    private SeekBar seekBarPrepTime;
    private Button btnSearch, btnReset;
    private ApiService api;

    private List<Recipe> recipes = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private SearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_recipes, container, false);

        api = RetrofitClient.getInstance().create(ApiService.class);

        initViews(view);
        setupAdapters();
        setupListeners();

        // При открытии - загружаем все рецепты
        loadAllRecipes();

        return view;
    }

    private void initViews(View view) {
        recyclerRecipes = view.findViewById(R.id.recyclerRecipes);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch = view.findViewById(R.id.etSearch);
        etIngredients = view.findViewById(R.id.etIngredients);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        tvMaxPrepTime = view.findViewById(R.id.tvMaxPrepTime);
        seekBarPrepTime = view.findViewById(R.id.seekBarPrepTime);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnReset = view.findViewById(R.id.btnReset);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchAdapter(recipes, this::onRecipeClick);
        recyclerRecipes.setAdapter(adapter);
    }

    private void setupAdapters() {
        // Адаптер сложности
        String[] difficulties = {"Любая", "легко", "середньо", "складно"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        // Загружаем категории
        loadCategories();
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnReset.setOnClickListener(v -> resetFilters());

        seekBarPrepTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMaxPrepTime.setText("До " + progress + " хв");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadCategories() {
        if (api == null) {
            api = RetrofitClient.getInstance().create(ApiService.class);
        }

        api.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> loadedCategories = response.body().getCategories();
                    setupCategorySpinner(loadedCategories);
                } else {
                    setupDefaultCategories();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                setupDefaultCategories();
            }
        });
    }

    private void setupCategorySpinner(List<Category> loadedCategories) {
        this.categories.clear();
        this.categories.addAll(loadedCategories);

        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Всі категорії");
        for (Category category : categories) {
            categoryNames.add(category.getCategory_name());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void setupDefaultCategories() {
        String[] defaultCategories = {"Всі категорії", "Сніданки", "Обіди", "Вечері", "Десерти",
                "Салати", "Супи", "Напої", "Випічка", "Закуски", "Основні страви"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, defaultCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String difficulty = spinnerDifficulty.getSelectedItemPosition() > 0 ?
                spinnerDifficulty.getSelectedItem().toString() : "";
        int maxPrepTime = seekBarPrepTime.getProgress();

        String category = "";
        if (spinnerCategory.getSelectedItemPosition() > 0 && !categories.isEmpty()) {
            int selectedPosition = spinnerCategory.getSelectedItemPosition() - 1;
            if (selectedPosition < categories.size()) {
                category = String.valueOf(categories.get(selectedPosition).getCategory_id());
            }
        }

        Log.d("SearchDebug", "=== SEARCH PARAMETERS ===");
        Log.d("SearchDebug", "Title: '" + query + "'");
        Log.d("SearchDebug", "Ingredients: '" + ingredients + "'");
        Log.d("SearchDebug", "Difficulty: '" + difficulty + "'");
        Log.d("SearchDebug", "Max prep time: " + maxPrepTime);
        Log.d("SearchDebug", "Category ID: '" + category + "'");

        recipes.clear();
        adapter.notifyDataSetChanged();

        Map<String, String> params = new HashMap<>();

        if (!query.isEmpty()) {
            params.put("title", query);
        }
        if (!ingredients.isEmpty()) {
            params.put("ingredients", ingredients);
        }
        if (!difficulty.isEmpty()) {
            params.put("difficulty", difficulty);
        }
        if (maxPrepTime > 0) {
            params.put("maxTime", String.valueOf(maxPrepTime));
            params.put("minTime", "0");
        }
        if (!category.isEmpty()) {
            params.put("category_id", category);
        }

        Log.d("SearchDebug", "Final params: " + params.toString());

        String searchType = getSearchType(params);

        showLoading(true);

        api.searchRecipes(params).enqueue(new Callback<SearchRecipesResponse>() {
            @Override
            public void onResponse(Call<SearchRecipesResponse> call, Response<SearchRecipesResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> newRecipes = response.body().getRecipes();

                    recipes.clear();
                    if (newRecipes != null && !newRecipes.isEmpty()) {
                        recipes.addAll(newRecipes);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "Найдено: " + recipes.size() + " рецептов (" + searchType + ")", Toast.LENGTH_SHORT).show();
                    } else {
                        showEmptyState();
                        Toast.makeText(getContext(), "Рецепты не найдены", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showEmptyState();
                    Toast.makeText(getContext(), "Ошибка поиска", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchRecipesResponse> call, Throwable t) {
                showLoading(false);
                showEmptyState();
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllRecipes() {
        showLoading(true);

        api.searchRecipes(new HashMap<>()).enqueue(new Callback<SearchRecipesResponse>() {
            @Override
            public void onResponse(Call<SearchRecipesResponse> call, Response<SearchRecipesResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> newRecipes = response.body().getRecipes();
                    recipes.clear();
                    if (newRecipes != null && !newRecipes.isEmpty()) {
                        recipes.addAll(newRecipes);
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<SearchRecipesResponse> call, Throwable t) {
                showLoading(false);
                showEmptyState();
            }
        });
    }

    private void resetFilters() {
        etSearch.setText("");
        etIngredients.setText("");
        spinnerDifficulty.setSelection(0);
        spinnerCategory.setSelection(0);
        seekBarPrepTime.setProgress(60);
        tvMaxPrepTime.setText("До 60 хв");

        loadAllRecipes();
        Toast.makeText(getContext(), "Фильтры сброшены", Toast.LENGTH_SHORT).show();
    }

    private String getSearchType(Map<String, String> params) {
        if (params.containsKey("title")) return "по названию";
        if (params.containsKey("ingredients")) return "по ингредиентам";
        if (params.containsKey("difficulty")) return "по сложности";
        if (params.containsKey("maxTime")) return "по времени";
        if (params.containsKey("category_id")) return "по категории";
        return "все рецепты";
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerRecipes.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState() {
        // Можно добавить TextView для пустого состояния
        Toast.makeText(getContext(), "Рецептов не найдено", Toast.LENGTH_SHORT).show();
    }

    private void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getRecipe_id());
        startActivity(intent);
    }
}