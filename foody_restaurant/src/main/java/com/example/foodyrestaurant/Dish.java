package com.example.foodyrestaurant;

import android.net.Uri;

public class Dish {

    private String dishName;
    private String dishDescription;
    private String price;
    private Uri image;

    public Dish(String name, String description, String price, Uri image){
        dishName = name;
        dishDescription = description;
        this.price = price;
        this.image = image;
    }

    public String getDishName(){
        return dishName;
    }

    public String getDishDescription(){
        return dishDescription;
    }

    public String getPrice(){
        return price;
    }

    public Uri getImage(){
        return image;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

}
