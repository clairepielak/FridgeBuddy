package com.example.fridgebuddy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fridgebuddy.ui.ScanBarcode.ScanBarcodeFragment;

public class ScanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanbarcode);

        // Create an instance of ScanBarcodeFragment
        ScanBarcodeFragment scanFragment = new ScanBarcodeFragment();

        // Replace the fragment container with the ScanBarcodeFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, scanFragment) // Replace "R.id.fragment_container" with your container ID
                .commit();
    }
}
