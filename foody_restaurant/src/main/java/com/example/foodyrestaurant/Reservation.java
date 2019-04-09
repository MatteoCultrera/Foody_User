package com.example.foodyrestaurant;

import java.util.ArrayList;

public class Reservation {
    private String reservationID;
    private ArrayList<Dish> dishesOrdered;

    public Reservation(String identifier, ArrayList<Dish> dishes){
        this.reservationID = identifier;
        this.dishesOrdered = dishes;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public ArrayList<Dish> getDishesOrdered() {
        return dishesOrdered;
    }

    public void setDishesOrdered(ArrayList<Dish> dishesOrdered) {
        this.dishesOrdered = dishesOrdered;
    }

}
