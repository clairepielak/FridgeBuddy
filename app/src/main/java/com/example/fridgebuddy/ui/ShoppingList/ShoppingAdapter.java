package com.example.fridgebuddy.ui.ShoppingList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.GroceriesViewHolder> {

    private List<Groceries> items;

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
        Groceries groceries = items.get(position);
        holder.bind(groceries);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
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
        }

        public void bind(Groceries groceries) {
            titleTextView.setText(groceries.getTitle());
            quantityTextView.setValue(groceries.getQuantity());
        }

        public void removeItem(int position) {
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }
    }
}
