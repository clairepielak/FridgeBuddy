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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment implements ShoppingAdapter.OnQuantityChangeListener {

    public List<Groceries> groceriesList;
    private ShoppingAdapter adapter;
    private EditText quantity;
    private EditText newItemEditText;
    private Button addItemButton;
    private Button rmItemButton;
    private RecyclerView rvShoppingList;
    private int currentQuantity = 1;

    private static final String PREF_NAME = "ShoppingListPrefs";
    private static final String KEY_GROCERY_LIST = "groceryList";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        groceriesList = loadGroceriesList();

        adapter = new ShoppingAdapter();
        adapter.setItems(groceriesList);
        adapter.setOnQuantityChangeListener(this);

        newItemEditText = view.findViewById(R.id.etList);
        addItemButton = view.findViewById(R.id.newItemButton);
        rmItemButton = view.findViewById(R.id.rmButton);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);
        quantity = view.findViewById(R.id.quantity);

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

    public void onQuantityChange(int position, int newQuantity) {
        if (position >= 0 && position < groceriesList.size()) {
            Groceries updatedItem = groceriesList.get(position);
            updatedItem.setQuantity(newQuantity);
            saveGroceriesList(groceriesList);
        }

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
            Type type0 = new TypeToken<List<Groceries>>() {}.getType();
            return new Gson().fromJson(json, type0);
        } else {
            return new ArrayList<>();
        }
    }

    // Save the grocery list to SharedPreferences
    public void saveGroceriesList(List<Groceries> groceriesList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = new Gson().toJson(groceriesList);
        editor.putString(KEY_GROCERY_LIST, json);
        editor.apply();
    }
    public int getItemQuantity(int position) {
        if (position >= 0 && position < groceriesList.size()) {
            return groceriesList.get(position).getQuantity();
        }
        return 1; //Return a default value
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        int newPosition = groceriesList.size() - 1;
        if (adapter != null && adapter.getItemCount() > 0) {
            int lastItemQuantity = getItemQuantity(newPosition);
            Groceries lastItem = groceriesList.get(newPosition);
            lastItem.setQuantity(lastItemQuantity);
        }

        if (adapter != null) {
            saveGroceriesList(groceriesList);
        }

        rvShoppingList.setAdapter(null);
        adapter = null;
    }
}