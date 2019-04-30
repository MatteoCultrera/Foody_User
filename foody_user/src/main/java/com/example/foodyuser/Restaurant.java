package com.example.foodyuser;

import java.util.ArrayList;

public class Restaurant {
    private String name;
    private ArrayList<String> cuisines;
    private float deliveryPrice;
    private float distance;

    public Restaurant() {}

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

    public String getKitchensString(){
        if(cuisines == null)
            return "";
        else {
            String returner="";
            for(int i = 0; i < cuisines.size(); i++){
                returner+=cuisines.get(i);
                if(i!= cuisines.size()-1)
                    returner+=", ";
            }
            return returner;
        }
    }

    public String getDeliveryPriceString(){
        String returner = String.format("%.2f", deliveryPrice);
        return returner;
    }

    public String getDistanceString(){
        String returner = String.format("%.2f Km", distance);
        return returner;
    }
}
