package com.example.fridgebuddy.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "user_items")
public class Item {

    public boolean isExpired() {
        Date currentDate = new Date();
        return expDate != null && expDate.before(currentDate);
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "upc")
    private String upc;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "exp_date")
    private Date expDate;

    @ColumnInfo(name = "imageDestination")
    private String imageDestination;

    // user scanned, upc given
    public Item(String upc, String name, Date expDate, String imageDestination) {
        this.upc = upc;
        this.name = name;
        this.expDate = expDate;
        this.imageDestination = imageDestination;
    }

    // setters/getters
    public void setId(int id) { this.id = id; }
    public void setUpc(String upc) { this.upc = upc; }
    public void setName(String name) { this.name = name; }
    public void setExpDate(Date expDate) { this.expDate = expDate; }
    public void setImageDestination(String imageDestination) { this.imageDestination = imageDestination; }

    public int getId() { return id; }
    public String getUpc() { return upc; }
    public String getName() { return name; }
    public Date getExpDate() { return expDate; }
    public String getImageDestination() { return imageDestination; }
}
