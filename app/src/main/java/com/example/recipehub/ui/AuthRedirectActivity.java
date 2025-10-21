package com.example.recipehub.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipehub.utils.SessionManager;
import com.example.recipehub.utils.TokenManager;
import com.example.recipehub.utils.UserManager;

import org.json.JSONObject;

import java.net.URLDecoder;

public class AuthRedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null) {
            handleDeepLink(data);
        }
        finish();
    }

    private void handleDeepLink(Uri uri) {
        try {
            String token = uri.getQueryParameter("token");
            String userJson = uri.getQueryParameter("user");

            if (token != null && userJson != null) {
                String decodedUserJson = URLDecoder.decode(userJson, "UTF-8");
                JSONObject userData = new JSONObject(decodedUserJson);

                SessionManager session = new SessionManager(this);
                session.saveTokenOnly(token);
                session.saveUserFromJson(
                        userData.getInt("user_id"),
                        userData.getString("first_name"),
                        userData.getString("last_name"),
                        userData.getString("email"),
                        userData.optString("avatar", ""),
                        userData.optString("about_user", "")
                );

                Toast.makeText(this, "Успішна авторизація!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Помилка авторизації", Toast.LENGTH_LONG).show();
        }
    }
}