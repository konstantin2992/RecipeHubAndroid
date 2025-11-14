package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.recipehub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();

        // Проверяем, не переходим ли мы на конкретную вкладку
        String selectedTab = getIntent().getStringExtra("SELECTED_TAB");
        if (selectedTab != null) {
            switch (selectedTab) {

                case "account":
                    bottomNavigation.setSelectedItemId(R.id.nav_account);
                    break;
                default:
                    loadFragment(new SearchRecipesFragment(), "search");
            }
        } else {
            // По умолчанию открываем домашнюю страницу (поиск рецептов)
            if (savedInstanceState == null) {
                loadFragment(new SearchRecipesFragment(), "search");
            }
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String tag = "";

            if (item.getItemId() == R.id.nav_home) {
                fragment = new SearchRecipesFragment();
                tag = "search";

            } else if (item.getItemId() == R.id.nav_account) {
                // Запускаем Activity вместо Fragment
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                // Возвращаем true чтобы BottomNavigation сохранил выделение
                return true;
            }

            if (fragment != null) {
                loadFragment(fragment, tag);
                return true;
            }
            return false;
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
        bottomNavigation.setSelectedItemId(itemId);
    }



    void setupBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Если на главной вкладке - закрываем приложение
                if (bottomNavigation.getSelectedItemId() == R.id.nav_home) {
                    // Завершаем активность (стандартное поведение back)
                    finish();
                } else {
                    // Иначе переходим на главную вкладку
                    bottomNavigation.setSelectedItemId(R.id.nav_home);
                }
            }
        });
    }
}