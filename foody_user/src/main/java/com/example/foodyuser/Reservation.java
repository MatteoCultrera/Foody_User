package com.example.foodyuser;

import java.util.ArrayList;

class Reservation {

    enum prepStatus {
        PENDING,
        DOING,
        DONE,
    }

    private String reservationID;
    private String restaurantID;
    private String restaurantName;
    private String restaurantAddress;
    private String deliveryTime;
    private ArrayList<Dish> dishesOrdered;
    private boolean accepted;
    private prepStatus preparationStatus;
    private String userName;
    private String userPhone;
    private String userLevel;
    private String userEmail;
    private String userAddress;
    private String resNote;
    private String orderTime;
    private String userUID;
    private int toBePrepared;
    private String totalCost;

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

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    String getReservationID() {
        return reservationID;
    }

    void setDishesOrdered(ArrayList<Dish> dishesOrdered){
        this.dishesOrdered = dishesOrdered;
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
                ret = "Doing";
                break;
            default:
                ret = "Status Unknown";
                break;
        }
        return ret;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    void setPreparationStatus(prepStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
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
