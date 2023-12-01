package com.example.fridgebuddy;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fridgebuddy.Repository.ItemRepository;
import com.example.fridgebuddy.database.Item;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    private ItemRepository repository;
    private LiveData<List<Item>> itemsWithinNext5Days;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        // Initialize the repository and LiveData for items within the next 5 days
        repository = new ItemRepository(application);
        itemsWithinNext5Days = repository.getItemsWithinNext5Days(getFiveDaysFromNow());
    }

    // Method to get LiveData for items within the next 5 days
    public LiveData<List<Item>> getItemsWithinNext5Days() {
        return itemsWithinNext5Days;
    }

    // Method to get a Date object representing 5 days from now -SM
    private Date getFiveDaysFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        return calendar.getTime();
    }
}
