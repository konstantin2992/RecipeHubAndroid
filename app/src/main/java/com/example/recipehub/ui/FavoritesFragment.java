package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.recipehub.model.FavoritesResponse;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.utils.SessionManager;

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

    private void loadFavorites() {
        api.getFavorites("Bearer " + session.getToken()).enqueue(new Callback<FavoritesResponse>() {
            @Override
            public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> list = response.body().getFavorites();
                    // Используем конструктор с listener
                    recycler.setAdapter(new MyRecipesAdapter(list, api, session, FavoritesFragment.this));
                } else {
                    Toast.makeText(getContext(), "No favorites yet", Toast.LENGTH_SHORT).show();
                    recycler.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<FavoritesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Открыть карточку рецепта для просмотра
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getRecipe_id());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Recipe recipe) {
        // Для избранных рецептов редактирование может быть недоступно
        // или можно открыть детали с возможностью редактирования если это рецепт пользователя
        Toast.makeText(getContext(), "Edit your recipe in 'My Recipes' section", Toast.LENGTH_SHORT).show();

        // Альтернативно: открыть просмотр вместо редактирования
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getRecipe_id());
        startActivity(intent);
    }
}