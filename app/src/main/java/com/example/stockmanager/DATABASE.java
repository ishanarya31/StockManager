package com.example.stockmanager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Item.class, ItemEntry.class}, version = 2, exportSchema = false)
public abstract class DATABASE extends RoomDatabase {

    public abstract ItemDAO itemDAO() ;

    @Override
    public void clearAllTables() {

    }


    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
