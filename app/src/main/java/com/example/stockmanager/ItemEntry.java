package com.example.stockmanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_entries",
        foreignKeys = @ForeignKey(entity = Item.class,
                parentColumns = "id",
                childColumns = "itemId",
                onDelete = ForeignKey.CASCADE))
public class ItemEntry {
    @PrimaryKey(autoGenerate = true)
    private int entryId;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    private String date;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "quantity")
    private int quantity;

    public String getDate() {
        return date;
    }

    private String retailerName;
    private int itemId;

    public String getRetailerName() {
        return retailerName;
    }

    public int getItemId() {
        return itemId;
    }

    // Constructor
    public ItemEntry(String date ,long timestamp, double price, int quantity, String retailerName, int itemId) {
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
        this.retailerName = retailerName;
        this.itemId = itemId;
        this.date = date;
    }

    // Getters and setters
    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int id) {
        this.entryId = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp =timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {return quantity;}

}
