package com.example.foodyrestaurant;

import java.util.ArrayList;

public class Restaurant {
    private String name;
    private ArrayList<String> cuisines;
    private float deliveryPrice;
    private float distance;

    public Restaurant(){}

    public Restaurant(String name, ArrayList<String> cuisines, float deliveryPrice, float distance){
        this.name = name;
        this.cuisines = cuisines;
        this.deliveryPrice = deliveryPrice;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
    }

    public float getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(float deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}
