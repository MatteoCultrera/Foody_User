package com.example.foodyrestaurant;

import java.util.ArrayList;

public class RestaurantInfo {
    private String username;
    private String email;
    private String address;
    private String numberPhone;
    private ArrayList<String> daysTime;
    private int deliveryPrice;
    private ArrayList<Integer> cuisines;
    private String imagePath;

    public RestaurantInfo(String username, String email, String address, String numberPhone,
                          ArrayList<String> daysTime, Integer deliveryCost, ArrayList<Integer> cuisineTypes) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.numberPhone = numberPhone;
        this.daysTime = daysTime;
        this.deliveryPrice = deliveryCost;
        this.cuisines = cuisineTypes;
    }

    public RestaurantInfo() {
    }

    public RestaurantInfo(String username, String email, ArrayList<String> daysTime, Integer deliveryCost) {
        this.username = username;
        this.email = email;
        this.daysTime = daysTime;
        this.deliveryPrice = deliveryCost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public ArrayList<String> getDaysTime() {
        return daysTime;
    }

    public void setDaysTime(ArrayList<String> daysTime) {
        this.daysTime = daysTime;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(int deliveryCost) {
        this.deliveryPrice = deliveryCost;
    }

    public ArrayList<Integer> getCuisineTypes() {
        return cuisines;
    }

    public void setCuisineTypes(ArrayList<Integer> cuisineTypes) {
        this.cuisines = cuisineTypes;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
