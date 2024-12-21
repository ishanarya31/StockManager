package com.example.stockmanager;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
@Dao
public interface ItemDAO {
    @Insert
    void insertItem(Item item);

    @Insert
    void insertLog(ItemEntry log);

    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    @Query("DELETE FROM items")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM items WHERE id = :itemId")
    ItemEntriesRelation getItemEntries(int itemId);

    @Query("SELECT * FROM items WHERE id = :itemId")
    Item getItemById(int itemId);

    @Query("SELECT SUM(quantity) FROM item_entries WHERE itemId = :itemId")
    int getTotalQuantityForItem(int itemId);

    @Update
    void updateItem(Item item);

    @Query("DELETE FROM item_entries WHERE entryId IN (SELECT entryId FROM item_entries WHERE itemId = :itemId ORDER BY entryId DESC LIMIT :n)")
    void deleteLastNEntriesForItem(int itemId, int n);

    @Query("SELECT * FROM item_entries WHERE itemId = :itemId ORDER BY timestamp ASC")
    List<ItemEntry> getItemEntriesSortedByDate(int itemId);

    @Query("SELECT * FROM item_entries WHERE itemId = :itemId ORDER BY price DESC")
    List<ItemEntry> getItemEntriesSortedByPrice(int itemId);

    @Query("SELECT * FROM item_entries WHERE itemId = :itemId ORDER BY quantity DESC")
    List<ItemEntry> getItemEntriesSortedByQuantity(int itemId);

    @Query("DELETE FROM ITEMS WHERE id = :itemId")
    void deleteItem(int itemId);

}
