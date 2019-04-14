package com.example.foodyuser;

import android.support.annotation.NonNull;

class Item{
    private final String text;
    private final int icon;
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }

    public int getIcon() {
        return this.icon;
    }

    @NonNull
    public String toString(){
        return this.text;
    }
}