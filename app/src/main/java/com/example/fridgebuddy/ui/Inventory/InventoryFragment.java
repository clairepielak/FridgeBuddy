package com.example.fridgebuddy.ui.Inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgebuddy.R;
import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.database.ItemDatabase;

import java.util.List;

public class InventoryFragment extends Fragment {
    private InventoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        InventoryViewModel inventoryViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(InventoryViewModel.class);

        ItemDatabase itemDB = ItemDatabase.getDatabase(requireContext().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        // Observe the LiveData from the ViewModel
        inventoryViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            // Update the adapter with the new list
            adapter.setItems(items);
        });

        LiveData<List<Item>> itemList = itemDB.itemDao().orderItemByName();
        adapter = new InventoryAdapter(requireContext().getApplicationContext(), itemDB);
        RecyclerView rvInvList = view.findViewById(R.id.rvInvList);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvInvList.setLayoutManager(layoutManager);
        rvInvList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.example.fridgebuddy.databinding.FragmentInventoryBinding binding = null;
    }
}