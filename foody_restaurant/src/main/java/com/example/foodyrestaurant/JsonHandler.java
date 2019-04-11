package com.example.foodyrestaurant;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class JsonHandler {

    private String JSON_PATH;
    private File storageDir;

    public JsonHandler(String filename, File storageDir){
        JSON_PATH = filename;
        this.storageDir = storageDir;

    }

    public ArrayList<Card> getCards(){
        File file = new File(storageDir, JSON_PATH);
        ArrayList<Card> cards;
        try{
            cards = readFromJSON(file);
        }catch (IOException e){
            e.getMessage();
            return new ArrayList<Card>();
        }
        return cards;
    }

    private ArrayList<Card> readFromJSON (File path) throws IOException {
        ArrayList<Card> cards = new ArrayList<>();
        FileInputStream fin = new FileInputStream(path);

        JsonReader reader = new JsonReader(new InputStreamReader(fin, StandardCharsets.UTF_8));
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
            switch (name) {
                case "title":
                    title = reader.nextString();
                    break;
                case "Dish":
                    dishes = readMultipleDishes(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
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

    private String toJSON (ArrayList<Card> cards){
        JSONObject obj = new JSONObject();
        JSONArray objCardArray = new JSONArray();
        try {
            for (Card card1 : cards) {
                JSONObject objCard = new JSONObject();
                Card card = card1;
                objCard.put("title", card.getTitle());
                ArrayList<Dish> dishes = card.getDishes();
                JSONArray objDishArray = new JSONArray();
                for (Dish dish : dishes) {
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
            e.getMessage();
        }
        return obj.toString();
    }

    private void saveStringToFile(String json, File file){
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e){
            e.getMessage();
        }
    }

    public void save(ArrayList<Card> cards){
        String json = toJSON(cards);
        File file = new File(storageDir, JSON_PATH);
        saveStringToFile(json, file);
    }
}
