package com.example.fridgebuddy;

// imports to handle database
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    // constants for names in database
    private static final String DB_NAME = "fridgedb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myfridge";
    private static final String UPC_COL = "upc";
    private static final String QTY_COL = "qty";
    private static final String NAME_COL = "name";
    private static final String EXP_DATE = "expiration";

    // constructor
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // make a sqlite query and set column names and data types
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + UPC_COL + " TEXT PRIMARY KEY, "
                + QTY_COL + " INTEGER DEFAULT 1, "
                + NAME_COL + " TEXT, "
                + EXP_DATE + " TEXT)";
        // for some reason use text instead of string in query, potential to drop
        // some 0's if using string

        // execute query
        db.execSQL(query);
    }

    public void addItem(String UPC) {
        // make a variable to easily call write method in db
        SQLiteDatabase db = this.getWritableDatabase();

        // variable to store content values
        ContentValues values = new ContentValues();

        // pass all value with key and pair
        values.put(UPC_COL, UPC);
        values.put(NAME_COL, "TEST");
        values.put(EXP_DATE, "10/16/2023");

        // pass content values into table
        db.insert(TABLE_NAME, null, values);

        // close db after addition
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
