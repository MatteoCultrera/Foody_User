package com.example.foodyuser;

public class OrderItem {

    private int pieces;
    private String orderName;
    private float price;

    public OrderItem() {
    }

    public OrderItem(int pieces, String orderName, float price){
        this.price = price;
        this.orderName = orderName;
        this.pieces = pieces;
    }

    public String getPriceString(){
        return String.format("%.2f €", price);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public int getPieces() {
        return pieces;
    }

    public String getPiecesString(){
        return ""+pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public float getTotal(){
        return pieces*price;
    }

    public void plus(){
        pieces++;
    }

    public void minus(){
        pieces--;
        if(pieces<0)
            pieces = 0;
    }
}
