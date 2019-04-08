package com.example.foodyrestaurant;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodyrestaurant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    RecyclerView menu;
    private ArrayList<Card> cards, cards2;
    LinearLayoutManager llm;
    private final String JSON_PATH = "menu.json";
    private File storageDir;
    private String json;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menu = (RecyclerView) view.findViewById(R.id.menu_display);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        llm = new LinearLayoutManager(view.getContext());
        menu.setLayoutManager(llm);
        storageDir = getContext().getFilesDir();
        File file = new File(storageDir, JSON_PATH);
        cards = new ArrayList<>();

        if (file.exists()) {
            try {
                cards = readFromJSON(file);
            } catch (IOException e) {
                e.getMessage();
            }
        }

        /*cards = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","pizza","2", null));
        dishes.add(new Dish("Paperino","pizza","2", null));
        dishes.add(new Dish("Fottiti","pizza","2", null));
        dishes.add(new Dish("Margerita","pizza","2", null));


        for(int i =0; i < 3;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }*/

        json = toJSON();
        saveStringToFile(json, file);
        RVAdapter adapter = new RVAdapter(cards);
        menu.setAdapter(adapter);
    }

    public ArrayList<Card> readFromJSON (File path) throws IOException {
        ArrayList<Card> cards = new ArrayList<>();
        FileInputStream fin = new FileInputStream(path);

        JsonReader reader = new JsonReader(new InputStreamReader(fin, "UTF-8"));
        try {
            reader.beginObject();
            if (reader.nextName().equals("Card"))
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
            if (name.equals("title")){
                title = reader.nextString();
            } else if (name.equals("Dish")){
                dishes = readMultipleDishes(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Log.d("title", title);
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
            if (name.equals("dishName")){
                dishName = reader.nextString();
            } else if (name.equals("dishDescription")){
                dishDescription = reader.nextString();
            } else if (name.equals("price")){
                price = reader.nextString();
            } else if (name.equals("image")) {
                image.parse(reader.nextString().replace('\\', Character.MIN_VALUE));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Dish(dishName, dishDescription, price, image);
    }

    public String toJSON (){
        JSONObject obj = new JSONObject();
        JSONArray objCardArray = new JSONArray();
        try {
            for (Iterator<Card> c = cards.iterator(); c.hasNext();) {
                JSONObject objCard = new JSONObject();
                Card card = c.next();
                objCard.put("title", card.getTitle());
                ArrayList<Dish> dishes = card.getDishes();
                JSONArray objDishArray = new JSONArray();
                for (Iterator<Dish> d = dishes.iterator(); d.hasNext();){
                    Dish dish = d.next();
                    JSONObject objDish = new JSONObject();
                    objDish.put("dishName", dish.getDishName());
                    objDish.put("dishDescription", dish.getDishDescription());
                    objDish.put("price", dish.getPrice());
                    objDish.put("image", dish.getImage());
                    objDishArray.put(objDish);
                }
                objCard.put("Dish", objDishArray);
                objCardArray.put(objCard);
            }
            obj.put("Card", objCardArray);
        }
        catch (JSONException e){
        }
        return obj.toString();
    }

    public void saveStringToFile(String json, File file){
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e){
            e.getMessage();
        }
    }
}
