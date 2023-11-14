package com.example.fridgebuddy.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.fridgebuddy.database.CatalogItem;
import com.example.fridgebuddy.database.CatalogItemDatabase;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.R;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Class to hold utilities used throughout the app.
 */
public class Util extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Item scannedItem;

    // Changed so the scan method can accept an instance of any activity -SM
    /**
     * scan function, allows us to use the barcode scanner in whatever activity we want
     * @param activity give the current activity the scan is associated with
     * @param itemDB give the database that you want to access to the ItemDatabase for adding items to users storage
     * @param catalogDB allows us to read the data from the CatalogItemDatabase
     */
    public void scan(Activity activity, ItemDatabase itemDB, CatalogItemDatabase catalogDB) {
        /*
          create a new instance of the options and barcode scanner and build it, can use this to change
          options in the future if we want or change the context that the barcode is running in
         */
        GmsBarcodeScannerOptions.Builder optionsBuilder = new GmsBarcodeScannerOptions.Builder();
        GmsBarcodeScanner gmsBarcodeScanner =
                GmsBarcodeScanning.getClient(activity.getApplicationContext(), optionsBuilder.build());

        /*
          start the barcode scanning and do something on success/fail/cancel
          uses Google Mobile Services barcode scanner
        */
        gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener(barcode -> scanSuccessful(barcode, itemDB, catalogDB, activity))
                .addOnFailureListener(e -> getErrorMessage(e, activity))
                .addOnCanceledListener(() -> Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.error_scanner_cancelled), Toast.LENGTH_SHORT).show());
    }

    /**
     * If a barcode scan is successful,
     * adds the item associated with that barcode to the user's fridge database.
     * Will only add the item if it exists in our catalog_items.db
     */
    private void scanSuccessful(com.google.mlkit.vision.barcode.common.Barcode barcode, ItemDatabase itemDB, CatalogItemDatabase catalogDB, Activity activity) {
        if (barcode.getDisplayValue() != null) {
            // format barcodeValue to a string we can use
            String barcodeValue = String.format(barcode.getDisplayValue());

            // get data on diff thread than main, may lock up ui
            executor.execute(() -> {
                CatalogItem catalogItem = catalogDB.catalogItemDao().getCatalogItemByUPC(barcodeValue);

                if (catalogItem != null) {
                    // set expiration date
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, catalogItem.getDaysUntilExp());
                    Date expirationDate = calendar.getTime();

                    // test = new Item("199901294", "Test",
                    scannedItem = new Item(barcodeValue, catalogItem.getName(), expirationDate, catalogItem.getImageBytes());
                    itemDB.itemDao().upsertItem(scannedItem);

                    // set reminder for the item
                    setReminder(activity.getApplicationContext(), scannedItem);

                    // Post a Runnable to the UI thread to display the Toast message
                    activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), scannedItem.getName() + " with UPC of " + barcodeValue + " has been added.", Toast.LENGTH_LONG).show());
                } else {
                    activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "Unable to scan.", Toast.LENGTH_LONG).show());
                }
            });
        }
    }

    /**
     * If an exception is thrown while trying to scan barcodes
      */
    @SuppressLint("SwitchIntDef")
    private void getErrorMessage(Exception e, Activity activity) {
        if (e instanceof MlKitException) {
            switch (((MlKitException) e).getErrorCode()) {
                case MlKitException.PERMISSION_DENIED:
                    Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.error_camera_permission_not_granted), Toast.LENGTH_SHORT).show();
                    return;
                case MlKitException.UNAVAILABLE:
                    Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.error_app_name_unavailable), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.error_default_message, e), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Add an Item without using the barcode scanner,
     * made for items that we don't have in our catalog_items.db.
     * Allows users to create their own items that we didn't account for
     * @param activity passes activity to allow the Toast to be shown
     * @param itemDB give the database that you want to access, in our case it will almost always be the AppDatabase
     * @param name name of item given
     * @param dateString date of expiration, needs to be a String datatype, will be parsed to a Date
     */
    public void addItem(Activity activity, ItemDatabase itemDB, String name, String dateString) {
        // convert string to date
        Date date = Converters.stringToDate(dateString);

        // items must have a valid date and name. If the parsing comes back incorrectly the item cannot be added
        if (date != null && !name.isEmpty() && !name.trim().isEmpty()) {
            // create new Item
            Item item = new Item(null, name, date, null);

            // add to database
            executor.execute(() -> {
                itemDB.itemDao().upsertItem(item);

                // set reminder
                setReminder(activity.getApplicationContext(), item);

                // display a toast to let the user know item has been added
                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), name  + " has been added.", Toast.LENGTH_LONG).show());
            });
        } else if (date == null) {
            Toast.makeText(activity.getApplicationContext(), "The date entered was invalid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity.getApplicationContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * cancels an item's reminder then deletes item from database
     * @param context context where reminder is stored, should always be application context since our app only stores reminders in this context
     * @param itemDB database where user's items are stored
     * @param item item that you would like removed
     */
    public void deleteItem(Context context, ItemDatabase itemDB, Item item) {
        // cancel alarm
        cancelReminder(context, item);

        // remove item from the ItemDatabase
        itemDB.itemDao().deleteItem(item);
    }

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

        // Set the alarm time to 10 AM on the extracted date
        calendar.set(year, month, day, 10, 0, 0);

        // create new AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // intent that triggers on alarm
        Intent intent = new Intent(context, AlarmReceiver.class);

        // pass the item name and exp date into the intent
        intent.putExtra("ITEM_NAME", item.getName());
        intent.putExtra("EXP_DATE", Converters.dateToString(item.getExpDate()));

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
        }
    }
}