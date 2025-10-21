package com.example.recipehub.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private static final String API_URL = "http://10.0.2.2:5000/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Просто відкриваємо системний браузер
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(API_URL + "/auth/google"));
        startActivity(browserIntent);
        finish();
    }
}