package com.example.fridgebuddy.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fridgebuddy.database.CatalogItem;
import com.example.fridgebuddy.database.CatalogItemDatabase;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.R;
import com.google.android.material.snackbar.Snackbar;
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
    private final ReminderUtil reminderUtil = new ReminderUtil();

    // Changed so the scan method can accept an instance of any activity -SM
    /**
     * scan function, allows us to use the barcode scanner in whatever activity we want
     * @param activity give the current activity the scan is associated with
     * @param itemDB give the database that you want to access to the ItemDatabase for adding items to users storage
     * @param catalogDB allows us to read the data from the CatalogItemDatabase
     */
    public void scan(Activity activity, ItemDatabase itemDB, CatalogItemDatabase catalogDB) {
        Context appContext = activity.getApplicationContext();

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
                .addOnFailureListener(e -> getErrorMessage(e, appContext))
                .addOnCanceledListener(() -> Toast.makeText(appContext, appContext.getString(R.string.error_scanner_cancelled), Toast.LENGTH_SHORT).show());
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

            addItem(activity, itemDB, catalogDB, barcodeValue, null, null);
        }
    }

    /**
     * If an exception is thrown while trying to scan barcodes
      */
    @SuppressLint("SwitchIntDef")
    private void getErrorMessage(Exception e, Context appContext) {
        if (e instanceof MlKitException) {
            switch (((MlKitException) e).getErrorCode()) {
                case MlKitException.PERMISSION_DENIED:
                    Toast.makeText(appContext, appContext.getString(R.string.error_camera_permission_not_granted), Toast.LENGTH_SHORT).show();
                    return;
                case MlKitException.UNAVAILABLE:
                    Toast.makeText(appContext, appContext.getString(R.string.error_app_name_unavailable), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    Toast.makeText(appContext, appContext.getString(R.string.error_default_message, e), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void addItem(Activity activity, ItemDatabase itemDB, CatalogItemDatabase catalogDB, String upc, String name, String dateString) {
        Context appContext = activity.getApplicationContext();

        executor.execute(() -> {
            Item item = null;

            // are we searching the catalogDB or not
            if (catalogDB != null) {
                // was the upc scanned in the database?
                CatalogItem catalogItem = catalogDB.catalogItemDao().getCatalogItemByUPC(upc);

                // if it was
                if (catalogItem != null) {
                    // set expiration date from the daysUntilExp variable the item has stored
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, catalogItem.getDaysUntilExp());
                    Date expirationDate = calendar.getTime();

                    // create a new scannedItem from the values we received from the catalog
                    item = new Item(upc, catalogItem.getName(), expirationDate, catalogItem.getImageDestination());
                } else {
                    activity.runOnUiThread(() -> Toast.makeText(appContext, "Item cannot be scanned", Toast.LENGTH_SHORT).show());
                }
            } else {
                // convert date from String to Date
                Date date = Converters.stringToDate(dateString);

                // if name and date are not empty or null then make the item and add it
                if (date != null && !name.isEmpty() && !name.trim().isEmpty()) {
                    // create new Item
                    item = new Item(null, name, date, null);
                } else if (date == null) {
                    activity.runOnUiThread(() -> Toast.makeText(appContext, "The date entered was invalid", Toast.LENGTH_SHORT).show());
                } else {
                    activity.runOnUiThread(() -> Toast.makeText(appContext, "Please enter a valid name", Toast.LENGTH_SHORT).show());
                }
            }

            // add the item to the itemDB and set a reminder for it
            if (item != null) {
                final Item itemWithId = itemDB.itemDao().upsertAndGet(item);

                reminderUtil.setReminder(activity.getApplicationContext(), itemWithId);

                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), itemWithId.getName() + " added", Toast.LENGTH_SHORT).show());
            } else {
                Log.d("NULL", "addItem had a null item");
            }

        });
    }

    /**
     * cancels an item's reminder then deletes item from database
     * @param context context where reminder is stored, should always be application context since our app only stores reminders in this context
     * @param itemDB database where user's items are stored
     * @param item item that you would like removed
     */
    public void deleteItem(Context context, ItemDatabase itemDB, Item item) {
        // cancel alarm
        reminderUtil.cancelReminder(context, item);

        // remove item from the ItemDatabase
        // new thread to not lock up UI
        executor.execute(() -> itemDB.itemDao().deleteItem(item));

    }

    /**
     * create and show undo snackbar
     * @param text what should display on the snackbar
     * @param undo should the undo action be available
     */
    private void showSnackbar(Activity activity, String text, ItemDatabase itemDB, Item scannedItem, Boolean undo) {
        // Use the root view of the activity as the parent
        View parentView = activity.findViewById(android.R.id.content);

        // create and show the Snackbar
        Snackbar snackbar = Snackbar.make(parentView, text, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();

        int marginInPixels = Converters.dpToPixels(activity.getApplicationContext(), 4);

        snackbarView.setPadding(marginInPixels, marginInPixels, marginInPixels, marginInPixels);

        if (undo) {
            snackbar.setAction("Undo", v -> deleteItem(activity.getApplicationContext(), itemDB, scannedItem));
        }

        snackbar.setBackgroundTint(Color.DKGRAY).setActionTextColor(R.drawable.green_two).show();
    }
}