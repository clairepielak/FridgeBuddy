
package com.example.fridgebuddy.Repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.fridgebuddy.database.ItemDao;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.database.Item;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemRepository {

    private ItemDao itemDao;
    private LiveData<List<Item>> itemsWithinNext5Days;

    public ItemRepository(Application application) {
        ItemDatabase database = ItemDatabase.getDatabase(application);
        itemDao = database.itemDao();
        // Get the LiveData for items within the next 5 days
        itemsWithinNext5Days = itemDao.getItemsWithinNext5Days(getFiveDaysFromNow());
    }

    // Method to get LiveData for items within the next 5 days -SM
    public LiveData<List<Item>> getItemsWithinNext5Days(Date fiveDaysFromNow) {
        return itemDao.getItemsWithinNext5Days(fiveDaysFromNow);
    }

    // Method to get a Date object representing 5 days from now -SM
    private Date getFiveDaysFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 5); // Add 5 days
        return calendar.getTime();
    }
}
