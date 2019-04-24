package com.example.foodyrestaurant;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

class Dish {

    private String dishName;
    private String dishDescription;
    private Float price;
    private Uri image;
    private boolean added;
    private int quantity;
    private boolean prepared;
    private boolean available;
    private boolean editImage;

    public Dish(){}

    public Dish(String name, String description, Float price, Uri image){
        dishName = name;
        dishDescription = description;
        this.price = price;
        this.image = image;
        added = false;
        quantity = 0;
        prepared = false;
        editImage = false;
        available = true;
    }

    void setAvailable(boolean b){
        this.available = b;
    }

    boolean isAvailable(){
        return this.available;
    }

    void setQuantity(int quantity){
        this.quantity = quantity;
    }

    String getStringForRes() {
        return quantity+" x "+dishName;
    }

    @NonNull
    @Override
    public String toString() {
        return dishName+" "+dishDescription+" "+String.format(Locale.UK, "%.2f €",price)+" "+(image==null?"null":image.toString());
    }

    String getDishName(){
        return dishName;
    }

    String getDishDescription(){
        return dishDescription;
    }

    Float getPrice(){
        return price;
    }

    Uri getImage(){
        return image;
    }

    void setDishName(String dishName) {
        this.dishName = dishName;
    }

    void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    void setPrice(Float price) {
        this.price = price;
    }

    void setImage(Uri image) {
        this.image = image;
    }

    void setAdded(boolean added){
        this.added = added;
    }

    void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    boolean isEditImage() {
        return editImage;
    }

    void setEditImage(boolean editImage) {
        this.editImage = editImage;
    }

    int getQuantity() {
        return quantity;
    }

    boolean isPrepared() {
        return prepared;
    }
}

