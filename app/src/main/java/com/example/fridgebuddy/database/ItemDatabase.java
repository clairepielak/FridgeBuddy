package com.example.fridgebuddy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.fridgebuddy.util.Converters;

@Database(entities = {Item.class}, version = 3, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class ItemDatabase extends RoomDatabase {
    private static ItemDatabase INSTANCE;
    public abstract ItemDao itemDao();

    public static ItemDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, ItemDatabase.class, "user_items.db").fallbackToDestructiveMigration().build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
