package com.example.fridgebuddy.ui.Inventory;

import android.app.Application;

import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.database.ItemDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {
    ItemDatabase itemDB;
    private final LiveData<List<Item>> allItems;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        itemDB = ItemDatabase.getDatabase(application);
        allItems = itemDB.itemDao().orderItemByName();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }
}