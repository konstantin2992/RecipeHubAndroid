package com.example.recipehub.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class SafeToast {
    public static void show(Context context, String message) {
        if (context != null) {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Игнорируем исключения
            }
        }
    }

    public static void show(Fragment fragment, String message) {
        if (fragment != null && fragment.isAdded() && fragment.getContext() != null) {
            show(fragment.getContext(), message);
        }
    }
}