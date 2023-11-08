package com.example.fridgebuddy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "catalog_items")
public class CatalogItem {
    @PrimaryKey
    @ColumnInfo(name = "upc")
    public String upc;

    @ColumnInfo(name= "name")
    public String name;

    @ColumnInfo(name = "expDays")
    public int daysUntilExp;
}
