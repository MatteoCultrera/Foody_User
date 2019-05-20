package com.example.foodybiker;

public class ReservationDBBiker {

    private String reservationID;
    private String orderTime;
    private String orderTimeBiker;
    private String restaurantName;
    private String userName;
    private String restaurantAddress;
    private String userAddress;
    private String status;
    private String restaurantID;
    private Double latitude;
    private Double longitude;

    public ReservationDBBiker() {}

    public ReservationDBBiker(String reservationID, String orderTime, String orderTimeBiker, String restaurantName, String userName, String restaurantAddress, String userAddress, String restaurantID) {
        this.reservationID = reservationID;
        this.orderTime = orderTime;
        this.orderTimeBiker = orderTimeBiker;
        this.restaurantName = restaurantName;
        this.userName = userName;
        this.restaurantAddress = restaurantAddress;
        this.userAddress = userAddress;
        this.restaurantID = restaurantID;
        latitude = 0.0;
        longitude = 0.0;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
