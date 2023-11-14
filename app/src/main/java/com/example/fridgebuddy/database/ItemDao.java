package com.example.fridgebuddy.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface ItemDao {
    @Upsert
    void upsertItem(Item... item);

    @Delete
    void deleteItem(Item item);

    @Query("SELECT * FROM user_items ORDER BY name ASC")
    LiveData<List<Item>> orderItemByName();

    @Query("SELECT * FROM user_items ORDER BY exp_date ASC")
    LiveData<List<Item>> orderItemByExpDate();
}
