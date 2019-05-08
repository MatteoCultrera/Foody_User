package com.example.foodyuser;

import java.util.ArrayList;

public class ReservationDBRestaurant {

    private String reservationID;
    private String bikerID;
    private ArrayList<OrderItem> dishesOrdered;
    private boolean accepted;
    private String resNote;
    private String numberPhone;
    private String userAddress;
    private String nameUser;
    private String orderTime;
    private String status;
    private String orderTimeBiker;
    private String totalCost;

    public ReservationDBRestaurant() {
    }

    public ReservationDBRestaurant(String reservationID, String bikerID, ArrayList<OrderItem> dishesOrdered, boolean accepted, String resNote, String numberPhone, String nameUser, String orderTime, String orderTimeBiker, String status, String userAddress, String totalCost) {
        this.reservationID = reservationID;
        this.bikerID = bikerID;
        this.dishesOrdered = dishesOrdered;
        this.accepted = accepted;
        this.resNote = resNote;
        this.numberPhone = numberPhone;
        this.nameUser = nameUser;
        this.orderTime = orderTime;
        this.orderTimeBiker = orderTimeBiker;
        this.status = status;
        this.userAddress = userAddress;
        this.totalCost = totalCost;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getBikerID() {
        return bikerID;
    }

    public void setBikerID(String bikerID) {
        this.bikerID = bikerID;
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

    public String getOrderTimeBiker() {
        return orderTimeBiker;
    }

    public void setOrderTimeBiker(String orderTimeBiker) {
        this.orderTimeBiker = orderTimeBiker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
