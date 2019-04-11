package com.example.foodyrestaurant;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MenuEdit extends AppCompatActivity {

    private RecyclerView recyclerMenu;

    LinearLayoutManager llm;
    private FloatingActionButton mainFAB;
    private ArrayList<Card> cards;
    private JsonHandler jsonHandler;
    private JsonHandler jsonPlaceholder;
    private final String JSON_PATH = "menu.json";
    private final String JSON_COPY = "menuCopy.json";
    private File storageDir;
    private ImageButton back;
    private ImageButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        init();


    }

    private void init(){

        recyclerMenu = findViewById(R.id.menu_edit);

        llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);
        mainFAB = findViewById(R.id.mainFAB);
        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        jsonHandler = new JsonHandler(JSON_PATH, storageDir);
        cards = jsonHandler.getCards();
        save = findViewById(R.id.saveButton);
        back = findViewById(R.id.backButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAll();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        /*
        cards = new ArrayList<>();

        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","Pomodoro, Mozzarella, Basilico","3,50 €", null));
        dishes.add(new Dish("Vegetariana","Verdure di Stagione, Pomodoro, Mozzarella","8,00 €", null));
        dishes.add(new Dish("Quattro Stagioni","Pomodoro, Mozzarella, Prosciutto, Carciofi, Funghi, Olive, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Quattro Formaggi","Mozzarella, Gorgonzola, Fontina, Stracchino","7,00 €", null));
        Card c = new Card("Pizza");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("Primi");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("Secondi");
        c.setDishes(dishes);
        cards.add(c);
        */


        final RVAdapterEdit adapter = new RVAdapterEdit(cards);
        recyclerMenu.setAdapter(adapter);

        mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card c = new Card("PLACEHOLDER TRY");
                cards.add(c);
                adapter.notifyItemInserted(cards.size()-1);
            }
        });

    }

    private void saveAll(){
        jsonHandler.save(cards);
        finish();
    }

    private void back(){
        finish();
    }





}
