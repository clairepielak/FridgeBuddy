package com.example.fridgebuddy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CatalogItemDao {
    @Insert
    void insertCatalogItem(CatalogItem item);

    @Query("SELECT * FROM catalog_items WHERE upc = :upc")
    CatalogItem getCatalogItemByUPC(String upc);
}
