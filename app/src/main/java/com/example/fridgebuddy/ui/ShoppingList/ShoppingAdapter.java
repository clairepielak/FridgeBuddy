package com.example.fridgebuddy.ui.ShoppingList;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.GroceriesViewHolder> {

    private List<Groceries> items = new ArrayList<>();
    private OnQuantityChangeListener onQuantityChangeListener;
    public interface OnQuantityChangeListener {
        void onQuantityChange(int position, int newQuantity);
    }
    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.onQuantityChangeListener = listener;
    }

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

    public class GroceriesViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        public EditText quantityEditText;
        private Button rmItemButton;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tvTitle);
            rmItemButton = itemView.findViewById(R.id.rmButton);
            quantityEditText = itemView.findViewById(R.id.quantity);

            int maxQuantity = 99;
            quantityEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, maxQuantity)});

            rmItemButton.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    removeItem(clickedPosition);
                }
            });

            quantityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //not used
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //not used
                }

                @Override
                public void afterTextChanged(Editable s) {
                    final String TAG = "msg";
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onQuantityChangeListener != null) {
                            try {
                                int newQuantity = Integer.parseInt(s.toString());
                                onQuantityChangeListener.onQuantityChange(position, newQuantity);
                            } catch (NumberFormatException e) {
                                Log.d(TAG, "Invalid number!");
                            }
                        }
                    }
                }
            });
        }

        public void bind(Groceries shoppingListViewModel) {
            titleTextView.setText(shoppingListViewModel.getTitle());
            quantityEditText.setText(String.valueOf(shoppingListViewModel.getQuantity()));
        }

        //Class for limiting max value of quantity
        public class InputFilterMinMax implements InputFilter {
            private int min, max;

            public InputFilterMinMax(int min, int max) {
                this.min = min;
                this.max = max;
            }

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    int input = Integer.parseInt(dest.toString() + source.toString());
                    if (isInRange(min, max, input))
                        return null;
                } catch (NumberFormatException ignored) {
                }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        }
        private void removeItem(int position) {
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }
    }
}
