package com.example.stockmanager;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private String name;


    public Item(String name) {
        this.name = name;
    }
    @Ignore
    public Item(int id){this.id = id;}

    public String getName() {
        return name;
    }

    public int totalQuantity ;

    public int getTotalQuantity() {
        return totalQuantity ;
    }

    public void setTotalQuantity(int quantity) {
        this.totalQuantity = quantity;
    }
}

