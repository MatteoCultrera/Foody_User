package com.example.foodyrestaurant;

import java.util.ArrayList;

public class Restaurant {
    private String name;
    private ArrayList<String> kitchens;
    private float deliveryPrice;
    private float distance;

    public Restaurant(String name, ArrayList<String> kitchens, float deliveryPrice, float distance){
        this.name = name;
        this.kitchens = kitchens;
        this.deliveryPrice = deliveryPrice;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<String> getKitchens() {
        return kitchens;
    }

    public void setKitchens(ArrayList<String> kitchens) {
        this.kitchens = kitchens;
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
        if(kitchens == null)
            return "";
        else {
            String returner="";
            for(int i = 0; i < kitchens.size(); i++){
                returner+=kitchens.get(i);
                if(i!= kitchens.size()-1)
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
