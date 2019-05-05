package com.example.foodyuser;

public class UserInfo {
    private String username;
    private String email;
    private String address;
    private String numberPhone;
    private String biography;

    public UserInfo(){}

    public UserInfo(String username, String email, String address, String numberPhone, String biography){
        this.username = username;
        this.email = email;
        this.address = address;
        this.numberPhone = numberPhone;
        this.biography = biography;
    }

    public UserInfo(String username, String email){
        this.username = username;
        this.email = email;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
