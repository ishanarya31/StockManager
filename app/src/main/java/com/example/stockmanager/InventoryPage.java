package com.example.stockmanager;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class InventoryPage extends AppCompatActivity implements RecycleViewAdapter.itemClickListener {

    private static List<Item> items;                                                       //list of items
    private RecycleViewAdapter recyclerViewAdapter;                                 //recycler view adapter
    private DATABASE db;                                                            //instantiate database class
    private ItemDAO itemDAO;                                                        //instantiate itemDAO class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_page);
        db = MainActivity.getDatabase();
        itemDAO = db.itemDAO();
        items = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewAdapter = new RecycleViewAdapter(items, InventoryPage.this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        loadItems();                                           // load items from the database

        // FloatingActionButton to add items
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton searchButton = findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateOptionsMenu();
            }
        });

    }





    private void filterList(String newText) {
        List<Item>filteredList = new ArrayList<Item>();
        for(Item item : items){
            if(item.getName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(InventoryPage.this , "No Items Found" , Toast.LENGTH_SHORT).show();
        }
        else{
            recyclerViewAdapter.setFilteredList(filteredList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showInputDialog() {
        // Your code for adding items
        View dialogView = getLayoutInflater().inflate(R.layout.item_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryPage.this).setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editItemName = dialogView.findViewById(R.id.itemName);
        Button buttonAdd = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editItemName.getText().toString();
                if(name.isEmpty()){
                    editItemName.setError("Please enter a name");
                    return;
                }

                Item newItem = new Item(name);

                // Add item to RecyclerView and database in a background thread
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        itemDAO.insertItem(newItem);
                        // Reload items from database and update the UI
                        List<Item> itemList = itemDAO.getAllItems();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                items.clear();
                                items.addAll(itemList);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onButtonClick(int position) {
        Intent intent = new Intent(this, itemDescription.class);
        Item clickedItem = items.get(position);
        intent.putExtra(itemDescription.ITEM_ID, clickedItem.getId());
        intent.putExtra(itemDescription.ITEM_NAME, clickedItem.getName());
        startActivity(intent);
    }

    @Override
    public void onLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setTitle("DELETE ITEM");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        itemDAO.deleteItem(items.get(position).getId());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                items.remove(position);
                                recyclerViewAdapter.notifyDataSetChanged();
                                // Update the UI with the remaining items in the list
                            }
                        });
                    }
                }).start();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void loadItems() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {            //creates a thread to execute database operations
            @Override
            public void run() {
                List<Item> itemList = itemDAO.getAllItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        items.clear();
                        items.addAll(itemList);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


}
