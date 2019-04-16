package com.example.foodyrestaurant;

import android.net.Uri;
import android.util.JsonReader;
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

class JsonHandler {

    ArrayList<Card> getCards(File file){
        ArrayList<Card> cards;
        try {
            cards = readFromJSON(file);
        } catch (IOException e) {
            e.getMessage();
            return new ArrayList<>();
        }
        return cards;
    }

    ArrayList<Reservation> getReservations(File file){
        ArrayList<Reservation> reservations;
        try {
            reservations = readResFromFile(file);
        } catch (IOException e){
            e.getMessage();
            return new ArrayList<>();
        }
        return reservations;
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

    private ArrayList<Reservation> readResFromFile (File path) throws IOException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        FileInputStream fin = new FileInputStream(path);
        JsonReader reader = new JsonReader(new InputStreamReader(fin, StandardCharsets.UTF_8));
        try {
            reader.beginObject();
            if (reader.nextName().equals("Reservation"))
                reservations = readMultipleReservations(reader);
        } catch (IOException e) {
            e.getMessage();
        }finally {
            reader.close();
        }

        return reservations;
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

    private ArrayList<Reservation> readMultipleReservations(JsonReader reader) throws  IOException{
        ArrayList<Reservation> reservations = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()){
            reservations.add(readSingleReservation(reader));
        }
        reader.endArray();
        return reservations;
    }

    private Reservation readSingleReservation(JsonReader reader) throws IOException{
        String reservationID = null, userName = null, userPhone = null, userLevel = null, userEmail = null,
                userAddress = null, resNote = null, orderTime = null;
        ArrayList<Dish> dishesOrdered = new ArrayList<>();
        boolean accepted = false;
        Reservation.prepStatus preparationStatus = Reservation.prepStatus.PENDING;

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            switch (name) {
                case "reservationID":
                    reservationID = reader.nextString();
                    break;
                case "dishesOrdered":
                    dishesOrdered = readMultipleDishes(reader);
                    break;
                case "userName":
                    userName = reader.nextString();
                    break;
                case "userPhone":
                    userPhone = reader.nextString();
                    break;
                case "userLevel":
                    userLevel = reader.nextString();
                    break;
                case "userEmail":
                    userEmail = reader.nextString();
                    break;
                case "userAddress":
                    userAddress = reader.nextString();
                    break;
                case "resNote":
                    resNote = reader.nextString();
                    break;
                case "orderTime":
                    orderTime = reader.nextString();
                    break;
                case "accepted":
                    accepted = reader.nextBoolean();
                    break;
                case "preparationStatus":
                    String help = reader.nextString();
                    if (help.compareTo("pending") == 0)
                        preparationStatus = Reservation.prepStatus.PENDING;
                    else if (help.compareTo("doing") == 0)
                        preparationStatus = Reservation.prepStatus.DOING;
                    else if (help.compareTo("done") == 0)
                        preparationStatus = Reservation.prepStatus.DONE;
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Reservation(reservationID, dishesOrdered, preparationStatus, accepted, orderTime, userName,
                userPhone, resNote, userLevel, userEmail, userAddress);
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
        return new Dish(dishName, dishDescription, Float.valueOf(Objects.requireNonNull(price)), image);
    }

    String toJSON (ArrayList<Card> cards){
        JSONObject obj = new JSONObject();
        JSONArray objCardArray = new JSONArray();
        try {
            for (Card card1 : cards) {
                JSONObject objCard = new JSONObject();
                objCard.put("title", card1.getTitle());
                ArrayList<Dish> dishes = card1.getDishes();
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
            return "Error String";
        }
        return obj.toString();
    }

    String resToJSON (ArrayList<Reservation> reservations){
        JSONObject obj = new JSONObject();
        JSONArray objCardArray = new JSONArray();
        try {
            for (Reservation res1 : reservations) {
                JSONObject objRes = new JSONObject();
                objRes.put("reservationID", res1.getReservationID());
                objRes.put("userName", res1.getUserName());
                objRes.put("userPhone", res1.getUserPhone());
                objRes.put("userLevel", res1.getUserLevel());
                objRes.put("userEmail", res1.getUserEmail());
                objRes.put("userAddress", res1.getUserAddress());
                objRes.put("resNote", res1.getResNote());
                objRes.put("orderTime", res1.getOrderTime());
                objRes.put("accepted", res1.isAccepted());
                objRes.put("preparationStatus", res1.getPreparationStatusString());
                ArrayList<Dish> dishes = res1.getDishesOrdered();
                JSONArray objDishArray = new JSONArray();
                for (Dish dish : dishes) {
                    JSONObject objDish = new JSONObject();
                    objDish.put("dishName", dish.getDishName());
                    objDish.put("dishDescription", dish.getDishDescription());
                    objDish.put("price", dish.getPrice());
                    objDish.put("image", dish.getImage());
                    objDishArray.put(objDish);
                }
                objRes.put("Dish", objDishArray);
                objCardArray.put(objRes);
            }
            obj.put("Card", objCardArray);
        }
        catch (JSONException e){
            e.getMessage();
            return "Error String";
        }
        return obj.toString();
    }

    void saveStringToFile(String json, File file){
        FileOutputStream outputStream= null;
        try{
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e){
            e.getMessage();
        } finally {
            if (outputStream != null){
                try{
                    outputStream.close();
                } catch (IOException e){
                    e.getMessage();
                }
            }
        }
    }
}
