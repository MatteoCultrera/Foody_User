package com.example.foodyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.foodyrestaurant.R;

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
import java.util.Iterator;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private FloatingActionButton editMode;
    RecyclerView menu;
    private ArrayList<Card> cards, cards2;
    LinearLayoutManager llm;

    ImageView profileImage, profileShadow;

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
        menu = view.findViewById(R.id.menu_display);
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

        this.editMode = view.findViewById(R.id.edit_mode);
        this.profileImage = view.findViewById(R.id.mainImage);
        this.profileShadow = view.findViewById(R.id.shadow);

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);
        Glide
                .with(this)
                .load(R.drawable.pizza)
                .into(profileImage);



        cards = new ArrayList<>();

        storageDir = Objects.requireNonNull(getContext()).getFilesDir();
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
        dishes.add(new Dish("Margerita","Pomodoro, Mozzarella, Basilico","3,50 €", null));
        dishes.add(new Dish("Vegetariana","Verdure di Stagione, Pomodoro, Mozzarella","8,00 €", null));
        dishes.add(new Dish("Quattro Stagioni","Pomodoro, Mozzarella, Prosciutto, Carciofi, Funghi, Olive, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Quattro Formaggi","Mozzarella, Gorgonzola, Fontina, Stracchino","7,00 €", null));
        Card c = new Card("PIZZA");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("PRIMI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("SECONDI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("PRIMI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("SECONDI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("PRIMI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("SECONDI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("PRIMI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("SECONDI");
        c.setDishes(dishes);
        cards.add(c);

        for(int i =0; i < 3;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }*/
      
      //TODO _ is in the right place?
        editMode.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MenuEdit.class);
            startActivity(intent);
           }
        });

        json = toJSON();
        saveStringToFile(json, file);
        RVAdapter adapter = new RVAdapter(cards);
        menu.setAdapter(adapter);
    }

    public ArrayList<Card> readFromJSON (File path) throws IOException {
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

    public String toJSON (){
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
