package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    RecyclerView menu;
    private ArrayList<Card> cards;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();

    }

    private void init(){
        menu = findViewById(R.id.menu_display);
        llm = new LinearLayoutManager(this);
        menu.setLayoutManager(llm);

        cards = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","pizza","2", null));
        dishes.add(new Dish("Paperino","pizza","2", null));
        dishes.add(new Dish("Fottiti","pizza","2", null));
        dishes.add(new Dish("Margerita","pizza","2", null));


        for(int i =0; i < 101;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }


        RVAdapter adapter = new RVAdapter(cards);
        menu.setAdapter(adapter);

    }
}
