package com.example.fridgebuddy.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fridgebuddy.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // get name and exp date values from the intent.extras we feed
        String itemName = intent.getStringExtra("ITEM_NAME");
        String expDate = intent.getStringExtra("EXP_DATE");

        showNotification(context, itemName + " is about to expire.", "Remember to use " + itemName + " before it expires on " + expDate + "!");
    }

    private void showNotification(Context context, String title, String message) {
        String channel_id = "com.example.fridgebuddy.reminder_channel";
        String channel_name = "Reminder Channel";


        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(channel);
        }

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.icons8_fridge_48)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        // Use a unique notification ID, for example, based on the current time
        int notificationId = (int) System.currentTimeMillis();
        notifManager.notify(notificationId, builder.build());
    }
}
