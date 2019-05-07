package com.example.foodyrestaurant;

import java.util.ArrayList;

public class ReservationDBUser {

    private String reservationID;
    private String restaurantID;
    private ArrayList<OrderItem> dishesOrdered;
    private boolean accepted;
    private String resNote;
    private String orderTime;

    public ReservationDBUser() {
    }

    public ReservationDBUser(String reservationID, String restaurantID, ArrayList<OrderItem> dishesOrdered, boolean accepted, String resNote, String orderTime) {
        this.reservationID = reservationID;
        this.restaurantID = restaurantID;
        this.dishesOrdered = dishesOrdered;
        this.accepted = accepted;
        this.resNote = resNote;
        this.orderTime = orderTime;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public ArrayList<OrderItem> getDishesOrdered() {
        return dishesOrdered;
    }

    public void setDishesOrdered(ArrayList<OrderItem> dishesOrdered) {
        this.dishesOrdered = dishesOrdered;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getResNote() {
        return resNote;
    }

    public void setResNote(String resNote) {
        this.resNote = resNote;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

}
