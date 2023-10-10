package com.example.fridgebuddy.ui.ScanBarcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScanBarcodeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScanBarcodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Scan Barcode Page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}