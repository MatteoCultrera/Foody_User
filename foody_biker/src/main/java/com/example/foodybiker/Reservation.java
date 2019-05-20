package com.example.foodybiker;

public class Reservation {

    private String restaurantName, restaurantAddress,
            restaurantPickupTime, userName,
            userAddress, userDeliveryTime,
            reservationID, restaurantID, notes;

    public Reservation(String restaurantName, String restaurantAddress, String restaurantPickupTime, String userName, String userAddress, String userDeliveryTime, String restaurantID, String notes) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPickupTime = restaurantPickupTime;
        this.userName = userName;
        this.userAddress = userAddress;
        this.userDeliveryTime = userDeliveryTime;
        this.restaurantID = restaurantID;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
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

    public String getRestaurantPickupTime() {
        return restaurantPickupTime;
    }

    public void setRestaurantPickupTime(String restaurantPickupTime) {
        this.restaurantPickupTime = restaurantPickupTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserDeliveryTime() {
        return userDeliveryTime;
    }

    public void setUserDeliveryTime(String userDeliveryTime) {
        this.userDeliveryTime = userDeliveryTime;
    }
}
