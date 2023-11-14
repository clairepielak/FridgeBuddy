package com.example.fridgebuddy.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

public class NotificationPermissionHelper {

    public static void requestNotificationPermission(final Context context) {
        // Check if the app has notification permissions
        if (!areNotificationsEnabled(context)) {
            // Notifications are not enabled, show a dialog to request permission
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enable Notifications");
            builder.setMessage("Notifications will allow us to remind you when your food is nearing its expiration date.");
            builder.setPositiveButton("Open Settings", (dialog, which) -> openAppSettings(context));
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Handle cancel if needed
            });
            builder.show();
        }
    }

    private static boolean areNotificationsEnabled(Context context) {
        // Check if the app has notification permissions
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    private static void openAppSettings(Context context) {
        // Open the app settings page where the user can enable notifications
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
