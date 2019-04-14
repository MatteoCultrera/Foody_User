package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private ArrayList<Card> cards;
    private final JsonHandler jsonHandler = new JsonHandler();
    private final String JSON_PATH = "menu.json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        File storageDir = getFilesDir();

        init();
    }

    private void init(){
        RecyclerView menu = findViewById(R.id.menu_display);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        menu.setLayoutManager(llm);
        /*File file = new File(storageDir, JSON_PATH);
        if (file.exists()) {
            try {
                cards = readFromJSON(file);
            } catch (IOException e) {
                e.getMessage();
            }
        }*/

        /*ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margherita","pizza","2", null));
        dishes.add(new Dish("Paperino","pizza","2", null));
        dishes.add(new Dish("Topolino","pizza","2", null));
        dishes.add(new Dish("Margherita","pizza","2", null));


        for(int i =0; i < 1;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }*/


        RVAdapter adapter = new RVAdapter(cards);
        menu.setAdapter(adapter);
        String json = jsonHandler.toJSON(cards);
    }
}
