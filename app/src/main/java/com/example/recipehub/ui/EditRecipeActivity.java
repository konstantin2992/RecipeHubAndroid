package com.example.recipehub.ui;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.model.CreateRecipeRequest;
import com.example.recipehub.model.Ingredient;
import com.example.recipehub.model.Recipe;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.Step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRecipeActivity extends CreateRecipeActivity {
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentRecipe = (Recipe) getIntent().getSerializableExtra("recipe");
        super.onCreate(savedInstanceState);

        // Изменить заголовок кнопки
        btnCreateRecipe.setText("Оновити рецепт");

        // Заполнить поля данными рецепта
        populateRecipeData();
    }

    private void populateRecipeData() {
        if (currentRecipe == null) return;

        etTitle.setText(currentRecipe.getTitle());
        etDescription.setText(currentRecipe.getDescription());
        etPrepTime.setText(String.valueOf(currentRecipe.getPrep_time()));
        etServing.setText(String.valueOf(currentRecipe.getServing()));
        actvDifficulty.setText(currentRecipe.getDifficulty(), false);

        // Установить категорию
        if (currentRecipe.getCategory() != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategory_id() == currentRecipe.getCategory().getCategory_id()) {
                    spinnerCategory.setSelection(i + 1); // +1 потому что первая позиция "Оберіть категорію"
                    break;
                }
            }
        }

        // Загрузить главное изображение
        if (currentRecipe.getImage_url() != null && !currentRecipe.getImage_url().isEmpty()) {
            Glide.with(this)
                    .load(currentRecipe.getImage_url())
                    .placeholder(R.drawable.ic_add_image)
                    .into(ivMainImage);
        }

        // Заполнить ингредиенты
        if (currentRecipe.getIngredients() != null) {
            ingredientsContainer.removeAllViews();
            for (Ingredient ingredient : currentRecipe.getIngredients()) {
                addIngredientField();
                View lastView = ingredientsContainer.getChildAt(ingredientsContainer.getChildCount() - 1);
                EditText etName = lastView.findViewById(R.id.etIngredientName);
                EditText etQuantity = lastView.findViewById(R.id.etIngredientQuantity);
                EditText etUnit = lastView.findViewById(R.id.etIngredientUnit);
                etName.setText(ingredient.getName());
                etQuantity.setText(ingredient.getQuantity());
                etUnit.setText(ingredient.getUnit());
            }
        }

        // Заполнить шаги
        if (currentRecipe.getSteps() != null) {
            stepsContainer.removeAllViews();
            for (Step step : currentRecipe.getSteps()) {
                addStepField();
                View lastView = stepsContainer.getChildAt(stepsContainer.getChildCount() - 1);
                EditText etStepDesc = lastView.findViewById(R.id.etStepDescription);
                etStepDesc.setText(step.getDescription());

                // Загрузить изображение шага если есть
                if (step.getImage_url() != null && !step.getImage_url().isEmpty()) {
                    ImageView stepImage = lastView.findViewById(R.id.ivStepImage);
                    Glide.with(this)
                            .load(step.getImage_url())
                            .placeholder(R.drawable.ic_add_image)
                            .into(stepImage);
                }
            }
        }
    }

    @Override
    protected void createRecipe() {
        if (!validateForm()) return;

        // Использовать существующий ID рецепта для обновления
        CreateRecipeRequest request = prepareRecipeRequest(); // Теперь этот метод доступен
        updateRecipe(request);
    }

    private void updateRecipe(CreateRecipeRequest request) {
        Log.d(TAG, "=== DEBUG: Starting update recipe ===");
        Log.d(TAG, "Main image URI: " + mainImageUri);
        Log.d(TAG, "Step images count: " + stepImages.size());

        btnCreateRecipe.setEnabled(false);
        btnCreateRecipe.setText("Оновлення...");

        // Подготовка данных для multipart запроса
        MultipartBody.Part mainImagePart = null;
        if (mainImageUri != null) {
            mainImagePart = prepareImagePart("image", mainImageUri, this);
            Log.d(TAG, "Main image part created: " + (mainImagePart != null));
        }

        // Шаговые изображения
        List<MultipartBody.Part> stepImageParts = new ArrayList<>();
        for (Map.Entry<Integer, Uri> entry : stepImages.entrySet()) {
            int idx = entry.getKey();
            Uri uri = entry.getValue();
            Log.d(TAG, "Step image " + idx + ": " + uri);

            MultipartBody.Part part = prepareImagePart("stepImages", uri, this);
            if (part != null) {
                stepImageParts.add(part);
                Log.d(TAG, "Step image part created for index: " + idx);
            }
        }
        Log.d(TAG, "Total step image parts: " + stepImageParts.size());

        // Текстовые поля
        RequestBody categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getCategory_id()));
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), request.getTitle());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), request.getDescription());
        RequestBody difficultyBody = RequestBody.create(MediaType.parse("text/plain"), request.getDifficulty());
        RequestBody prepTimeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getPrep_time()));
        RequestBody servingBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getServing()));
        RequestBody stepsBody = RequestBody.create(MediaType.parse("text/plain"), request.getStepsJson());
        RequestBody ingredientsBody = RequestBody.create(MediaType.parse("text/plain"), request.getIngredientsJson());

        Log.d(TAG, "All parts prepared, making API call...");

        // Вызов API для обновления
        Call<SimpleResponse> call = api.updateRecipe(
                "Bearer " + session.getToken(),
                currentRecipe.getRecipe_id(),
                mainImagePart,
                stepImageParts,
                categoryIdBody,
                titleBody,
                descriptionBody,
                difficultyBody,
                prepTimeBody,
                servingBody,
                stepsBody,
                ingredientsBody
        );

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                Log.d(TAG, "Response received, code: " + response.code());
                resetCreateButton();
                if (response.isSuccessful()) {
                    Toast.makeText(EditRecipeActivity.this, "Рецепт успішно оновлено!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = "Помилка оновлення рецепту";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage += ": " + errorBody;
                            Log.e(TAG, "Error response: " + errorBody);
                        }
                    } catch (IOException e) {
                        errorMessage += ": " + e.getMessage();
                    }
                    Toast.makeText(EditRecipeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getMessage());
                resetCreateButton();
                Toast.makeText(EditRecipeActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}