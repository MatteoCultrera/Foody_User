package com.example.foodyuser;

import java.util.ArrayList;
import java.util.Locale;

public class Restaurant {
    private String uid;
    private String username;
    private String address;
    private ArrayList<String> cuisines;
    private ArrayList<Integer> cuisineTypes;
    private int deliveryPrice;
    private ArrayList<String> daysTime;
    private float distance;
    private String imagePath;
    private boolean open;
    private String numberPhone;
    private String email;

    public Restaurant() {}

    public Restaurant(String name, ArrayList<String> cuisines, int deliveryPrice, float distance, String imagePath,
                      String numberPhone, String email){
        this.username = name;
        this.cuisines = cuisines;
        this.deliveryPrice = deliveryPrice;
        this.distance = distance;
        this.imagePath = imagePath;
        this.numberPhone = numberPhone;
        this.email = email;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<String> getDaysTime() {
        return daysTime;
    }

    public void setDaysTime(ArrayList<String> daysTime) {
        this.daysTime = daysTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(int deliveryPrice) {
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
        double delivery = (float) this.deliveryPrice * 0.5;
        return String.format(Locale.UK, "%.2f", delivery);
    }

    public String getDistanceString(){
        return String.format(Locale.UK, "%.2f Km", distance);
    }

    public ArrayList<Integer> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(ArrayList<Integer> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }
}
