package com.example.fridgebuddy.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "catalog_items")
public class CatalogItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "upc")
    private String upc;

    private String name;

    private int daysUntilExp;

    private byte[] imageBytes;

    // setters/getters
    public void setUpc(@NonNull String upc) { this.upc = upc; }
    public void setName(String name) { this.name = name; }
    public void setDaysUntilExp(int daysUntilExp) { this.daysUntilExp = daysUntilExp; }
    public void setImageBytes(byte[] imageBytes) { this.imageBytes = imageBytes; }

    @NonNull
    public String getUpc() { return upc; }
    public String getName() { return name; }
    public int getDaysUntilExp() { return daysUntilExp; }
    public byte[] getImageBytes() { return imageBytes; }
}