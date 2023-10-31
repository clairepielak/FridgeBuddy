package com.example.fridgebuddy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "upc")
    public String upc;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "exp_date")
    public String expDate;

    public Item(String upc, String name, String expDate) {
        this.upc = upc;
        this.name = name;
        this.expDate = expDate;
    }
}
