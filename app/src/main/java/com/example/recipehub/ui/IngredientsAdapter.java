package com.example.recipehub.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipehub.R;
import com.example.recipehub.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<Ingredient> ingredients;

    public IngredientsAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        String ingredientText = ingredient.getName();
        if (ingredient.getQuantity() != null && ingredient.getUnit() != null) {
            ingredientText += " - " + ingredient.getQuantity() + " " + ingredient.getUnit();
        }
        holder.tvIngredient.setText(ingredientText);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredient;

        public ViewHolder(View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(R.id.tvIngredient);
        }
    }
}