package com.example.recipehub.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.recipehub.model.User;

/**
 * SessionManager – зберігає та завантажує дані користувача через SharedPreferences
 * Після переходу на сервер: зберігає JWT токен і дані користувача
 */
public class SessionManager {
    private static final String PREF_NAME = "recipehub_session";
    private static final String KEY_ID = "user_id";
    private static final String KEY_FIRST = "first_name";
    private static final String KEY_LAST = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ABOUT = "about_user";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_AVATAR_PUBLIC_ID = "avatar_public_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGGED_IN = "logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context ctx) {
        this.context = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /** Зберігає користувача + токен після логіну або реєстрації */
    public void saveUser(User user, String token) {
        if (user != null) {
            editor.putInt(KEY_ID, user.getUser_id());
            editor.putString(KEY_FIRST, user.getFirst_name());
            editor.putString(KEY_LAST, user.getLast_name());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_ABOUT, user.getAbout_user());
            editor.putString(KEY_AVATAR, user.getAvatar());
            editor.putString(KEY_ROLE, user.getRole());
            editor.putString(KEY_AVATAR_PUBLIC_ID, user.getAvatar_public_id());
        }
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();

        Log.d("SESSION_DEBUG", "Saved user: " + (user != null ? user.getEmail() : "null"));
        Log.d("SESSION_DEBUG", "Saved token: " + token);
    }

    /** НОВИЙ МЕТОД: Зберігає тільки токен для Google auth */
    public void saveTokenOnly(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
        Log.d("SESSION_DEBUG", "Saved token only: " + token);
    }

    /** НОВИЙ МЕТОД: Зберігає дані з JSON для Google auth */
    public void saveUserFromJson(int userId, String firstName, String lastName,
                                 String email, String avatar, String aboutUser) {
        editor.putInt(KEY_ID, userId);
        editor.putString(KEY_FIRST, firstName);
        editor.putString(KEY_LAST, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AVATAR, avatar);
        editor.putString(KEY_ABOUT, aboutUser != null ? aboutUser : "");
        editor.putString(KEY_ROLE, "user");
        editor.apply();

        Log.d("SESSION_DEBUG", "Saved user from JSON: " + email);
    }

    /** Оновлення профілю (локально) */
    public void updateUser(User user) {
        editor.putString(KEY_FIRST, user.getFirst_name());
        editor.putString(KEY_LAST, user.getLast_name());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ABOUT, user.getAbout_user());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.putString(KEY_AVATAR_PUBLIC_ID, user.getAvatar_public_id());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    /** Отримання користувача */
    public User getUser() {
        User u = new User();
        u.setUser_id(prefs.getInt(KEY_ID, 0));
        u.setFirst_name(prefs.getString(KEY_FIRST, ""));
        u.setLast_name(prefs.getString(KEY_LAST, ""));
        u.setEmail(prefs.getString(KEY_EMAIL, ""));
        u.setAbout_user(prefs.getString(KEY_ABOUT, ""));
        u.setAvatar(prefs.getString(KEY_AVATAR, ""));
        u.setAvatar_public_id(prefs.getString(KEY_AVATAR_PUBLIC_ID, ""));
        u.setRole(prefs.getString(KEY_ROLE, ""));
        return u;
    }

    /** Отримання токена */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /** Перевірка, чи користувач залогінений */
    public boolean isLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false) && getToken() != null;
        Log.d("SESSION_DEBUG", "isLoggedIn: " + loggedIn);
        return loggedIn;
    }

    /** Вихід із профілю */
    public void logout() {
        editor.putBoolean(KEY_LOGGED_IN, false);
        editor.apply();
        Log.d("SESSION_DEBUG", "User logged out");
    }
    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    /** Примусово зберігає токен (для веб-авторизації) */
    public void forceSaveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }
    /** Повне очищення сесії */
    public void clear() {
        editor.clear().apply();
        Log.d("SESSION_DEBUG", "Session cleared completely");
    }
}