package com.example.recipehub.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class UserManager {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_DATA = "user_data";

    public static void saveUserData(Context context, JSONObject userData) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_DATA, userData.toString()).apply();
    }

    public static JSONObject getUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userDataJson = prefs.getString(KEY_USER_DATA, null);

        try {
            return userDataJson != null ? new JSONObject(userDataJson) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    public static void clearUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_DATA).apply();
    }
}