package com.example.recipehub.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.model.FavoriteItem;
import com.example.recipehub.model.FavoritesResponse;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.utils.SessionManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment implements MyRecipesAdapter.OnRecipeClickListener {
    private RecyclerView recycler;
    private ApiService api;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        recycler = v.findViewById(R.id.recyclerMyRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        api = RetrofitClient.getInstance().create(ApiService.class);
        session = new SessionManager(getContext());

        loadFavorites();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем список каждый раз когда фрагмент становится видимым
        loadFavorites();
    }

    public void loadFavorites() {
        Log.d("FavoritesFragment", "Loading favorites...");

        if (!session.isLoggedIn()) {
            Toast.makeText(getContext(), "Будь ласка, увійдіть в систему", Toast.LENGTH_SHORT).show();
            recycler.setAdapter(null);
            return;
        }

        api.getFavorites("Bearer " + session.getToken()).enqueue(new Callback<FavoritesResponse>() {
            @Override
            public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response) {
                Log.d("FavoritesFragment", "Response: " + response.isSuccessful());
                Log.d("FavoritesFragment", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    FavoritesResponse favoritesResponse = response.body();

                    List<Recipe> list = new ArrayList<>();

                    if (favoritesResponse.getFavorites() != null) {
                        Log.d("FavoritesFragment", "Favorites list size: " + favoritesResponse.getFavorites().size());

                        for (FavoriteItem favorite : favoritesResponse.getFavorites()) {
                            // Используем getFavorite_recipe() вместо getRecipe()
                            if (favorite != null && favorite.getFavorite_recipe() != null) {
                                Recipe recipe = favorite.getFavorite_recipe();
                                list.add(recipe);
                                Log.d("FavoritesFragment", "Added recipe: " + recipe.getTitle() + " ID: " + recipe.getRecipe_id());
                            } else {
                                Log.d("FavoritesFragment", "Favorite item or favorite_recipe is null");
                                // Также можно попробовать получить recipe_id и загрузить рецепт отдельно
                                if (favorite != null) {
                                    Log.d("FavoritesFragment", "Favorite has recipe_id: " + favorite.getRecipe_id());
                                }
                            }
                        }
                    }

                    Log.d("FavoritesFragment", "Final list size: " + list.size());

                    if (list.isEmpty()) {
                        Toast.makeText(getContext(), "У вас поки що немає улюблених рецептів", Toast.LENGTH_SHORT).show();
                        recycler.setAdapter(null);
                    } else {
                        recycler.setAdapter(new MyRecipesAdapter(list, api, session, FavoritesFragment.this));
                        Log.d("FavoritesFragment", "Adapter updated with " + list.size() + " items");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("FavoritesFragment", "Error response: " + errorBody);
                    } catch (IOException e) {
                        Log.e("FavoritesFragment", "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(getContext(), "Помилка завантаження улюблених", Toast.LENGTH_SHORT).show();
                    recycler.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<FavoritesResponse> call, Throwable t) {
                Log.e("FavoritesFragment", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Помилка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getRecipe_id());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Recipe recipe) {
        // Для избранных рецептов редактирование недоступно
        Toast.makeText(getContext(), "Редагування доступне тільки для ваших рецептів", Toast.LENGTH_SHORT).show();
    }
}