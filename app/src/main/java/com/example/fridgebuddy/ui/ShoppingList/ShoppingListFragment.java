package com.example.fridgebuddy.ui.ShoppingList;

// ShoppingListFragment.java
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    private List<Groceries> groceriesList;
    private ShoppingAdapter adapter;
    private EditText newItemEditText;
    private Button addItemButton;
    private Button rmItemButton;
    private RecyclerView rvShoppingList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        groceriesList = new ArrayList<>();
        adapter = new ShoppingAdapter();

        newItemEditText = view.findViewById(R.id.etList);
        addItemButton = view.findViewById(R.id.newItemButton);
        rmItemButton = view.findViewById(R.id.rmButton);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvShoppingList.setLayoutManager(layoutManager);
        rvShoppingList.setAdapter(adapter);

        addItemButton.setOnClickListener(v -> addItem());

        //Can only enter 8 character items
        int maxLength = 8;
        newItemEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        return view;
    }

    private void addItem() {
        String newItemTitle = newItemEditText.getText().toString();
        if (!newItemTitle.isEmpty()) {
            // Add a new item to the list
            Groceries newItem = new Groceries(newItemTitle, 1);
            groceriesList.add(newItem);

            // Update the adapter
            adapter.setItems(groceriesList);

            // Clear the EditText
            newItemEditText.setText("");
        }
    }
}
