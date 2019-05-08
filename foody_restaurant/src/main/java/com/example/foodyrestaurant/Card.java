package com.example.foodyrestaurant;
import android.util.Log;

import java.util.ArrayList;

class Card {
    private String title;
    private ArrayList<Dish> dishes;
    private boolean editing;
    private boolean selected;

    public Card(){}

    public Card(String title) {
        this.title = title;
        dishes = new ArrayList<>();
        editing = false;
        selected = false;
    }

    public Card(String title, ArrayList<Dish> dishes){
        this.title = title;
        this.dishes = dishes;
    }

    public void print(){

        Log.d("TITLECHECK", title+"\n");
        for (int i = 0; i < dishes.size(); i++){
            Log.d("TITLECHECK","\t"+dishes.get(i).toString());
        }
        Log.d("TITLECHECK","\n\n");

    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
