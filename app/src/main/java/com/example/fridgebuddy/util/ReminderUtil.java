package com.example.fridgebuddy.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.fridgebuddy.database.Item;

import java.util.Calendar;

public class ReminderUtil {
    /**
     * this function will set the reminder for the product currently being added/scanned
     * we are passing the Intent to send data to the AlarmReceiver class. The request code is out unique id will ensure that only one reminder
     * will be made per item and allows us to cancel specific item's
     * @param context context where the alarm will be set. This should be set to application context in our case since we want this to go off even if the app is not open.
     * @param item the item the alarm should be set for
     */
    public void setReminder(Context context, Item item) {
        // set the reminder to go off at 10am on the date the item is set to expire
        // extract the date from the item expDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getExpDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the alarm time to 10 AM the day before it expires
        calendar.set(year, month, (day - 1), 10, 0, 0);

        // create new AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // intent that triggers on alarm
        Intent intent = new Intent(context, AlarmReceiver.class);

        // pass the item name and exp date into the intent
        intent.putExtra("ITEM_NAME", item.getName());

        // unique request code. All item ids will be unique
        int requestCode = item.getId();

        // will be set off when alarm is triggered
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to the specified date and time
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (SecurityException e) {
            e.printStackTrace();

            Log.e("Reminder", "SecurityException: " + e.getMessage());
        }
    }

    /**
     * cancels an item's reminder, all variables should be the same as when setting. The AlarmManager can identify the reminder by these variables.
     * @param context provide context that the reminder resides in. Like setReminder(), this should be application since we want all reminders to go off when app is closed.
     * @param item item that reminder needs to be canceled
     */
    public void cancelReminder(Context context, Item item) {
        // get intent and request code
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = item.getId();

        //
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // cancel if it exists
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);

            Log.e("TEST", "Alarm for item with id " + item.getId() + " has been cancelled");
        }
    }

}
