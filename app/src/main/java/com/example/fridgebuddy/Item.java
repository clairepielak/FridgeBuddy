package com.example.fridgebuddy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "user_items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "upc")
    private String upc;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "exp_date")
    private Date expDate;

    // user scanned, upc given
    public Item(String upc, String name, Date expDate) {
        this.upc = upc;
        this.name = name;
        this.expDate = expDate;
    }

    // setters/getters
    public void setId(int id) { this.id = id; }
    public void setUpc(String upc) { this.upc = upc; }
    public void setName(String name) { this.name = name; }
    public void setExpDate(Date expDate) { this.expDate = expDate; }

    public int getId() { return id; }
    public String getUpc() { return upc; }
    public String getName() { return name; }
    public Date getExpDate() { return expDate; }
}
