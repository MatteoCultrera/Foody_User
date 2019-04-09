package com.example.foodyrestaurant;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Card {

    public String title;
    public ArrayList<Dish> dishes;

    public Card(String title) {
        this.title = title;
        dishes = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    public void removeDish(Dish toRemove){
        dishes.remove(toRemove);
    }


}
