package com.example.foodyrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MenuEditItem extends AppCompatActivity {


    private String className;
    private JsonHandler jsonHandler;
    private File storageDir;
    private ImageButton save;
    private ArrayList<Dish> dishes;
    private ArrayList<Card> cards;
    private FloatingActionButton fabDishes;
    private boolean unchanged = true;
    private AlertDialog dialogDism;
    private String dialogCode = "ok";
    private final String JSON_PATH = "menu.json";
    private final String JSON_COPY = "menuCopy.json";
    private File fileTmp;
    private RVAdapterEditItem recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        init();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cards = jsonHandler.getCards(fileTmp);
        String writingCard = savedInstanceState.getString("writing", "");
        String dialogPrec = savedInstanceState.getString("dialog");
        unchanged = savedInstanceState.getBoolean("unchanged");
        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("back") == 0) {
                onBackPressed();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, fileTmp);
        outState.putBoolean("unchanged", unchanged);
        outState.putString("dialog", dialogCode);
    }

    private void init(){
        fileTmp = new File(storageDir, JSON_COPY);
        jsonHandler = new JsonHandler();
        final RecyclerView recyclerMenu = findViewById(R.id.menu_items);
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
                insertItem(dishes.size());
                recyclerMenu.smoothScrollToPosition(dishes.size());
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
        File plc = new File(storageDir, JSON_PATH);
        String toJson = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(toJson, plc);
        finish();
    }

    public void saveEnabled(boolean enabled){

        if(enabled)
            enabled = canEnable();

        save.setEnabled(enabled);
        if(enabled){
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

    public void insertItem(int position){
        dishes.add(new Dish("","",0.0f,null));
        recyclerAdapter.notifyItemInserted(position);
        saveEnabled(false);
    }

    public void removeItem(int position){
        dishes.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
        recyclerAdapter.notifyItemRangeChanged(position, dishes.size());
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
        unchanged = recyclerAdapter.getUnchanged();
        if (unchanged){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    MenuEditItem.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        unchanged = recyclerAdapter.getUnchanged();
        if (unchanged){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    MenuEditItem.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }
}
