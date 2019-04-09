package com.example.foodyrestaurant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Card {
    public String title;
    public ArrayList<Dish> dishes;

    public Card(String title) {
        this.title = title;
        dishes = new ArrayList<>();
    }

    public Card(String title, ArrayList<Dish> dishes){
        this.title = title;
        this.dishes = dishes;
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

}
