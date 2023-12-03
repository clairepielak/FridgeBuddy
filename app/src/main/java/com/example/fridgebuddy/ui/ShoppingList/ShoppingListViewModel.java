package com.example.fridgebuddy.ui.ShoppingList;

// Groceries.java
public class ShoppingListViewModel {
    private String title;
    private int quantity;

    public ShoppingListViewModel(String title, int quantity) {
        this.title = title;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }
}

