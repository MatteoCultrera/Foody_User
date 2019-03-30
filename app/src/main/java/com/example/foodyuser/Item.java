package com.example.foodyuser;

public class Item{
    private final String text;
    private final int icon;
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return text;
    }
}