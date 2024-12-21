package com.example.stockmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class SalesItemPage extends AppCompatActivity implements RecycleViewAdapter.itemClickListener {

    private DATABASE db;
    private ItemDAO itemDAO;
    private List<Item> items;
    private RecycleViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sales_item_page);

        db = MainActivity.getDatabase();
        itemDAO = db.itemDAO();
        items = new ArrayList<>();


        recyclerViewAdapter = new RecycleViewAdapter(items, SalesItemPage.this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        loaditems();

        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }












    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loaditems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Item> itemsList = db.itemDAO().getAllItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        items.clear();
                        items.addAll(itemsList);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onButtonClick(int position) {
        Item clickedItem = items.get(position);
        // TODO: Implement sales functionality here
        // Add sales entry to item_entries table
        // Update total quantity for the item in items table
        // Update total sales amount for the item in items table
        // Update last_sold_date for the item in items table
        // Update total_sold_quantity for the item in items table
        // Update total_sold_amount for the item in items table
    }

    @Override
    public void onLongClick(int position) {

    }
}