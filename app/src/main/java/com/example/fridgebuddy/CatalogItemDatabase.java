package com.example.fridgebuddy;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CatalogItem.class}, version = 1)
public abstract class CatalogItemDatabase extends RoomDatabase {
    private static CatalogItemDatabase INSTANCE;
    public abstract CatalogItemDao catalogItemDao();

    public static CatalogItemDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CatalogItemDatabase.class, "catalog_items").fallbackToDestructiveMigration().build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
