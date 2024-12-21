package com.example.stockmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    private static DATABASE database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        database = Room.databaseBuilder(getApplicationContext(),
                DATABASE.class, "stock-database").build();
    }
    public void inventorypage(View view){
        Intent intent = new Intent(this , InventoryPage.class);
        startActivity(intent);
    }

    public void salesPage(View v){
        Intent intent = new Intent(this , SalesItemPage.class);
        startActivity(intent);
    }

    public static DATABASE getDatabase() {
        return database;
    }


}