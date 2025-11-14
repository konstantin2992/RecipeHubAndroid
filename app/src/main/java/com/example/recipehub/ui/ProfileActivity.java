package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.model.User;
import com.example.recipehub.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {
    private static final int EDIT_PROFILE_REQUEST = 1001;
    private static final int CREATE_RECIPE_REQUEST = 1002;
    private static final int RECIPE_DETAIL_REQUEST = 1003;
    private BottomNavigationView bottomNavigation;
    private TextView fullName, email, about;
    private ImageView avatar;
    private Button btnEdit, btnLogout;
    private SessionManager session;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;
    private Fragment[] fragments = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupBottomNavigation(); // Добавляем навигацию
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
        bottomNavigation = findViewById(R.id.bottomNavigation); // Добавляем BottomNavigation
        session = new SessionManager(this);
    }

    private void setupBottomNavigation() {
        // Выделяем вкладку профиля
        bottomNavigation.setSelectedItemId(R.id.nav_account);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Переходим на MainActivity с вкладкой Home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;


            } else if (itemId == R.id.nav_account) {
                // Уже в профиле, ничего не делаем
                return true;
            }
            return false;
        });
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
            new AlertDialog.Builder(this)
                    .setTitle("Выход")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        session.logout();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
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
                case RECIPE_DETAIL_REQUEST:
                    // Обновляем вкладку избранных после работы с рецептом
                    updateFavoritesTab();
                    break;
            }
        }
    }
    private void updateFavoritesTab() {
        Fragment fragment = adapter.createFragment(1);
        if (fragment instanceof FavoritesFragment) {
            ((FavoritesFragment) fragment).loadFavorites();
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
    public Fragment getFragment(int position) {
        return fragments[position];
    }
    private void setupBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Возврат на MainActivity при нажатии назад
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
