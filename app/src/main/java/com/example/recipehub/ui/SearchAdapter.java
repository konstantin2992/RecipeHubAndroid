package com.example.recipehub.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.model.Recipe;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public SearchAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvDescription.setText(recipe.getDescription());
        holder.tvDifficulty.setText("🧩 " + recipe.getDifficulty());
        holder.tvTime.setText("⏱ " + recipe.getPrep_time() + " хв");
        holder.tvServings.setText("🍽 " + recipe.getServing() + " порцій");

        if (recipe.getCategory() != null) {
            holder.tvCategory.setText("📂 " + recipe.getCategory().getCategory_name());
        }

        if (recipe.getImage_url() != null && !recipe.getImage_url().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(recipe.getImage_url())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .into(holder.ivImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvDifficulty, tvTime, tvServings, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivRecipeImage);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvDescription = itemView.findViewById(R.id.tvRecipeDescription);
            tvDifficulty = itemView.findViewById(R.id.tvRecipeDifficulty);
            tvTime = itemView.findViewById(R.id.tvRecipeTime);
            tvServings = itemView.findViewById(R.id.tvRecipeServings);
            tvCategory = itemView.findViewById(R.id.tvRecipeCategory);
        }
    }
}