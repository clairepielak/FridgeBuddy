package com.example.fridgebuddy.ui.ShoppingList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    private List<Groceries> groceriesList;
    private ShoppingAdapter adapter;
    private EditText newItemEditText;
    private Button addItemButton;
    private Button rmItemButton;
    private RecyclerView rvShoppingList;

    private static final String PREF_NAME = "ShoppingListPrefs";
    private static final String KEY_GROCERY_LIST = "groceryList";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        groceriesList = loadGroceriesList();

        adapter = new ShoppingAdapter();
        adapter.setItems(groceriesList);

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
        newItemEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

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

            // Save the updated list
            saveGroceriesList(groceriesList);

            // Clear the EditText
            newItemEditText.setText("");

            // Close the keyboard when added
            closeKeyboard();
        }
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Load the saved grocery list from SharedPreferences
    private List<Groceries> loadGroceriesList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_GROCERY_LIST, "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Groceries>>() {
            }.getType();
            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    // Save the grocery list to SharedPreferences
    private void saveGroceriesList(List<Groceries> groceriesList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = new Gson().toJson(groceriesList);
        editor.putString(KEY_GROCERY_LIST, json);
        editor.apply();
    }
}