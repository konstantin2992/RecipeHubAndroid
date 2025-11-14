package com.example.recipehub.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.utils.SessionManager;
import com.example.recipehub.model.SimpleResponse;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private ApiService api;
    private SessionManager session;
    private OnRecipeClickListener listener;
    private Context context;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onEditClick(Recipe recipe);
    }

    // Добавить конструктор без listener для обратной совместимости
    public MyRecipesAdapter(List<Recipe> recipes, ApiService api, SessionManager session) {
        this.recipes = recipes;
        this.api = api;
        this.session = session;
    }

    public MyRecipesAdapter(List<Recipe> recipes, ApiService api, SessionManager session, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.api = api;
        this.session = session;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, btnDelete, btnEdit;
        TextView title, difficulty, description, time, servings, category;
        ProgressBar progressBar; // Добавляем ProgressBar для индикации загрузки

        public ViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.recipeImage);
            btnDelete = v.findViewById(R.id.btnDelete);
            btnEdit = v.findViewById(R.id.btnEdit);
            title = v.findViewById(R.id.recipeTitle);
            difficulty = v.findViewById(R.id.recipeDifficulty);
            description = v.findViewById(R.id.recipeDescription);
            time = v.findViewById(R.id.recipeTime);
            servings = v.findViewById(R.id.recipeServings);
            category = v.findViewById(R.id.recipeCategory);
            progressBar = v.findViewById(R.id.progressBar); // Добавьте ProgressBar в layout
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_recipe, parent, false);
        context = parent.getContext(); // Сохраняем контекст
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        Recipe r = recipes.get(pos);

        h.title.setText(r.getTitle());
        h.description.setText(r.getDescription());
        h.difficulty.setText("🧩 " + r.getDifficulty());
        h.category.setText("📂 " + (r.getCategory() != null ? r.getCategory().getCategory_name() : "—"));
        h.time.setText("⏱ " + r.getPrep_time() + " min");
        h.servings.setText("🍽 " + r.getServing() + " servings");

        Glide.with(h.img.getContext())
                .load(r.getImage_url())
                .placeholder(R.drawable.ic_food_placeholder)
                .into(h.img);

        // Сбрасываем видимость прогресса
        h.progressBar.setVisibility(View.GONE);
        h.btnDelete.setVisibility(View.VISIBLE);

        // Клик на всю карточку для просмотра
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(r);
            } else {
                // Fallback: открыть детали рецепта по умолчанию
                openRecipeDetails(v.getContext(), r);
            }
        });

        // Кнопка редактирования
        h.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(r);
            } else {
                // Fallback: открыть редактирование по умолчанию
                openEditRecipe(v.getContext(), r);
            }
        });

        // Кнопка удаления
        h.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(h, r, pos);
        });
    }

    private void showDeleteConfirmationDialog(ViewHolder holder, Recipe recipe, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Видалення рецепту")
                .setMessage("Ви впевнені, що хочете видалити рецепт \"" + recipe.getTitle() + "\"?")
                .setPositiveButton("Видалити", (dialog, which) -> {
                    deleteRecipe(holder, recipe, position);
                })
                .setNegativeButton("Скасувати", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void deleteRecipe(ViewHolder holder, Recipe recipe, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.btnDelete.setVisibility(View.GONE);

        api.deleteRecipe("Bearer " + session.getToken(), recipe.getRecipe_id()).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> resp) {
                holder.progressBar.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);

                // ПРОСТО СЧИТАЕМ ЛЮБОЙ УСПЕШНЫЙ ОТВЕТ УСПЕХОМ
                if (resp.isSuccessful()) {
                    recipes.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "✅ Рецепт успішно видалено", Toast.LENGTH_SHORT).show();

                    if (position < recipes.size()) {
                        notifyItemRangeChanged(position, recipes.size() - position);
                    }
                } else {
                    Toast.makeText(context, "❌ Помилка видалення", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                holder.progressBar.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                Toast.makeText(context, "❌ Помилка мережі", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Методы по умолчанию для случаев когда listener не установлен
    private void openRecipeDetails(Context context, Recipe recipe) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getRecipe_id());
        context.startActivity(intent);
    }

    private void openEditRecipe(Context context, Recipe recipe) {
        Intent intent = new Intent(context, EditRecipeActivity.class);
        intent.putExtra("recipe", recipe);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() { return recipes.size(); }
}