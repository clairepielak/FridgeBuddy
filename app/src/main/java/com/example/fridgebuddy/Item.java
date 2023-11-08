package com.example.fridgebuddy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "user_items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "upc")
    public String upc;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "exp_date")
    public Date expDate;

    // user scanned, upc given
    public Item(String upc, String name, Date expDate) {
        this.upc = upc;
        this.name = name;
        this.expDate = expDate;
    }
}
