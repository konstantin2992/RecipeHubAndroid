package com.example.recipehub.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.utils.SessionManager;
import com.example.recipehub.model.SimpleResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private ApiService api;
    private SessionManager session;

    public MyRecipesAdapter(List<Recipe> recipes, ApiService api, SessionManager session) {
        this.recipes = recipes;
        this.api = api;
        this.session = session;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, btnDelete;
        TextView title, difficulty;
        public ViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.recipeImage);
            btnDelete = v.findViewById(R.id.btnDelete);
            title = v.findViewById(R.id.recipeTitle);
            difficulty = v.findViewById(R.id.recipeDifficulty);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        Recipe r = recipes.get(pos);
        h.title.setText(r.getTitle());
        h.difficulty.setText(r.getDifficulty());
        Glide.with(h.img.getContext()).load(r.getImage_url()).into(h.img);

        h.btnDelete.setOnClickListener(v -> {
            api.deleteRecipe("Bearer " + session.getToken(), r.getRecipe_id()).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> resp) {
                    if (resp.isSuccessful()) {
                        recipes.remove(pos);
                        notifyItemRemoved(pos);
                         Toast.makeText(v.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() { return recipes.size(); }
}