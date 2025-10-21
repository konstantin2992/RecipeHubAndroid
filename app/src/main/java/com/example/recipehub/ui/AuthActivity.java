package com.example.recipehub.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipehub.MainActivity;
import com.example.recipehub.R;
import com.example.recipehub.utils.SessionManager;
import com.example.recipehub.utils.TokenManager;
import com.example.recipehub.utils.UserManager;

import org.json.JSONObject;

import java.net.URLDecoder;

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