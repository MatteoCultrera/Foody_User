package com.example.foodyrestaurant;

import android.net.Uri;

public class Dish {

    private String dishName;
    private String dishDescription;
    private Float price;
    private Uri image;
    private boolean added;

    public Dish(String name, String description, Float price, Uri image){
        dishName = name;
        dishDescription = description;
        this.price = price;
        this.image = image;
        added = false;
    }

    public boolean getAdded(){
        return added;
    }

    public String getDishName(){
        return dishName;
    }

    public String getDishDescription(){
        return dishDescription;
    }

    public Float getPrice(){
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

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public void setAdded(boolean added){
        this.added = added;
    }

}
