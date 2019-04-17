package com.example.foodyrestaurant;

import java.util.ArrayList;

public class Reservation {

    enum prepStatus {
        PENDING,
        DOING,
        DONE,
    }

    private String reservationID;
    private ArrayList<Dish> dishesOrdered;
    private boolean accepted;
    private prepStatus preparationStatus;
    private int toBePrepared;

    //User useful data
    private String userName;
    private String userPhone;
    private String userLevel;
    private String userEmail;
    private String userAddress;

    private String resNote;
    private String orderTime;

    public Reservation(String identifier, ArrayList<Dish> dishes, prepStatus preparationStatus, boolean accepted,
                       String orderTime, String userName, String userPhone, String resNote, String userLevel,
                       String userEmail, String userAddress){
        this.reservationID = identifier;
        this.dishesOrdered = dishes;
        this.accepted = accepted;
        this.preparationStatus = preparationStatus;
        this.orderTime = orderTime;
        this.userName = userName;
        this.userPhone = userPhone;
        this.resNote = resNote;
        this.userLevel = userLevel;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        toBePrepared = dishes.size();
    }

    public String getReservationID() {
        return " "+reservationID;
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

    public prepStatus getPreparationStatus() {
        return preparationStatus;
    }

    public String getPreparationStatusString(){
        String ret;
        switch (this.preparationStatus){
            case PENDING:
                ret = "Pending";
                break;
            case DONE:
                ret = "Done";
                break;
            case DOING:
                ret="Doing";
                break;
            default:
                ret="Status Unknown";
                break;
        }
        return ret;
    }

    public void setPreparationStatus(prepStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
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

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public int getToBePrepared() {
        return toBePrepared;
    }

    public int incrementToBePrepared(int number) {
        if(number == 1)
            toBePrepared--;
        else
            toBePrepared++;
        return toBePrepared;
    }
}
