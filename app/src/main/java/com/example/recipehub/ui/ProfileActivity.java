package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.model.User;
import com.example.recipehub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {
    private static final int EDIT_PROFILE_REQUEST = 1001;
    private static final int CREATE_RECIPE_REQUEST = 1002;

    private TextView fullName, email, about;
    private ImageView avatar;
    private Button btnEdit, btnLogout;
    private SessionManager session;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupClickListeners();
        loadData();
        setupTabs();
    }

    private void initializeViews() {
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        about = findViewById(R.id.about);
        avatar = findViewById(R.id.avatar);
        btnEdit = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        session = new SessionManager(this);
    }

    private void setupTabs() {
        adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Мої рецепти");
            else tab.setText("Улюблені рецепти");
        }).attach();

        FloatingActionButton fabCreateRecipe = findViewById(R.id.fabCreateRecipe);
        fabCreateRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CreateRecipeActivity.class);
            startActivityForResult(intent, CREATE_RECIPE_REQUEST);
        });
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> openEditProfile());

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void openEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivityForResult(intent, EDIT_PROFILE_REQUEST);
    }

    private void loadData() {
        User u = session.getUser();
        if (u != null) {
            // Устанавливаем данные пользователя
            fullName.setText(u.getFirst_name() + " " + u.getLast_name());
            email.setText(u.getEmail());

            // Обрабатываем поле "about"
            String aboutText = u.getAbout_user();
            if (aboutText == null || aboutText.isEmpty()) {
                about.setText("About me...");
            } else {
                about.setText(aboutText);
            }

            // Загружаем аватар с помощью Glide
            loadAvatar(u.getAvatar());
        }
    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Используем Glide для загрузки изображения по URL
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_person) // изображение-заглушка
                    .error(R.drawable.ic_person) // изображение при ошибке
                    .into(avatar);
        } else {
            avatar.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_PROFILE_REQUEST:
                    // Обновляем данные профиля после редактирования
                    refreshProfileData();
                    break;
                case CREATE_RECIPE_REQUEST:
                    // Обновляем вкладку "Мои рецепты" после создания рецепта
                    updateMyRecipesTab();
                    break;
            }
        }
    }

    private void updateMyRecipesTab() {
        Fragment fragment = adapter.createFragment(0);
        if (fragment instanceof MyRecipesFragment) {
            ((MyRecipesFragment) fragment).loadRecipes();
        }
    }

    private void refreshProfileData() {
        // Перезагружаем данные пользователя из сессии
        User user = session.getUser();

        if (user != null) {
            // Обновляем текстовые поля
            fullName.setText(user.getFirst_name() + " " + user.getLast_name());
            email.setText(user.getEmail());

            // Обновляем поле "about"
            String aboutText = user.getAbout_user();
            if (aboutText == null || aboutText.isEmpty()) {
                about.setText("About me...");
            } else {
                about.setText(aboutText);
            }

            // Обновляем аватар
            loadAvatar(user.getAvatar());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Всегда обновляем данные при возвращении на экран
        refreshProfileData();
    }
}