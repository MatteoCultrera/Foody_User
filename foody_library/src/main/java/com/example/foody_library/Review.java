package com.example.foody_library;


public class Review {
    private String reviewID;
    private String userID;
    private String userName;
    private String imagePath;
    private String note;
    private float rating;
    private String restName;
    private String imagePathRest;

    public Review(){

    }

    public Review(String reviewID, String userID, String note, float rating){
        this.reviewID = reviewID;
        this.userID = userID;
        this.note = note;
        this.rating = rating;
        this.userName = null;
        this.imagePath = null;
    }

    public Review(String reviewID, String userID, String userName,String imagePath, String note, float rating){
        this.reviewID = reviewID;
        this.userID = userID;
        this.userName = userName;
        this.imagePath = imagePath;
        this.note = note;
        this.rating = rating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getRating() {
        return rating;
    }

    public String getRatingString(){
        return String.format("%.1f", rating);
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getImagePathRest() {
        return imagePathRest;
    }

    public void setImagePathRest(String imagePathRest) {
        this.imagePathRest = imagePathRest;
    }
}
