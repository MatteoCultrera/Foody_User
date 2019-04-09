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


        for(int i =0; i < 20;i++){
            ArrayList<Dish> dishes = new ArrayList<>();
            dishes.add(new Dish("Margerita","Pomodoro, Mozzarella, Basilico","3,50 €", null));
            dishes.add(new Dish("Vegetariana","Verdure di Stagione, Pomodoro, Mozzarella","8,00 €", null));
            dishes.add(new Dish("Quattro Stagioni","Pomodoro, Mozzarella, Prosciutto, Carciofi, Funghi, Olive, Grana a Scaglie","6,50 €", null));
            dishes.add(new Dish("Quattro Formaggi","Mozzarella, Gorgonzola, Fontina, Stracchino","7,00 €", null));
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
