package com.example.fridgebuddy.ui.ShoppingList;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.GroceriesViewHolder> {

    private List<ShoppingListViewModel> items = new ArrayList<>();

    public void setItems(List<ShoppingListViewModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroceriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list, parent, false);
        return new GroceriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceriesViewHolder holder, int position) {
        ShoppingListViewModel shoppingListViewModel = items.get(position);
        holder.bind(shoppingListViewModel);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class GroceriesViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private NumberPicker quantityTextView;
        private Button rmItemButton;
        private Handler mHandler;
        private ShoppingListFragment fragment;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tvTitle);
            rmItemButton = itemView.findViewById(R.id.rmButton);
            quantityTextView = itemView.findViewById(R.id.quantity);

            quantityTextView.setMinValue(1);
            quantityTextView.setMaxValue(10);

            rmItemButton.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    fragment.removeItem(clickedPosition);
                }
            });
        }

        public void bind(ShoppingListViewModel shoppingListViewModel) {
            titleTextView.setText(shoppingListViewModel.getTitle());
            quantityTextView.setValue(shoppingListViewModel.getQuantity());
        }
    }
    public void saveDataToSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("shopping_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (ShoppingListViewModel item : items) {
            editor.putInt(item.getTitle(), item.getQuantity());
        }

        editor.apply();
    }

    public void loadDataFromSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("shopping_data", Context.MODE_PRIVATE);

        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String title = entry.getKey();
            int quantity = preferences.getInt(title, 1);
            ShoppingListViewModel item = new ShoppingListViewModel(title, quantity);
            items.add(item);
        }
        notifyDataSetChanged();
    }

    public boolean isSharedPreferencesEmpty(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("shopping_data",Context.MODE_PRIVATE);
        return preferences.getAll().isEmpty();
    }
}
