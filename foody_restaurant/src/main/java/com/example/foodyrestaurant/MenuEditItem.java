package com.example.foodyrestaurant;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;

public class MenuEditItem extends AppCompatActivity {


    private String className;
    private File storageDir;
    private ImageButton save;
    private ArrayList<Dish> dishes;
    private FloatingActionButton fabDishes;
    private RVAdapterEditItem recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        init();
    }

    private void init(){
        RecyclerView recyclerMenu = findViewById(R.id.menu_items);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);

        save = findViewById(R.id.saveButton);

        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        TextView title = findViewById(R.id.textView);
        className = getIntent().getExtras().getString("MainName");

        title.setText(getResources().getString(R.string.edit, className));
        dishes = getDishes();

        recyclerAdapter = new RVAdapterEditItem(dishes, this);
        recyclerMenu.setAdapter(recyclerAdapter);
        fabDishes = findViewById(R.id.fabDishes);

        fabDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }

    private void save(){
       for (int i = 0; i < dishes.size(); i++){
            Log.d("TITLECHECK",i+" "+dishes.get(i).toString());
       }

    }

    public void saveEnabled(boolean enabled){

        if(enabled == true)
            enabled = canEnable();

        save.setEnabled(enabled);
        if(enabled == true){
            save.setImageResource(R.drawable.save_white);
        }else{
            save.setImageResource(R.drawable.save_dis);
        }
    }

    public boolean canEnable(){
        ArrayList<String> dishNames = new ArrayList<>();

        for(int i = 0; i < dishes.size(); i++){
            if(dishes.get(i).getDishName().isEmpty())
                return false;
            dishNames.add(dishes.get(i).getDishName());

        }

        Set<String> dis = new HashSet<String>(dishNames);

        if(dis.size() < dishes.size())
            return false;


        return true;
    }

    public boolean getSaveEnabled(){
        return save.isEnabled();
    }

    public void insertItem(){
        dishes.add(new Dish("","",0.0f,null));
        recyclerAdapter.notifyDataSetChanged();
        saveEnabled(false);
    }

    public void removeItem(int position){
        dishes.remove(position);
        recyclerAdapter.notifyDataSetChanged();

    }

    private ArrayList<Dish> getDishes(){

        ArrayList<Card> cards;
        ArrayList<Dish> dishes = new ArrayList<>();
        JsonHandler placeholder = new JsonHandler();
        String JSON_COPY = "menuCopy.json";
        File plc = new File(storageDir, JSON_COPY);
        cards = placeholder.getCards(plc);

        for (int i = 0; i < cards.size(); i++){

            if(cards.get(i).getTitle().equals(className)){
                dishes = cards.get(i).getDishes();
            }

        }

        return dishes;
    }

    public void backToEditMenu(View view) {
        super.onBackPressed();
    }
}
