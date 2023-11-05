package com.example.fridgebuddy.ui;

public class FooditemModel {
    String FoodName;
    String FoodExpirationDate;
    String Quantity;
    int image;
    String DaystoExpire;


    public FooditemModel(String foodName, String foodExpirationDate, String quantity, int image, String daystoExpire) {
        FoodName = foodName;
        FoodExpirationDate = foodExpirationDate;
        Quantity = quantity;
        this.image = image;
        DaystoExpire = daystoExpire;
    }

    public String getFoodName() {
        return FoodName;
    }

    public String getFoodExpirationDate() {
        return FoodExpirationDate;
    }

    public String getQuantity() {
        return Quantity;
    }

    public int getImage() {
        return image;
    }

    public String getDaystoExpire() {
        return DaystoExpire;
    }
}
