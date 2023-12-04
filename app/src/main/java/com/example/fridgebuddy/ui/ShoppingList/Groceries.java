package com.example.fridgebuddy.ui.ShoppingList;

// Groceries.java
public class Groceries {
    private String title;
    private int quantity;

    public Groceries(String title, int quantity) {
        this.title = title;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

