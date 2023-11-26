package com.example.fridgebuddy.ui.ShoppingList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ShoppingListViewModel extends ViewModel {
    private MutableLiveData<List<Groceries>> groceriesListLiveData = new MutableLiveData<>();

    public LiveData<List<Groceries>> getGroceriesListLiveData() {
        return groceriesListLiveData;
    }

    public void setGroceriesList(List<Groceries> groceriesList) {
        groceriesListLiveData.setValue(groceriesList);
    }
}

