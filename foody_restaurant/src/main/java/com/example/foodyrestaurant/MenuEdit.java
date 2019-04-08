package com.example.foodyrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MenuEdit extends AppCompatActivity {

    private RecyclerView recyclerMenu;

    LinearLayoutManager llm;
    private ArrayList<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        init();

        loadCards();



    }

    private void init(){

        recyclerMenu = findViewById(R.id.menu_edit);

        llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);

        cards = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","pizza","2", null));
        dishes.add(new Dish("Paperino","pizza","2", null));
        dishes.add(new Dish("Fottiti","pizza","2", null));
        dishes.add(new Dish("Margerita","pizza","2", null));


        for(int i =0; i < 20;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }

        RVAdapterEdit adapter = new RVAdapterEdit(cards);
        recyclerMenu.setAdapter(adapter);


    }

    private void loadCards(){

    }


}
