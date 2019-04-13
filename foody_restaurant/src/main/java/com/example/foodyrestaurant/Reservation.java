package com.example.foodyrestaurant;

import java.util.ArrayList;

public class Reservation {
    private String reservationID;
    private ArrayList<Dish> dishesOrdered;
    private int preparationStatus;
    private boolean accepted;

    //User useful data
    private String userName;
    private String userPhone;
    private String userEmail;
    private String userAddress;

    private String resNote;
    private String orderTime;



    public Reservation(String identifier, ArrayList<Dish> dishes, int preparationStatus){
        this.reservationID = identifier;
        this.dishesOrdered = dishes;
        this.preparationStatus = preparationStatus;
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

    public int getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(int preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
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
}
