package com.example.fridgebuddy.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.example.fridgebuddy.CatalogItem;
import com.example.fridgebuddy.CatalogItemDatabase;
import com.example.fridgebuddy.ItemDatabase;
import com.example.fridgebuddy.Item;
import com.example.fridgebuddy.R;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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



    // used to set exp dates
    private final Calendar calendar = Calendar.getInstance();
    private Date expirationDate = calendar.getTime();
    // private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy");


    // Changed so the scan method can accept an instance of any activity -SM
    /**
     * Scan function, allows us to use the barcode scanner in whatever activity we want
     * @param activity give the current activity the scan is associated with
     * @param itemDB give the database that you want to access to the ItemDatabase for adding items to users storage
     * @param catalogDB allows us to read the data from the CatalogItemDatabase
     */
    public void Scan(Activity activity, ItemDatabase itemDB, CatalogItemDatabase catalogDB) {
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
        // just display something in the home page currently for debug
        // will incorporate with the database in future change
        String barcodeValue = String.format(barcode.getDisplayValue());

        // get data on diff thread than main, may lock up ui
        executor.execute(() -> {
            CatalogItem catalogItem = catalogDB.catalogItemDao().getCatalogItemByUPC(barcodeValue);

            if (catalogItem != null) {
                // set expiration date
                calendar.add(Calendar.DAY_OF_MONTH, catalogItem.getDaysUntilExp());
                expirationDate = calendar.getTime();

                // test = new Item("199901294", "Test",
                scannedItem = new Item(barcodeValue, catalogItem.getName(), expirationDate);


                itemDB.itemDao().upsertItem(scannedItem);


                // Post a Runnable to the UI thread to display the Toast message
                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), scannedItem.getName() + " with UPC of " + barcodeValue + " has been added.", Toast.LENGTH_LONG).show());
            } else {
                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "Unable to scan.", Toast.LENGTH_LONG).show());
            }
        });
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
    public void AddItem(Activity activity, ItemDatabase itemDB, String name, String dateString) {
        try {
            // parse dateString to date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateString);

            Item item = new Item(null, name, date);

            // add to database
            executor.execute(() -> {
                itemDB.itemDao().upsertItem(item);


                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), item.getName()  + " has been added.", Toast.LENGTH_LONG).show());
            });
        } catch (ParseException e) {
            // Handle the parsing exception if the date format is incorrect.

            // REMOVE BEFORE RELEASE
            // print error
            System.err.println("Invalid date format. Please enter the date in the format dd/MM/yyyy");
            // print stackTrace
            e.printStackTrace();
        }


    }
}