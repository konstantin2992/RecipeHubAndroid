package com.example.recipehub.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.model.CategoryResponse;
import com.example.recipehub.model.Category;
import com.example.recipehub.model.CreateRecipeRequest;
import com.example.recipehub.model.Ingredient;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.Step;
import com.example.recipehub.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CreateRecipeActivity extends AppCompatActivity {

    private static final int PICK_MAIN_IMAGE = 1;
    private static final int PICK_STEP_IMAGE = 2;

    protected EditText etTitle, etDescription, etPrepTime, etServing;

    protected AutoCompleteTextView actvDifficulty;
    protected ImageView ivMainImage;
    protected LinearLayout ingredientsContainer, stepsContainer;
    protected Button btnCreateRecipe;
    protected AutoCompleteTextView actvCategory;

    protected int selectedCategoryId = -1;


    protected ApiService api;
    protected SessionManager session;
    protected List<Category> categories = new ArrayList<>();
    protected Uri mainImageUri;
    protected Map<Integer, Uri> stepImages = new HashMap<>();
    protected int currentStepForImage = -1;

    protected final String[] difficultyLevels = {"Any", "easy", "medium", "hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        initializeViews();
        setupAdapters();
        setupClickListeners();

        api = RetrofitClient.getInstance().create(ApiService.class);
        session = new SessionManager(this);

        loadCategories();
    }

    protected CreateRecipeRequest prepareRecipeRequest() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int prepTime = Integer.parseInt(etPrepTime.getText().toString().trim());
        int serving = Integer.parseInt(etServing.getText().toString().trim());
        String difficulty = actvDifficulty.getText().toString().trim();

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            EditText etName = ingredientView.findViewById(R.id.etIngredientName);
            EditText etQuantity = ingredientView.findViewById(R.id.etIngredientQuantity);
            EditText etUnit = ingredientView.findViewById(R.id.etIngredientUnit);
            String name = etName.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            if (!name.isEmpty() && !quantity.isEmpty() && !unit.isEmpty()) {
                ingredients.add(new Ingredient(name, quantity, unit));
            }
        }

        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);
            EditText etStepDesc = stepView.findViewById(R.id.etStepDescription);
            String stepDesc = etStepDesc.getText().toString().trim();
            if (!stepDesc.isEmpty()) {
                Uri stepUri = stepImages.get(i);
                String stepPath = stepUri != null ? getRealPathFromURI(stepUri) : null;
                steps.add(new Step(stepDesc, stepPath, i + 1));
            }
        }

        return new CreateRecipeRequest(selectedCategoryId, title, description, difficulty, prepTime, serving, steps, ingredients);
    }

    protected void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrepTime = findViewById(R.id.etPrepTime);
        etServing = findViewById(R.id.etServing);
        actvCategory = findViewById(R.id.actvCategory);
        actvDifficulty = findViewById(R.id.actvDifficulty);
        ivMainImage = findViewById(R.id.ivMainImage);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        stepsContainer = findViewById(R.id.stepsContainer);
        btnCreateRecipe = findViewById(R.id.btnCreateRecipe);

    }

    protected void setupAdapters() {
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficultyLevels);
        actvDifficulty.setAdapter(difficultyAdapter);

        ArrayAdapter<String> tempCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        tempCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actvCategory.setAdapter(tempCategoryAdapter);
    }

    protected void setupClickListeners() {
        findViewById(R.id.btnPickMainImage).setOnClickListener(v -> pickImage(PICK_MAIN_IMAGE));
        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> addIngredientField());
        findViewById(R.id.btnAddStep).setOnClickListener(v -> addStepField());
        btnCreateRecipe.setOnClickListener(v -> createRecipe());

        addIngredientField();
        addStepField();
    }


    protected void loadCategories() {
        api.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getCategories();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Оберіть категорію");
                    for (Category c : categories) categoryNames.add(c.getCategory_name());

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, categoryNames) {
                        @Override
                        public boolean isEnabled(int position) {
                            return position != 0;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                            return view;
                        }
                    };
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    actvCategory.setAdapter(categoryAdapter);

                    actvCategory.setOnItemClickListener((parent, view, position, id) -> {
                        if (position > 0) {
                            selectedCategoryId = categories.get(position - 1).getCategory_id();
                        } else {
                            selectedCategoryId = -1;
                        }
                    });
                } else setupDefaultCategories();
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                setupDefaultCategories();
            }
        });
    }

    protected void setupDefaultCategories() {
        String[] defaultCategories = {"Оберіть категорію", "Сніданки", "Обіди", "Вечері", "Десерти",
                "Салати", "Супи", "Напої", "Випічка", "Закуски", "Основні страви"};

        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultCategories);
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actvCategory.setAdapter(defaultAdapter);

        categories.clear();
        for (int i = 1; i < defaultCategories.length; i++) {
            Category category = new Category();
            category.setCategory_id(i);
            category.setCategory_name(defaultCategories[i]);
            categories.add(category);
        }
    }

    protected void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (requestCode == PICK_MAIN_IMAGE) {
                mainImageUri = imageUri;
                Glide.with(this).load(imageUri).placeholder(R.drawable.ic_add_image).into(ivMainImage);
            } else if (requestCode == PICK_STEP_IMAGE && currentStepForImage != -1) {
                stepImages.put(currentStepForImage, imageUri);
                View stepView = stepsContainer.getChildAt(currentStepForImage);
                ImageView stepImage = stepView.findViewById(R.id.ivStepImage);
                Glide.with(this).load(imageUri).placeholder(R.drawable.ic_add_image).into(stepImage);
            }
        }
        currentStepForImage = -1;
    }

    protected void addIngredientField() {
        View ingredientView = getLayoutInflater().inflate(R.layout.item_ingredient_input, null);
        ingredientsContainer.addView(ingredientView);
        ingredientView.findViewById(R.id.btnRemoveIngredient).setOnClickListener(v -> ingredientsContainer.removeView(ingredientView));
    }

    protected void addStepField() {
        int stepNumber = stepsContainer.getChildCount() + 1;
        View stepView = getLayoutInflater().inflate(R.layout.item_step_input, null);
        TextView tvStepNumber = stepView.findViewById(R.id.tvStepNumber);
        tvStepNumber.setText("Крок " + stepNumber);

        stepView.findViewById(R.id.btnPickStepImage).setOnClickListener(v -> {
            currentStepForImage = stepsContainer.indexOfChild(stepView);
            pickImage(PICK_STEP_IMAGE);
        });

        stepView.findViewById(R.id.btnRemoveStep).setOnClickListener(v -> {
            int index = stepsContainer.indexOfChild(stepView);
            stepImages.remove(index);
            stepsContainer.removeView(stepView);
            updateStepNumbers();
        });

        stepsContainer.addView(stepView);
    }

    protected void updateStepNumbers() {
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);
            TextView tvStepNumber = stepView.findViewById(R.id.tvStepNumber);
            tvStepNumber.setText("Крок " + (i + 1));
        }
    }

    protected void createRecipe() {
        if (!validateForm()) return;

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int prepTime = Integer.parseInt(etPrepTime.getText().toString().trim());
        int serving = Integer.parseInt(etServing.getText().toString().trim());
        String difficulty = actvDifficulty.getText().toString().trim();

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            EditText etName = ingredientView.findViewById(R.id.etIngredientName);
            EditText etQuantity = ingredientView.findViewById(R.id.etIngredientQuantity);
            EditText etUnit = ingredientView.findViewById(R.id.etIngredientUnit);
            String name = etName.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            if (!name.isEmpty() && !quantity.isEmpty() && !unit.isEmpty())
                ingredients.add(new Ingredient(name, quantity, unit));
        }

        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);
            EditText etStepDesc = stepView.findViewById(R.id.etStepDescription);
            String stepDesc = etStepDesc.getText().toString().trim();
            if (!stepDesc.isEmpty()) {
                Uri stepUri = stepImages.get(i);
                String stepPath = stepUri != null ? getRealPathFromURI(stepUri) : null;
                steps.add(new Step(stepDesc, stepPath, i + 1));
            }
        }

        CreateRecipeRequest request = new CreateRecipeRequest(selectedCategoryId, title, description, difficulty, prepTime, serving, steps, ingredients);
        uploadRecipeWithImages(request);
    }

    protected void uploadRecipeWithImages(CreateRecipeRequest request) {
        btnCreateRecipe.setEnabled(false);
        btnCreateRecipe.setText("Створення...");

        // Главное изображение
        MultipartBody.Part mainImagePart = null;
        if (mainImageUri != null) mainImagePart = prepareImagePart("image", mainImageUri, this);

        // Шаговые изображения
        List<MultipartBody.Part> stepImageParts = new ArrayList<>();
        for (Map.Entry<Integer, Uri> entry : stepImages.entrySet()) {
            int idx = entry.getKey();
            Uri uri = entry.getValue();
            MultipartBody.Part part = prepareImagePart("step_images[" + idx + "]", uri, this);
            if (part != null) stepImageParts.add(part);
        }

        // Текстовые поля
        RequestBody categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getCategory_id()));
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), request.getTitle());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), request.getDescription());
        RequestBody difficultyBody = RequestBody.create(MediaType.parse("text/plain"), request.getDifficulty());
        RequestBody prepTimeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getPrep_time()));
        RequestBody servingBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getServing()));
        RequestBody stepsBody = RequestBody.create(MediaType.parse("text/plain"), request.getStepsJson());
        RequestBody ingredientsBody = RequestBody.create(MediaType.parse("text/plain"), request.getIngredientsJson());

        Call<SimpleResponse> call = api.createRecipe(
                "Bearer " + session.getToken(),
                mainImagePart,
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
                resetCreateButton();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateRecipeActivity.this, "Рецепт успішно створено!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = "Помилка: ";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            if (errorJson.has("message"))
                                errorMessage += errorJson.get("message").getAsString();
                            else errorMessage += errorBody;
                        } else errorMessage += response.message();
                    } catch (IOException e) {
                        errorMessage += response.message();
                    }
                    Toast.makeText(CreateRecipeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                resetCreateButton();
                Toast.makeText(CreateRecipeActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected MultipartBody.Part prepareImagePart(String partName, Uri imageUri, Context context) {
        try {
            String path = getRealPathFromURI(imageUri);
            if (path == null) return null;
            File file = new File(path);
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";
            RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(colIndex);
            cursor.close();
            return path;
        }
        return contentUri.getPath();
    }

    protected void resetCreateButton() {
        btnCreateRecipe.setEnabled(true);
        btnCreateRecipe.setText("Створити рецепт");
    }

    protected boolean validateForm() {
        if (TextUtils.isEmpty(etTitle.getText())) {
            etTitle.setError("Введіть назву рецепту");
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText())) {
            etDescription.setError("Введіть опис рецепту");
            return false;
        }
        if (selectedCategoryId == -1 &&
                (!(this instanceof EditRecipeActivity) ||
                        ((EditRecipeActivity) this).currentRecipe.getCategory() == null)) {
            Toast.makeText(this, "Оберіть категорію", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(actvDifficulty.getText())) {
            actvDifficulty.setError("Оберіть складність");
            return false;
        }
        if (TextUtils.isEmpty(etPrepTime.getText())) {
            etPrepTime.setError("Введіть час приготування");
            return false;
        }
        if (TextUtils.isEmpty(etServing.getText())) {
            etServing.setError("Введіть кількість порцій");
            return false;
        }
        if (mainImageUri == null &&
                (!(this instanceof EditRecipeActivity) ||
                        ((EditRecipeActivity) this).currentRecipe.getImage_url() == null ||
                        ((EditRecipeActivity) this).currentRecipe.getImage_url().isEmpty())) {
            Toast.makeText(this, "Додайте головне зображення", Toast.LENGTH_SHORT).show();
            return false;


        }

        return true;
    }
}
