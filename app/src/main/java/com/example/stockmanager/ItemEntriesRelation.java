package com.example.stockmanager;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ItemEntriesRelation {
    @Embedded
    public Item item;

    @Relation(parentColumn = "id", entityColumn = "itemId")
    public List<ItemEntry> entries;
}
