package com.example.fridgebuddy.ui.Inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;
import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.util.Converters;
import com.example.fridgebuddy.util.Util;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private final Context context;
    private List<Item> itemList;
    private final ItemDatabase itemDB;
    private final Util util = new Util();

    public InventoryAdapter(Context context, ItemDatabase itemDB) {
        this.context = context;
        this.itemDB = itemDB;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inv, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemNameView;
        private final TextView expDateView;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameView = itemView.findViewById(R.id.food_name);
            expDateView = itemView.findViewById(R.id.exp_date);
            ImageView removeBtn = itemView.findViewById(R.id.remove_btn);

            removeBtn.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    util.deleteItem(context, itemDB, getItem(clickedPosition));
                }
            });
        }

        public void bind(Item item) {
            itemNameView.setText(item.getName());

            if (item.isExpired()) {
                // If the item is expired, set the text to "Expired"
                expDateView.setText("Expired");
                expDateView.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                // If the item is not expired, display the formatted expiration date
                expDateView.setText(Converters.dateToMonthDay(item.getExpDate()));
                expDateView.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }

        public Item getItem(int position) {
            if (position >= 0 && position < itemList.size()) {
                return itemList.get(position);
            }

            // handle out of bounds
            return null;
        }
    }
}
