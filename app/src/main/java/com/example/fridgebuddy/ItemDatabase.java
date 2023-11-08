package com.example.fridgebuddy;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.fridgebuddy.util.DateTypeConverter;

@Database(entities = {Item.class}, version = 2)
@TypeConverters(DateTypeConverter.class)
public abstract class ItemDatabase extends RoomDatabase {
    private static ItemDatabase INSTANCE;
    public abstract ItemDao itemDao();

    public static ItemDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ItemDatabase.class, "fridge_db").fallbackToDestructiveMigration().build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
