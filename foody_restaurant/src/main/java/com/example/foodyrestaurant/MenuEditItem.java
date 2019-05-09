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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class MenuEditItem extends AppCompatActivity {

    private String className;
    private JsonHandler jsonHandler;
    private File storageDir;
    private ImageButton save;
    private ArrayList<Dish> dishes;
    private ArrayList<Card> cards;
    private boolean unchanged = true;
    private AlertDialog dialogDism;
    private String dialogCode = "ok";
    private final String JSON_PATH = "menuCopy.json";
    private final String JSON_COPY = "menuCopyItem.json";
    private final String PLACEHOLDER_CAMERA = "dishPlaceholder.jpg";
    private File fileTmp;
    private int posToChange;
    private RVAdapterEditItem recyclerAdapter;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;
    private String placeholderPath;
    private File storageImageDir;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        init();

        for(int i = 0; i < cards.size(); i++)
            cards.get(i).print();

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
        placeholderPath = savedInstanceState.getString("placeholderPath");
        unchanged = savedInstanceState.getBoolean("unchanged");
        recyclerAdapter.setUnchanged(unchanged);
        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("back") == 0) {
                restoreBack();
            } else if (dialogPrec.compareTo("pickImage") == 0){
                showPickImageDialog(posToChange);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        unchanged = recyclerAdapter.getUnchanged() && unchanged;
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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        File file = new File(storageDir, JSON_PATH);
        jsonHandler = new JsonHandler();
        final RecyclerView recyclerMenu = findViewById(R.id.menu_items);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);
        cards = jsonHandler.getCards(file);
        save = findViewById(R.id.saveButton);

        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        TextView title = findViewById(R.id.textView);
        className = Objects.requireNonNull(getIntent().getExtras()).getString("MainName");
        title.setText(getResources().getString(R.string.edit, className));
        File temp = new File(storageDir,JSON_COPY);
        if(temp.exists()){
            dishes = getDishes(JSON_COPY);
        }
        else
            dishes = getDishes(JSON_PATH);
        recyclerAdapter = new RVAdapterEditItem(dishes, this);
        recyclerMenu.setAdapter(recyclerAdapter);
        FloatingActionButton fabDishes = findViewById(R.id.fabDishes);

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
        unchanged = recyclerAdapter.getUnchanged();
        if (unchanged){
            Toast.makeText(getApplicationContext(), R.string.noSave, Toast.LENGTH_SHORT).show();
        } else {
            final String JSON_REAL = "menu.json";
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File oldFile = new File(storageDir, JSON_PATH);
            ArrayList<Card> oldCards = jsonHandler.getCards(oldFile);
            ArrayList<Dish> oldDishes = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getTitle().equals(className)) {
                    oldDishes = oldCards.get(i).getDishes();
                }
            }
            boolean stillExists;
            for (int i = 0; i < oldDishes.size(); i++) {
                stillExists = false;
                for (int j = 0; j < dishes.size(); j++) {
                    if (oldDishes.get(i).getImage() == null || dishes.get(j).getImage() == null)
                        continue;
                    if (oldDishes.get(i).getImage().toString().equals(dishes.get(j).getImage().toString())) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists && oldDishes.get(i).getImage() != null) {
                    File toDelete = new File(oldDishes.get(i).getImage().getPath());
                    if (toDelete.exists()) {
                        if (!toDelete.delete()) {
                            System.out.println("Cannot delete the file.");
                        }
                    }
                }
            }
            DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                    .child("restaurantsMenu").child(user.getUid()).child("Card");
            HashMap<String, Object> child = new HashMap<>();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getDishes().size() != 0)
                    child.put(Integer.toString(i), cards.get(i));
                for(Dish d : cards.get(i).getDishes()){
                    if (d.getImage() != null) {

                    }
                }
            }
            database.updateChildren(child);
            String json = jsonHandler.toJSON(cards);
            File file = new File(storageDir, JSON_REAL);
            File fileCopy = new File(storageDir, JSON_PATH);
            jsonHandler.saveStringToFile(json, file);
            jsonHandler.saveStringToFile(json, fileCopy);
            unchanged = true;
            recyclerAdapter.setUnchanged(true);
            Toast.makeText(getApplicationContext(), R.string.save, Toast.LENGTH_SHORT).show();
        }
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

    private boolean canEnable(){
        ArrayList<String> dishNames = new ArrayList<>();

        for(int i = 0; i < dishes.size(); i++){
            if(dishes.get(i).getDishName().isEmpty())
                return false;
            dishNames.add(dishes.get(i).getDishName());

        }
        Set<String> dis = new HashSet<>(dishNames);
        return dis.size() >= dishes.size();
    }

    private void insertItem(int position){
        unchanged = false;
        dishes.add(new Dish("","",0.0f,null));
        recyclerAdapter.notifyItemInserted(position);
        saveEnabled(false);
    }

    public void removeItem(int position){
        unchanged = false;
        if(dishes.get(position).isEditImage()){
            File image = new File(dishes.get(position).getImage().getPath());
            if(image.exists()){
                if(!image.delete()){
                    System.out.println("Cannot delete the file.");
                }
            }

            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + "/"
                    + dishes.get(position).getPathDB() + ".jpeg");
            ref.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("SWSW", "image deleted");
                        }
                    });
        }
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
        return dishes;
    }

    public void backToEditMenu(View view) {
        if (unchanged && recyclerAdapter.getUnchanged()){
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
        if (unchanged && recyclerAdapter.getUnchanged()){
            File f = new File(storageDir, JSON_COPY);
            if(f.exists()){
                if(!f.delete()){
                    System.out.println("Cannot delete the file.");
                }
            }
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
                                if(!f.delete()){
                                    System.out.println("Cannot delete the file.");
                                }
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
        dialogDism = builder.show();
    }

    private void pickFromGallery(){

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
                        FirebaseStorage storage;
                        StorageReference storageReference;
                        storage = FirebaseStorage.getInstance();
                        storageReference = storage.getReference();
                        StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + "/"
                                + imageFileName + ".jpeg");
                        ref.putFile(imageURi)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("SWSW", "success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        if(dishes.get(posToChange).isEditImage()){
                            File toDelete = new File(dishes.get(posToChange).getImage().getPath());
                            if(toDelete.exists())
                                if(!toDelete.delete()){
                                    System.out.println("Cannot delete the file.");
                                }
                        }else
                            dishes.get(posToChange).setEditImage(true);
                        dishes.get(posToChange).setImage(imageURi);
                        dishes.get(posToChange).setPathDB(imageFileName);
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
        options.setCircleDimmedLayer(true);
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
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
            imageFileName = "JPEG_" + timeStamp + "_";
            File fileToSave = new File(storageImageDir, imageFileName);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(fileToSave.getPath());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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
