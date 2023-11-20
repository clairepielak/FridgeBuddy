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

    @NonNull
    private String name;

    @NonNull
    private int daysUntilExp;

    private String imageDestination;

    // setters/getters
    public void setUpc(@NonNull String upc) { this.upc = upc; }
    public void setName(@NonNull String name) { this.name = name; }
    public void setDaysUntilExp(@NonNull int daysUntilExp) { this.daysUntilExp = daysUntilExp; }
    public void setImageDestination(String imageDestination) { this.imageDestination = imageDestination; }

    @NonNull
    public String getUpc() { return upc; }
    public String getName() { return name; }
    public int getDaysUntilExp() { return daysUntilExp; }
    public String getImageDestination() { return imageDestination; }
}
