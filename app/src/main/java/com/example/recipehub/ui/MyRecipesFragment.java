package com.example.recipehub.ui;

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
import com.example.recipehub.model.MyRecipesResponse;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.utils.SessionManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipesFragment extends Fragment {
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
        loadRecipes();
        return v;
    }

    private void loadRecipes() {
        api.getMyRecipes("Bearer " + session.getToken()).enqueue(new Callback<MyRecipesResponse>() {
            @Override
            public void onResponse(Call<MyRecipesResponse> call, Response<MyRecipesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> list = response.body().getRecipes();
                    recycler.setAdapter(new MyRecipesAdapter(list, api, session));
                } else {
                    Toast.makeText(getContext(), "No recipes yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyRecipesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}