package com.example.foodyrestaurant;

import java.util.ArrayList;

public class BikerInfo {
    private String username;
    private String email;
    private String address;
    private String city;
    private String numberPhone;
    private ArrayList<String> daysTime;
    private String path;

    BikerInfo() {}

    BikerInfo(String username, String email, ArrayList<String> daysTime) {
        this.username = username;
        this.email = email;
        this.daysTime= daysTime;
    }

    public BikerInfo(String username, String email, String address, String city, String numberPhone, ArrayList<String> daysTime) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.city = city;
        this.numberPhone = numberPhone;
        this.daysTime = daysTime;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
