package com.example.stockmanager;


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    // TODO: Implement this class to handle the recycling of views in the RecyclerView.

    List<Item> itemList = new ArrayList<>();    //list of items

    public interface itemClickListener{
        void onButtonClick(int position);
        void onLongClick(int position);
    }
    private itemClickListener listener;

    public RecycleViewAdapter(List<Item> itemList, itemClickListener listener) {
        this.listener = listener;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public RecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.inventory_row, parent, false);
        return new MyViewHolder(view , listener);
    }


    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.MyViewHolder holder, int position) {
        // TODO: Implement this method to populate the RecyclerView with data from the list.
        Item item = itemList.get(position);
        // Set the item name and quantity in the ViewHolder
        int itemID = item.getId();


        holder.itemName.setText(item.getName());

        // TODO: Populate the views with the item data.
    }


    @Override
    public int getItemCount() {
        // TODO: Implement this method to return the number of items in the list.
        return itemList.size();
    }

    public void setFilteredList(List<Item> filteredList) {
        // TODO: Implement this method to update the list of items based on the filtered list.
        this.itemList = filteredList;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        Button button;
        TextView quantity;

        public MyViewHolder(@NonNull View itemView , itemClickListener listener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            button = itemView.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Implement this method to handle the button click event.
                    if(listener!=null){
                        listener.onButtonClick(getAdapterPosition());
                    }
                }
            });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener != null){
                        listener.onLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });

        }
    }


}
