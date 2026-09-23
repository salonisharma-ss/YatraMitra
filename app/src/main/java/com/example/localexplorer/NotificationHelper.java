package com.example.localexplorer;

import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;

public class NotificationHelper {

    // ✅ Success notification (Green)
    public static void showSuccess(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#4CAF50")); // Green
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.show();
    }

    // ✅ Error notification (Red)
    public static void showError(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#F44336")); // Red
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.show();
    }

    // ✅ Warning notification (Orange)
    public static void showWarning(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#FF9800")); // Orange
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.show();
    }

    // ✅ Info notification (Blue)
    public static void showInfo(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#2196F3")); // Blue
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.show();
    }
}