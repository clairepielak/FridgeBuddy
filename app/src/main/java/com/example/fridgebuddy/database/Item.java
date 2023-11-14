package com.example.fridgebuddy.database;

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

    @ColumnInfo(name = "imageBytes", typeAffinity = ColumnInfo.BLOB)
    private byte[] imageBytes;

    // user scanned, upc given
    public Item(String upc, String name, Date expDate, byte[] imageBytes) {
        this.upc = upc;
        this.name = name;
        this.expDate = expDate;
        this.imageBytes = imageBytes;
    }

    // setters/getters
    public void setId(int id) { this.id = id; }
    public void setUpc(String upc) { this.upc = upc; }
    public void setName(String name) { this.name = name; }
    public void setExpDate(Date expDate) { this.expDate = expDate; }
    public void setImageBytes(byte[] imageBytes) { this.imageBytes = imageBytes; }

    public int getId() { return id; }
    public String getUpc() { return upc; }
    public String getName() { return name; }
    public Date getExpDate() { return expDate; }
    public byte[] getImageBytes() { return imageBytes; }
}
