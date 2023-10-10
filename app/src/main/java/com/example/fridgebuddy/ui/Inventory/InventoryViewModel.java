package com.example.fridgebuddy.ui.Inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public InventoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Inventory Page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}