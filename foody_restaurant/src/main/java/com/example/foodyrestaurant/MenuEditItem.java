package com.example.foodyrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final String JSON_REAL = "menu.json";
    private final String JSON_PATH = "menuCopy.json";
    private final String JSON_COPY = "menuCopyItem.json";
    private final String PLACEHOLDER_CAMERA = "dishPlaceholder.jpg";
    private File fileTmp, file;
    private int posToChange;
    private RVAdapterEditItem recyclerAdapter;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;
    private String placeholderPath;
    private File storageImageDir;

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

        fileTmp = new File(storageDir, JSON_COPY);
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, fileTmp);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String dialogPrec = savedInstanceState.getString("dialog");
        posToChange = savedInstanceState.getInt("posToChange", 0);
        fileTmp = new File(storageDir, JSON_COPY);
        cards = jsonHandler.getCards(fileTmp);
        placeholderPath = savedInstanceState.getString("placeholderPath");
        dishes = getDishes(JSON_COPY);
        unchanged = savedInstanceState.getBoolean("unchanged");
        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("back") == 0) {
                restoreBack();
            }
        }
        for(int i = 0; i < dishes.size();i ++){
            Log.d("TITLECHECK",dishes.get(i).toString());
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("unchanged", unchanged);
        outState.putString("dialog", dialogCode);
        outState.putInt("posToChange", posToChange);
        outState.putString("placeholderPath", placeholderPath);

        for (int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTitle().equals(className)){
                cards.get(i).setDishes(dishes);
            }
        }
        fileTmp = new File(storageDir, JSON_COPY);
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, fileTmp);
    }

    private void init(){
        storageImageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        file = new File(storageDir, JSON_PATH);
        jsonHandler = new JsonHandler();
        final RecyclerView recyclerMenu = findViewById(R.id.menu_items);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);
        cards = jsonHandler.getCards(file);
        save = findViewById(R.id.saveButton);

        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        TextView title = findViewById(R.id.textView);
        className = getIntent().getExtras().getString("MainName");
        title.setText(getResources().getString(R.string.edit, className));
        File temp = new File(storageDir,JSON_COPY);
        if(temp.exists())
            dishes = getDishes(JSON_COPY);
        else
            dishes = getDishes(JSON_PATH);
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
        File oldFile = new File(storageDir,JSON_PATH);
        ArrayList<Card> oldCards = jsonHandler.getCards(oldFile);
        ArrayList<Dish> oldDishes = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTitle().equals(className)){
                oldDishes = oldCards.get(i).getDishes();
            }
        }


        boolean stillExists;
        for(int i = 0; i < oldDishes.size(); i++){
            stillExists = false;
            for(int j = 0; j < dishes.size(); j++) {
                if(oldDishes.get(i).getImage() == dishes.get(j).getImage()){
                    stillExists = true;
                }
            }
            if(stillExists == false && oldDishes.get(i).getImage() != null){
                File toDelete = new File(oldDishes.get(i).getImage().getPath());
                if(toDelete.exists())
                    toDelete.delete();
            }
        }


        String json = jsonHandler.toJSON(cards);
        File file = new File(storageDir, JSON_REAL);
        File fileTMP = new File(storageDir, JSON_PATH);
        File fileItem = new File(storageDir, JSON_COPY);
        jsonHandler.saveStringToFile(json, file);
        if(fileTMP.exists())
            fileTMP.delete();
        if(fileItem.exists())
            fileItem.exists();
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

    private ArrayList<Dish> getDishes(String fileName){

        ArrayList<Dish> dishes = new ArrayList<>();
        File plc = new File(storageDir, fileName);
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
                    for(int j = 0; j< dishes.size(); j++){
                        if(dishes.get(j).isEditImage()){
                            File f = new File(dishes.get(j).getImage().getPath());
                            if(f.exists())
                                f.delete();
                        }
                    }
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

    public void showPickImageDialog(int i){
        posToChange = i;
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

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);


    }

    private void pickFromCamera(){

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

    }

    private File createOrReplacePlaceholder(){

        File f = new File(storageImageDir, PLACEHOLDER_CAMERA);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(storageImageDir, PLACEHOLDER_CAMERA);

        placeholderPath = f.getPath();

        return f;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){

                case REQUEST_CAPTURE_IMAGE:
                    File f = new File(placeholderPath);
                    startCrop(Uri.fromFile(f));
                    break;

                case GALLERY_REQUEST_CODE:
                    if(data !=null){
                        Uri imageUri = data.getData();

                        if(imageUri != null)
                            startCrop(imageUri);
                    }
                    break;

                case  UCrop.REQUEST_CROP:
                    Bitmap bitmap = getBitmapFromFile();

                    if(bitmap != null){
                        File placeholder = new File(storageDir, PLACEHOLDER_CAMERA);
                        Uri imageURi = saveBitmap(bitmap, placeholder.getPath());
                        if(dishes.get(posToChange).isEditImage()){
                            File toDelete = new File(dishes.get(posToChange).getImage().getPath());
                            if(toDelete.exists())
                                toDelete.delete();
                        }else
                            dishes.get(posToChange).setEditImage(true);
                        dishes.get(posToChange).setImage(imageURi);
                        recyclerAdapter.notifyItemChanged(posToChange);
                        unchanged = false;
                    }
                    break;
            }
        }
    }

    private void startCrop(@NonNull Uri uri){
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(storageDir, PLACEHOLDER_CAMERA)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(960,960);
        uCrop.withOptions(getCropOptions());
        uCrop.start(MenuEditItem.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options= new UCrop.Options();

        options.setCompressionQuality(100);
        options.setHideBottomControls(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
        options.setToolbarTitle(getResources().getString(R.string.crop_image));
        return options;
    }

    private Bitmap getBitmapFromFile(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File dest = new File(storageDir, PLACEHOLDER_CAMERA);
        if(!dest.exists())
            return null;
        return  BitmapFactory.decodeFile(dest.getPath(), options);

    }

    private Uri saveBitmap(Bitmap bitmap,String path){
        if(bitmap!=null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File fileToSave = new File(storageImageDir, imageFileName);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(fileToSave.getPath()); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Uri.fromFile(fileToSave);
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.notifyDataSetChanged();
    }
}
