package com.example.recipehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipehub.R;
import com.example.recipehub.model.AuthResponse;
import com.example.recipehub.model.LoginRequest;
import com.example.recipehub.model.User;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnToRegister;
    private LinearLayout btnGoogleLogin;

    private SessionManager session;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.inputEmail);
        etPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);
        btnGoogleLogin = findViewById(R.id.btnGoogle);

        session = new SessionManager(this);
        api = RetrofitClient.getInstance().create(ApiService.class);

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return;
        }
        btnGoogleLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);


        });
        btnLogin.setOnClickListener(v -> login());
        btnToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));


    }



    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заповніть email та пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);
        api.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    String token = response.body().getToken();

                    session.saveUser(user, token);

                    Toast.makeText(LoginActivity.this, "Успішний вхід!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Невірний email або пароль", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}