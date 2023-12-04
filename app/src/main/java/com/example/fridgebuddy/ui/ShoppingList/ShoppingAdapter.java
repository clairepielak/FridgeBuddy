package com.example.fridgebuddy.ui.ShoppingList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

    private List<Groceries> items = new ArrayList<>();

    public void setItems(List<Groceries> items) {
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
        Groceries shoppingListViewModel = items.get(position);
        holder.bind(shoppingListViewModel);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItemQuantity(int position, int newQuantity) {
        if (position >= 0 && position < items.size()) {
            items.get(position).setQuantity(newQuantity);
            notifyItemChanged(position);
        }
    }

    public class GroceriesViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private NumberPicker quantityTextView;
        private Button rmItemButton;

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
                    removeItem(clickedPosition);
                }
            });

            quantityTextView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    int valuePicker = quantityTextView.getValue();
                    Log.d("Quantity", valuePicker + "");
                }
            });
        }

        public void bind(Groceries shoppingListViewModel) {
            titleTextView.setText(shoppingListViewModel.getTitle());
            quantityTextView.setValue(shoppingListViewModel.getQuantity());
        }
        private void removeItem(int position) {
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }
    }
}
