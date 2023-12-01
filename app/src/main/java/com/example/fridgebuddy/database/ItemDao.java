package com.example.fridgebuddy.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Upsert;

import java.util.Date;
import java.util.List;

@Dao
public interface ItemDao {
    @Upsert
    Long upsertItem(Item item);

    @Query("SELECT * FROM user_items WHERE id = :itemId")
    Item getItem(long itemId);

    @Transaction
    default Item upsertAndGet(Item item) {
        long itemId = upsertItem(item);
        return getItem(itemId);
    }

    @Delete
    void deleteItem(Item item);

    @Query("SELECT * FROM user_items ORDER BY id ASC LIMIT 2")
    LiveData<List<Item>> getTwoSmallestIdItems();

    @Query("SELECT * FROM user_items ORDER BY name ASC")
    LiveData<List<Item>> orderItemByName();

    @Query("SELECT * FROM user_items ORDER BY exp_date ASC")
    LiveData<List<Item>> orderItemByExpDate();

    @Query("SELECT * FROM user_items WHERE exp_date <= :fiveDaysFromNow")
    LiveData<List<Item>> getItemsWithinNext5Days(Date fiveDaysFromNow);
}
