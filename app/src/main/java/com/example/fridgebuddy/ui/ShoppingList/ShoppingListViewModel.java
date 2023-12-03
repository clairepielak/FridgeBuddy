package com.example.fridgebuddy.ui.ShoppingList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;


public class ShoppingListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Groceries>> groceriesListLiveData = new MutableLiveData<>();

    public ShoppingListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Groceries>> getGroceriesListLiveData() {
        return groceriesListLiveData;
    }

    public void setGroceriesList(List<Groceries> groceriesList) {
        groceriesListLiveData.setValue(groceriesList);
    }
}

