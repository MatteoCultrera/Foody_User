package com.example.foodyrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
    private File fileTmp, file;
    private RVAdapterEditItem recyclerAdapter;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        init();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (dialogDism != null){
            dialogDism.dismiss();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String dialogPrec = savedInstanceState.getString("dialog");
        cards = jsonHandler.getCards(fileTmp);
        unchanged = savedInstanceState.getBoolean("unchanged");
        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("back") == 0) {
                restoreBack();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("unchanged", unchanged);
        outState.putString("dialog", dialogCode);

        for (int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTitle().equals(className)){
                cards.get(i).setDishes(dishes);
            }
        }
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, fileTmp);
    }

    private void init(){
        fileTmp = new File(storageDir, JSON_COPY);
        file = new File(storageDir, JSON_PATH);
        jsonHandler = new JsonHandler();
        final RecyclerView recyclerMenu = findViewById(R.id.menu_items);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);
        if(fileTmp.exists())
            cards = jsonHandler.getCards(fileTmp);
        else
            cards = jsonHandler.getCards(file);
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
        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        for (int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTitle().equals(className)){
                cards.get(i).setDishes(dishes);
            }
        }
        String json = jsonHandler.toJSON(cards);
        Log.d("SWSW", "save"+json);
        File file = new File(storageDir, JSON_PATH);
        File fileTMP = new File(storageDir, JSON_COPY);
        jsonHandler.saveStringToFile(json, file);
        jsonHandler.saveStringToFile(json, fileTMP);
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

        ArrayList<Dish> dishes = new ArrayList<>();
        File plc = new File(storageDir, JSON_COPY);
        cards = jsonHandler.getCards(plc);

        for (int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTitle().equals(className)){
                dishes = cards.get(i).getDishes();
            }
        }
        String json = jsonHandler.toJSON(cards);
        Log.d("SWSW", "get"+json);
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

    private void restoreBack(){
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

    public void showPickImageDialog(){
        final Item[] items = {
                new Item(getString(R.string.alert_dialog_image_gallery), R.drawable.collections_black),
                new Item(getString(R.string.alert_dialog_image_camera), R.drawable.camera_black)
        };
        ListAdapter arrayAdapter = new ArrayAdapter<Item>(
                this,
                R.layout.alert_dialog_item,
                R.id.tv1,
                items){
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView iv = v.findViewById(R.id.iv1);
                iv.setImageDrawable(getDrawable(items[position].getIcon()));
                return v;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCode = "ok";
                dialog.dismiss();
            }
        });
        builder.setTitle(getResources().getString(R.string.alert_dialog_image_title));
        builder.setCancelable(false);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        pickFromGallery();
                        dialogCode = "ok";
                        break;
                    case 1:
                        pickFromCamera();
                        dialogCode = "ok";
                        break;
                }
            }
        });
        dialogCode = "pickImage";
        builder.show();
    }

    private  void pickFromGallery(){
        /*
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
        */

    }

    private void pickFromCamera(){
        /*
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager())!= null){

            File photoFile = createOrReplacePlaceholder();

            if(photoFile!=null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.foodyrestaurant",
                        photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
        */
    }
}
