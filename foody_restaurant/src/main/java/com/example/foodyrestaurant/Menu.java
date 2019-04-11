package com.example.foodyrestaurant;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;

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
        String json = toJSON();
    }

    public ArrayList<Card> readFromJSON (File path) throws IOException{
        ArrayList<Card> cards;
        FileInputStream fin = new FileInputStream(path);

        JsonReader reader = new JsonReader(new InputStreamReader(fin, StandardCharsets.UTF_8));
        try {
            cards = readMultipleCards(reader);
        } finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                e.getMessage();
            }
        }
        return cards;
    }

    private ArrayList<Card> readMultipleCards(JsonReader reader) throws IOException{
        ArrayList<Card> cards = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()){
            cards.add(readSingleCard(reader));
        }
        reader.endArray();
        return cards;
    }

    private Card readSingleCard(JsonReader reader) throws IOException{
        String title = null;
        ArrayList<Dish> dishes = new ArrayList<>();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            switch (name) {
                case "title":
                    title = reader.nextString();
                    break;
                case "dishes":
                    dishes = readMultipleDishes(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Card(title, dishes);
    }

    private ArrayList<Dish> readMultipleDishes(JsonReader reader) throws IOException {
        ArrayList<Dish> dishes = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()){
            dishes.add(readSingleDish(reader));
        }
        reader.endArray();
        return dishes;
    }

    private Dish readSingleDish(JsonReader reader) throws IOException {
        String dishName = null;
        String dishDescription = null;
        String price = null;
        Uri image = null;

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            switch (name) {
                case "dishName":
                    dishName = reader.nextString();
                    break;
                case "dishDescription":
                    dishDescription = reader.nextString();
                    break;
                case "price":
                    price = reader.nextString();
                    break;
                case "image":
                    Uri.parse(reader.nextString().replace('\\', Character.MIN_VALUE));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Dish(dishName, dishDescription, price, image);
    }

    public String toJSON (){
        JSONObject obj = new JSONObject();
        try {
            obj.put("Dishes", cards);
        }
        catch (JSONException e){
            e.getMessage();
        }
        return obj.toString();
    }
}
