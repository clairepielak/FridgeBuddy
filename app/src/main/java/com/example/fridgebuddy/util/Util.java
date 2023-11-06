package com.example.fridgebuddy.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.example.fridgebuddy.AppDatabase;
import com.example.fridgebuddy.Item;
import com.example.fridgebuddy.R;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Class to hold utilities used throughout the app.
 */
public class Util extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Changed so the scan method can accept an instance of any activity -SM
    /**
     * Scan function, allows us to use the barcode scanner in whatever activity we want
     * @param activity give the current activity the scan is associated with
     * @param database give the database that you want to access, in our case it will almost always be the AppDatabase
     */
    public void Scan(Activity activity, AppDatabase database) {
        /*
          create a new instance of the options and barcode scanner and build it, can use this to change
          options in the future if we want or change the context that the barcode is running in
         */
        GmsBarcodeScannerOptions.Builder optionsBuilder = new GmsBarcodeScannerOptions.Builder();
        GmsBarcodeScanner gmsBarcodeScanner =
                GmsBarcodeScanning.getClient(activity.getApplicationContext(), optionsBuilder.build());

        /*
          start the barcode scanning and do something on success/fail/cancel
          these will be changed in the future to add items and display error codes to the user
        */
        gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener(barcode -> scanSuccessful(barcode, database, activity))
                .addOnFailureListener(e -> getErrorMessage(e, activity))
                .addOnCanceledListener(() -> Toast.makeText(activity.getApplicationContext(), activity.getApplicationContext().getString(R.string.error_scanner_cancelled), Toast.LENGTH_SHORT).show());
    }

    /**
     * If a barcode scan is successful,
     * adds the item associated with that barcode to the user's fridge database.
     * Will only add the item if it exists in our database of items we accounted for.
     */
    private void scanSuccessful(com.google.mlkit.vision.barcode.common.Barcode barcode, AppDatabase database, Activity activity) {
        // just display something in the home page currently for debug
        // will incorporate with the database in future change
        String barcodeValue =
                String.format(
                        Locale.US,
                        "Barcode Value: %s",
                        barcode.getDisplayValue());


        // test item
        // Item testItem = new Item(barcodeValue, "test", "10/30/2023");


        executor.execute(() -> {
            // this is where item will be upserted
            // database.itemDao().upsertItem(testItem);
        });

        Toast.makeText(activity.getApplicationContext(), "Item with UPC of " + barcodeValue + " has been added.", Toast.LENGTH_SHORT).show();
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
     * made for items that we don't have in our database of items.
     * Allows users to create their own items that we didn't account for
     * @param database give the database that you want to access, in our case it will almost always be the AppDatabase
     * @param name name of item given
     * @param date date of expiration, needs to be a java.util.Date variable
     */
    public void AddItem(AppDatabase database, String name, Date date) {
        // has to be executed off of main thread
        executor.execute(() -> database.itemDao().upsertItem(new Item(null, name, date)));
    }
}