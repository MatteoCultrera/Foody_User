package com.example.foodyrestaurant;

import android.net.Uri;

import java.util.Locale;

class Dish {

    private String dishName;
    private String dishDescription;
    private Float price;
    private Uri image;
    private boolean added;
    private int quantity;
    private boolean prepared;
    private boolean editImage;

    public Dish(String name, String description, Float price, Uri image){
        dishName = name;
        dishDescription = description;
        this.price = price;
        this.image = image;
        added = false;
        quantity = 0;
        prepared = false;
        editImage = false;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public String getStringForRes(){
        return quantity+" x "+dishName;
    }

    @Override
    public String toString() {
        return dishName+" "+dishDescription+" "+String.format(Locale.UK, "%.2f €",price)+" "+(image==null?"null":image.toString());
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

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    public boolean isEditImage() {
        return editImage;
    }

    public void setEditImage(boolean editImage) {
        this.editImage = editImage;
    }

}
