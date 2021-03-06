package com.example.foodybiker;

public class Reservation {

    private String restaurantName, restaurantAddress,
            restaurantPickupTime, userName, userId,
            userAddress, userDeliveryTime,
            reservationID, restaurantID, notes;
    private boolean accepted;
    private String userPhone;
    private String restPhone;
    private Double distance;
    private String date;
    private String ISOdate;

    public Reservation(String restaurantName, String restaurantAddress, String restaurantPickupTime, String userName, String userAddress, String userDeliveryTime, String restaurantID, String notes, boolean accepted) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPickupTime = restaurantPickupTime;
        this.userName = userName;
        this.userAddress = userAddress;
        this.userDeliveryTime = userDeliveryTime;
        this.restaurantID = restaurantID;
        this.notes = notes;
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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

    public void setUserId(){
        this.userId = reservationID.substring(0,28);
    }

    public String getUserId(){
        return userId;
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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRestPhone() {
        return restPhone;
    }

    public void setRestPhone(String restPhone) {
        this.restPhone = restPhone;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getISOdate() {
        return ISOdate;
    }

    public void setISOdate(String ISOdate) {
        this.ISOdate = ISOdate;
    }
}
