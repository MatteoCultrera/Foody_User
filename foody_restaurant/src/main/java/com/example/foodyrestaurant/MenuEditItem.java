package com.example.foodyrestaurant;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MenuEditItem extends AppCompatActivity {


    private LinearLayoutManager llm;
    private TextView title;
    private String className;
    private final String JSON_COPY = "menuCopy.json";
    private File storageDir;
    private ArrayList<Dish> dishes;
    private RecyclerView recyclerMenu;
    private RVAdapterEditItem recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        init();

    }

    private void init(){
        recyclerMenu = findViewById(R.id.menu_items);
        llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);

        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        title = findViewById(R.id.textView);
        className = getIntent().getExtras().getString("MainName");

        title.setText("Edit "+className);
        dishes = getDishes();

        recyclerAdapter = new RVAdapterEditItem(dishes);
        recyclerMenu.setAdapter(recyclerAdapter);

    }


    private ArrayList<Dish> getDishes(){

        ArrayList<Card> cards;
        ArrayList<Dish> dishes = new ArrayList<>();
        JsonHandler placeholder = new JsonHandler();
        File plc = new File(storageDir, JSON_COPY);
        cards = placeholder.getCards(plc);
        Log.d("TITLECHECK", "Searching dishes in cards of size "+cards.size());

        for (int i = 0; i < cards.size(); i++){

            Log.d("TITLECHECK", "Card "+i+" with name "+cards.get(i).getTitle());
            if(cards.get(i).getTitle().equals(className)){
                dishes = cards.get(i).getDishes();
                Log.d("TITLECHECK", "Found Dishes oof size" + dishes.size());
            }

        }

        return dishes;
    }

    public void backToEditMenu(View view) {
        super.onBackPressed();
    }
}
