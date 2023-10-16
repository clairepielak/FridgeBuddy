package com.example.fridgebuddy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fridgebuddy.databinding.ActivityMainBinding;

// imports for scanning -ZL
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button scanMove;
    private ActivityMainBinding binding;

    // handler for user fridge
    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // creating dbhandler class for user fridge and giving it context
        dbHandler = new DBHandler(MainActivity.this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_ShoppingList)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        scanMove=findViewById(R.id.scannow);
        scanMove.setOnClickListener(v -> {
            Scan();
        });
    }

    // function to do the scanning -ZL
    public void Scan() {
        // create a new instance of the options and barcode scanner and build it, can use this to change
        // options in the future if we want or change the context that the barcode is running in
        GmsBarcodeScannerOptions.Builder optionsBuilder = new GmsBarcodeScannerOptions.Builder();
        GmsBarcodeScanner gmsBarcodeScanner =
                GmsBarcodeScanning.getClient(getApplicationContext(), optionsBuilder.build());

        // start the barcode scanning and do something on success/fail/cancel
        // these will be changed in the future to add items and display error codes to the user
        gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener(this::scanSuccessful)
                .addOnFailureListener(
                        this::getErrorMessage)
                .addOnCanceledListener(
                        () -> Toast.makeText(MainActivity.this, getString(R.string.error_scanner_cancelled), Toast.LENGTH_SHORT).show());
    }

    // function for a successful barcode reading -ZL
    private void scanSuccessful(com.google.mlkit.vision.barcode.common.Barcode barcode) {
        // just display something in the home page currently for debug
        // will incorporate with the database in future change
        String barcodeValue =
                String.format(
                        Locale.US,
                        "Barcode Value: %s",
                        barcode.getDisplayValue());

        dbHandler.addItem(barcodeValue);

        Toast.makeText(MainActivity.this, "Item with UPC of " + barcodeValue + " has been added.", Toast.LENGTH_SHORT).show();
    }

    // function if an exception is thrown while trying to read barcodes -ZL
    @SuppressLint("SwitchIntDef")
    private void getErrorMessage(Exception e) {
        if (e instanceof MlKitException) {
            switch (((MlKitException) e).getErrorCode()) {
                case MlKitException.PERMISSION_DENIED:
                    Toast.makeText(MainActivity.this, getString(R.string.error_camera_permission_not_granted), Toast.LENGTH_SHORT).show();
                    return;
                case MlKitException.UNAVAILABLE:
                    Toast.makeText(MainActivity.this, getString(R.string.error_app_name_unavailable), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    Toast.makeText(MainActivity.this, getString(R.string.error_default_message, e), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}