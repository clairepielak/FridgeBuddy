package com.example.fridgebuddy.ui.ShoppingList;

// ShoppingListFragment.java
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    private List<ShoppingListViewModel> shoppingListViewHolderList;
    private ShoppingAdapter adapter;
    private EditText newItemEditText;
    private Button addItemButton;
    private Button rmItemButton;
    private RecyclerView rvShoppingList;
    private Handler mHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        shoppingListViewHolderList = new ArrayList<>();
        adapter = new ShoppingAdapter();
        mHandler = new Handler();

        newItemEditText = view.findViewById(R.id.etList);
        addItemButton = view.findViewById(R.id.newItemButton);
        rmItemButton = view.findViewById(R.id.rmButton);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvShoppingList.setLayoutManager(layoutManager);
        rvShoppingList.setAdapter(adapter);

        addItemButton.setOnClickListener(v -> addItem());

        //Can only enter 8 character titles
        int maxLength = 8;
        newItemEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!adapter.isSharedPreferencesEmpty(getContext().getApplicationContext())) {
                            adapter.loadDataFromSharedPreferences(getContext().getApplicationContext());
                        }
                    }
                });
            }
        }).start();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().post(() -> {
            if (adapter != null && !adapter.isSharedPreferencesEmpty(requireContext())) {
                adapter.loadDataFromSharedPreferences(requireContext());
            }
        });
    }

    private void addItem() {
        String newItemTitle = newItemEditText.getText().toString();
        if (!newItemTitle.isEmpty()) {
            // Add a new item to the list
            ShoppingListViewModel newItem = new ShoppingListViewModel(newItemTitle, 1);
            shoppingListViewHolderList.add(newItem);

            // Update the adapter
            adapter.setItems(shoppingListViewHolderList);

            // Clear the EditText
            newItemEditText.setText("");

            new Handler().post(() -> {
                adapter.saveDataToSharedPreferences(requireContext());
            });
        }
    }
    public void removeItem(int position) {
        if (position >= 0 && position < shoppingListViewHolderList.size()) {
            shoppingListViewHolderList.remove(position);
            adapter.setItems(shoppingListViewHolderList);
            adapter.saveDataToSharedPreferences(requireContext());
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(adapter != null) {
            adapter.saveDataToSharedPreferences(getContext().getApplicationContext());
        }
    }
}
