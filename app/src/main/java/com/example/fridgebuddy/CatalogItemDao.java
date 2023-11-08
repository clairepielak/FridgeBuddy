package com.example.fridgebuddy;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface CatalogItemDao {
    @Query("SELECT * FROM catalog_items WHERE upc = :upc")
    CatalogItem getCatalogItemByUPC(String upc);
}
