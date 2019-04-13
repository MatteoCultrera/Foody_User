package com.example.foodyrestaurant;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    RecyclerView menu;
    private ArrayList<Card> cards;
    LinearLayoutManager llm;
    JsonHandler jsonHandler = new JsonHandler();
    private final String JSON_PATH = "menu.json";
    private File storageDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        storageDir = getFilesDir();

        init();
    }

    private void init(){
        menu = findViewById(R.id.menu_display);
        llm = new LinearLayoutManager(this);
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
