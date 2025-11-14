package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.recipehub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;
    private int currentItemId = R.id.nav_home; // Сохраняем текущий item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        setupBackPressedDispatcher();

        // Восстанавливаем состояние после поворота экрана
        if (savedInstanceState != null) {
            currentItemId = savedInstanceState.getInt("CURRENT_ITEM", R.id.nav_home);
            bottomNavigation.setSelectedItemId(currentItemId);
        } else {
            // Проверяем, не переходим ли мы на конкретную вкладку
            String selectedTab = getIntent().getStringExtra("SELECTED_TAB");
            if (selectedTab != null) {
                switch (selectedTab) {
                    case "account":
                        currentItemId = R.id.nav_account;
                        bottomNavigation.setSelectedItemId(R.id.nav_account);
                        break;
                    default:
                        loadFragment(new SearchRecipesFragment(), "search");
                        currentItemId = R.id.nav_home;
                }
            } else {
                // По умолчанию открываем домашнюю страницу (поиск рецептов)
                loadFragment(new SearchRecipesFragment(), "search");
                currentItemId = R.id.nav_home;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_ITEM", currentItemId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // При возвращении в активность обновляем выделение
        bottomNavigation.setSelectedItemId(currentItemId);
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupBottomNavigation() {
        // Устанавливаем начальное выделение
        bottomNavigation.setSelectedItemId(currentItemId);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            String tag = "";

            if (itemId == R.id.nav_home) {
                fragment = new SearchRecipesFragment();
                tag = "search";
                currentItemId = R.id.nav_home;

            } else if (itemId == R.id.nav_account) {
                // Запускаем Activity вместо Fragment
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

                // НЕ возвращаем true здесь - пусть BottomNavigation сам управляет состоянием
                return true;
            }

            if (fragment != null) {
                loadFragment(fragment, tag);
                currentItemId = itemId; // Сохраняем текущий item
                return true;
            }
            return false;
        });

        // Обработчик когда item уже выбран (для случаев когда нажимаем на активный item)
        bottomNavigation.setOnItemReselectedListener(item -> {
            // Можно добавить скролл к верху или другие действия при повторном нажатии
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
    }

    public void setSelectedItem(int itemId) {
        currentItemId = itemId;
        bottomNavigation.setSelectedItemId(itemId);
    }

    private void setupBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Если на главной вкладке - закрываем приложение
                if (currentItemId == R.id.nav_home) {
                    finish();
                } else {
                    // Иначе переходим на главную вкладку
                    setSelectedItem(R.id.nav_home);
                }
            }
        });
    }
}