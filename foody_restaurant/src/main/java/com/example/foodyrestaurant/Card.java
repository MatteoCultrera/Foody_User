package com.example.foodyrestaurant;
import java.util.ArrayList;

public class Card {
    public String title;
    public ArrayList<Dish> dishes;
    private boolean editing;
    private boolean selected;

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
