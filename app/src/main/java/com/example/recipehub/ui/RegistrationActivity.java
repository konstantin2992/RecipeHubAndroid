package com.example.recipehub.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipehub.R;
import com.example.recipehub.model.AuthResponse;
import com.example.recipehub.model.RegisterRequest;
import com.example.recipehub.model.User;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.utils.SessionManager;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etFirst, etLast, etEmail, etPassword, etConfirm;
    private Button btnRegister;
    private LinearLayout btnGoogleReg;
    private ApiService api;
    private SessionManager session;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zА-Яа-яЇїІіЄєҐґ]{2,30}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@gmail\\.com$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirst = findViewById(R.id.inputFirstName);
        etLast = findViewById(R.id.inputLastName);
        etEmail = findViewById(R.id.inputEmail);
        etPassword = findViewById(R.id.inputPassword);
        etConfirm = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogleReg = findViewById(R.id.btnGoogleR);
        btnGoogleReg.setOnClickListener(v -> openGoogleAuth());
        api = RetrofitClient.getInstance().create(ApiService.class);
        session = new SessionManager(this);

        btnRegister.setOnClickListener(v -> register());
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }
    private void openGoogleAuth() {
        // ✅ Та же самая функция что и в LoginActivity
        String googleUrl = "http://10.0.2.2:5000/api/auth/google";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl));
        startActivity(intent);

        Toast.makeText(this, "Sign up with Google in browser", Toast.LENGTH_LONG).show();
    }
    private void register() {
        String first = etFirst.getText().toString().trim();
        String last = etLast.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String conf = etConfirm.getText().toString();

        // --- Проверка на пустые поля ---
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(conf)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Проверка имени ---
        if (!NAME_PATTERN.matcher(first).matches()) {
            Toast.makeText(this, "Invalid first name (only letters, 2–30 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Проверка фамилии ---
        if (!NAME_PATTERN.matcher(last).matches()) {
            Toast.makeText(this, "Invalid last name (only letters, 2–30 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Проверка Email ---
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !GMAIL_PATTERN.matcher(email).matches()) {
            Toast.makeText(this, "Email must be valid Gmail address", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Проверка пароля ---
        if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            Toast.makeText(this, "Password must be ≥8 chars with upper, lower case and number", Toast.LENGTH_LONG).show();
            return;
        }

        // --- Проверка совпадения паролей ---
        if (!pass.equals(conf)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(pass) || TextUtils.isEmpty(conf)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(conf)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest req = new RegisterRequest(first, last, email, pass);
        api.register(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    String token = response.body().getToken();

                    session.saveUser(user, token);
                    Toast.makeText(RegistrationActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegistrationActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}