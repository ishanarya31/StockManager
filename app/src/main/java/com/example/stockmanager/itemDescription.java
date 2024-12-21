package com.example.stockmanager;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.DeleteTable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

public class itemDescription extends AppCompatActivity {


    private List<ItemEntry> entryList;
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_NAME = "item_name";

    private int itemID;


    private int totalQuantity = 0;
    private double totalprice = 0.0;

    private ItemDAO itemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_description);

        entryList = new ArrayList<>();
        itemDAO = MainActivity.getDatabase().itemDAO();

        TextView itemName = findViewById(R.id.itemName);
        TextView itemQuantity = findViewById(R.id.qty);
        TextView itemPrice = findViewById(R.id.stockValue);
        ImageButton imageButton = findViewById(R.id.edit);
        ImageButton imageButton2 = findViewById(R.id.sortBy);

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortEntries(itemID);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu();
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            itemID = intent.getIntExtra(ITEM_ID, -1);  // Ensure itemID is correctly retrieved
            String itemNameValue = intent.getStringExtra(ITEM_NAME);

            itemName.setText(itemNameValue);

            loadEntries(itemID);
        }


        Button button = findViewById(R.id.entryButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataEntry();                                     //handles data entry of the individual items
            }
        });


        ImageButton backButton = findViewById(R.id.imageButton2);
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

    private void dataEntry() {
        View dialogView = getLayoutInflater().inflate(R.layout.item_entry_dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(itemDescription.this).setView(dialogView);
        AlertDialog dialog = builder.create();

        TextInputLayout inputRetailer = dialogView.findViewById(R.id.retailerName);
        TextInputLayout inputDate = dialogView.findViewById(R.id.date);
        TextInputLayout inputQuantity = dialogView.findViewById(R.id.quantity);
        TextInputLayout inputPrice = dialogView.findViewById(R.id.price);
        Button button1 = dialogView.findViewById(R.id.saveButton);
        Button button2 = dialogView.findViewById(R.id.cancelButton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String retailerName = Objects.requireNonNull(inputRetailer.getEditText()).getText().toString();
                if (retailerName.isEmpty()) {
                    inputRetailer.setError("Please enter a name");
                    return;
                }
                String dateString = Objects.requireNonNull(inputDate.getEditText()).getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                long timestamp;
                try {
                    Date date = sdf.parse(dateString);
                    assert date != null;
                    timestamp = date.getTime();
                } catch (ParseException e) {
                    inputDate.setError("Please enter a valid date");
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(Objects.requireNonNull(inputQuantity.getEditText()).getText().toString());
                } catch (NumberFormatException e) {
                    inputQuantity.setError("Please enter a valid quantity");
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(Objects.requireNonNull(inputPrice.getEditText()).getText().toString());
                } catch (NumberFormatException e) {
                    inputPrice.setError("Please enter a valid price");
                    return;
                }

                ItemEntry entry = new ItemEntry(dateString,timestamp, price, quantity, retailerName, itemID);
                entryList.add(entry);



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        itemDAO.insertLog(entry);

                    }
                }).start();

                // Create new row for each entry in the table
                TableLayout table = findViewById(R.id.tableLayout);
                TableRow newRow = (TableRow) getLayoutInflater().inflate(R.layout.entry_table_row, null);

                TextView itemDateAdded = newRow.findViewById(R.id.textView8);
                TextView itemRetailerName = newRow.findViewById(R.id.textView11);
                TextView itemQuantity = newRow.findViewById(R.id.quantity);
                TextView itemPrice = newRow.findViewById(R.id.price);

                itemDateAdded.setText(entry.getDate());
                itemRetailerName.setText(entry.getRetailerName());
                itemQuantity.setText(String.valueOf(entry.getQuantity()));
                itemPrice.setText(String.valueOf(entry.getPrice()));
                table.addView(newRow);
                updateTotalQuantity();
                updateTotalPrice();
                dialog.dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadEntries(int itemID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ItemEntry> list = itemDAO.getItemEntriesSortedByDate(itemID);
                if (list != null) {
                    entryList.addAll(list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTable();
                            updateTotalPrice();
                            updateTotalQuantity();
                        }
                    });
                }
            }
        }).start();
    }

    private void updateTable() {
        TableLayout table = findViewById(R.id.tableLayout);
        table.removeAllViews();


        for (ItemEntry entry : entryList) {
            TableRow newRow = (TableRow) getLayoutInflater().inflate(R.layout.entry_table_row, null);
            TextView itemDateAdded = newRow.findViewById(R.id.textView8);
            TextView itemRetailerName = newRow.findViewById(R.id.textView11);
            TextView itemQuantity = newRow.findViewById(R.id.quantity);
            TextView itemPrice = newRow.findViewById(R.id.price);

            itemDateAdded.setText(entry.getDate());
            itemRetailerName.setText(entry.getRetailerName());
            itemQuantity.setText(String.valueOf(entry.getQuantity()));
            itemPrice.setText(String.valueOf(entry.getPrice()));
            table.addView(newRow);
        }
    }


    private void updateTotalQuantity() {
        totalQuantity = 0; // Reset total quantity
        for (ItemEntry entry : entryList) {
            totalQuantity += entry.getQuantity();
        }
        TextView textView = findViewById(R.id.qty);
        textView.setText("Quantity in Stock: " + totalQuantity);
    }

    private void updateTotalPrice(){
        totalprice = 0.0;
        for(ItemEntry entry : entryList){
            totalprice += entry.getPrice();
        }
        TextView textView1 = findViewById(R.id.stockValue);
        textView1.setText("Stock Value: " + totalprice);
    }

    private void popupMenu() {
        View editButton = findViewById(R.id.edit);
        if (editButton == null) {
            Toast.makeText(itemDescription.this, "Edit button not found!", LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(itemDescription.this, editButton);

        popupMenu.getMenuInflater().inflate(R.menu.item_description, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                int actionEditId = getResources().getIdentifier("action_edit", "id", getPackageName());
                int actionDeleteId = getResources().getIdentifier("delete_action", "id", getPackageName());

                if (itemId == actionEditId) {
                    // Handle Edit action
                    Toast.makeText(itemDescription.this, "Feature Coming Soon!", LENGTH_SHORT).show();
                    return true;
                } else if (itemId == actionDeleteId) {
                    // Handle Delete action
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemDescription.this);
                            builder.setIcon(android.R.drawable.ic_menu_delete);
                            builder.setTitle("Delete Last Entry");
                            builder.setMessage("Are you sure you want to delete the last entry?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!entryList.isEmpty()) {
                                                ItemEntry lastEntry = entryList.remove(entryList.size() - 1);
                                                itemDAO.deleteLastNEntriesForItem(itemID, 1);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        updateTable();
                                                        updateTotalPrice();
                                                        updateTotalQuantity();
                                                    }
                                                });
                                            }
                                            dialog.dismiss();
                                        }
                                    }).start();
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
                    });
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void sortEntries(int itemID){
            View v = findViewById(R.id.sortBy);

            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.sort_table_dropdown, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int itemId = item.getItemId();
                    int sortByDateId = getResources().getIdentifier("date_added", "id", getPackageName());
                    int sortByQuantity = getResources().getIdentifier("quantity", "id", getPackageName());
                    int sortByPrice = getResources().getIdentifier("price", "id", getPackageName());

                    if(itemId == sortByDateId){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                entryList = itemDAO.getItemEntriesSortedByDate(itemID);
                                if (entryList != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateTable();
                                        }
                                    });
                                }
                            }
                        }).start();
                        return true;
                    }


                    else if(itemId ==sortByQuantity){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                entryList = itemDAO.getItemEntriesSortedByQuantity(itemID);
                                if(entryList != null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateTable();
                                        }
                                    });
                                }
                            }
                        }).start();
                        return true;
                    }


                    else if(itemId == sortByPrice){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                entryList = itemDAO.getItemEntriesSortedByPrice(itemID);
                                if(entryList!=null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateTable();
                                        }
                                    });
                                }
                            }
                        }).start();
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
    }

}
