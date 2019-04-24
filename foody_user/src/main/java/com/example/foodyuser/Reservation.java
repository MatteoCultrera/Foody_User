package com.example.foodyuser;

import java.util.ArrayList;

class Reservation {

    enum prepStatus {
        PENDING,
        DOING,
        DONE,
    }

    private final String reservationID;
    private final ArrayList<Dish> dishesOrdered;
    private boolean accepted;
    private prepStatus preparationStatus;
    private final String userName;
    private final String userPhone;
    private final String userLevel;
    private final String userEmail;
    private final String userAddress;

    private final String resNote;
    private final String orderTime;
    private int toBePrepared;

    Reservation(String identifier, ArrayList<Dish> dishes, prepStatus preparationStatus, boolean accepted,
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

    String getReservationID() {
        return reservationID;
    }

    ArrayList<Dish> getDishesOrdered() {
        return dishesOrdered;
    }

    prepStatus getPreparationStatus() {
        return preparationStatus;
    }

    String getPreparationStatusString(){
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

    void setPreparationStatus(prepStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    String getOrderTime() {
        return orderTime;
    }

    String getUserName() {
        return userName;
    }

    String getUserPhone() {
        return userPhone;
    }

    String getUserEmail() {
        return userEmail;
    }

    String getUserAddress() {
        return userAddress;
    }

    boolean isAccepted() {
        return accepted;
    }

    void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    String getResNote() {
        return resNote;
    }

    String getUserLevel() {
        return userLevel;
    }

    int getToBePrepared() {
        return toBePrepared;
    }

    void incrementToBePrepared() {
        toBePrepared++;
    }

    void incrementDishDone() {
        toBePrepared--;
    }

    void setToBePrepared(int toBePrepared) {
        this.toBePrepared = toBePrepared;
    }
}
