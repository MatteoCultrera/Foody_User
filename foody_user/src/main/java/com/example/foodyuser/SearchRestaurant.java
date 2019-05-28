package com.example.foodyuser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SearchRestaurant extends AppCompatActivity {

    EditText search;
    RecyclerView scrollView;
    File storage;
    boolean visible;
    InputMethodManager imm;
    private final String RESTAURANT_IMAGES = "RestaurantImages";
    private ArrayList<Restaurant> restaurants;
    private ArrayList<Restaurant> restName;
    private ArrayList<Restaurant> restCuisine;
    private boolean[] checkedFoods = new boolean[27];
    private ArrayList<Integer> indexFoods;
    private ArrayList<Integer> copyIndexFoods;
    private ArrayList<String> selectedFoods;
    private SharedPreferences sharedPrefer;
    private TextView filterButton;
    private String[] foodCategories;
    private int imageToFetch, imageFetched;
    private SpinKitView loading;
    private ConstraintLayout parent;
    private RVAdapterRestaurants adapter;
    private boolean clearFilter = false;
    private boolean firstTime = true;
    private AlertDialog foodChooseType;
    private int width;
    private int height;
    private boolean allImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);
        imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        final View v = new View(this);
        foodCategories = getResources().getStringArray(R.array.foodcategory_array);
        scrollView = findViewById(R.id.list_restaurants);
        loading = findViewById(R.id.loading_restaurants);
        filterButton = findViewById(R.id.filter_button);
        search = findViewById(R.id.search_restaurant);
        parent = findViewById(R.id.parent_restaurants);
        sharedPrefer = getSharedPreferences("myPreference", MODE_PRIVATE);

        allImages = sharedPrefer.getBoolean("restaurantFetches",false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

    }

    private void init(){
        visible = true;
        search.clearFocus();
        search.setY(getResources().getDimensionPixelSize(R.dimen.short30));
        Log.d("MADMAX", parent.getHeight()+"");
        filterButton.setY(height - getResources().getDimensionPixelSize(R.dimen.short100));
        Log.d("MADMAX", filterButton.getY()+"");
        scrollView.clearOnScrollListeners();
        scrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Animation searchAnim = search.getAnimation();

                if(searchAnim != null && searchAnim.hasStarted() && !searchAnim.hasEnded()){

                }
                else{
                    if(dy > 0 && visible){
                        search.animate().translationY(-getResources().getDimensionPixelSize(R.dimen.short60)).setDuration(200).start();
                        search.clearFocus();
                        hideKeyboard();
                        filterButton.animate().translationY(height + getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                        visible = false;
                    }

                    if(dy < 0 && !visible){
                        search.animate().translationY(getResources().getDimensionPixelSize(R.dimen.short30)).setDuration(200).start();
                        filterButton.animate().translationY(height - getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                        visible = true;
                    }

                }
            }


        });

        restaurants = new ArrayList<>();
        restName = new ArrayList<>();
        restCuisine = new ArrayList<>();
        selectedFoods = new ArrayList<>();
        indexFoods = new ArrayList<>();
        copyIndexFoods = new ArrayList<>();
        foodCategories = getResources().getStringArray(R.array.foodcategory_array);

        filterButton.setOnClickListener(null);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });



        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+RESTAURANT_IMAGES);

        loading.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        scrollView.setLayoutManager(llm);

        if(storage.exists()){
            //Images Already created
            fetchRestaurants(true);

        }else{
            //Fetch Images from DB
            storage.mkdirs();


            imageToFetch = 0;
            imageFetched = 0;
            fetchRestaurants(false);

        }


    }

    private void fetchRestaurants(final Boolean hasImages){

        Log.d("MAD", "FetchRestaurants()");

        Calendar calendar = Calendar.getInstance(Locale.ITALY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (dayOfWeek == -1)
            dayOfWeek = 6;
        final int day = dayOfWeek;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALY);
        final String time = sdf.format(calendar.getTime());

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("restaurantsInfo");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Restaurant restaurant = ds1.getValue(Restaurant.class);
                        restaurant.setUid(ds.getKey());
                        Log.d("VANGOGH","ds "+ds.getKey());
                        if (restaurant.getDaysTime() != null) {
                            String intervalTime = restaurant.getDaysTime().get(day).replace(" ", "");
                            if (!intervalTime.startsWith("C")) {
                                String[] splits = intervalTime.split("-");
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.ITALY);
                                try{
                                    Date date = sdf2.parse(splits[1]);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    cal.add(Calendar.MINUTE, -30);
                                    String newTime = sdf2.format(cal.getTime());
                                    if (splits[0].compareTo(time) <= 0 && newTime.compareTo(time) >= 0) {
                                        restaurant.setOpen(true);
                                    }else{
                                        restaurant.setOpen(false);
                                    }
                                } catch(ParseException e){
                                    e.printStackTrace();
                                }
                            }
                            else{
                                restaurant.setOpen(false);
                            }
                        }
                        if (restaurant.getCuisineTypes() != null) {
                            ArrayList<String> types = new ArrayList<>();
                            for (Integer i : restaurant.getCuisineTypes()) {
                                types.add(foodCategories[i]);
                            }
                            restaurant.setCuisines(types);
                        }

                        if(ds1.child("address").exists()) {
                            //Double distance = calculateDistance(delivAddress, restaurant.getAddress());
                            //restaurant.setDistance(distance.floatValue());
                        }

                        if(restaurant.getImagePath()!=null){
                            imageToFetch++;
                        }

                        restaurants.add(restaurant);
                        restName.add(restaurant);
                        restCuisine.add(restaurant);
                    }
                }

                restaurants.sort(new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant r1, Restaurant r2) {
                        if (r1.isOpen() && !r2.isOpen())
                            return -1;
                        if (!r1.isOpen() && r2.isOpen())
                            return 1;
                        return 0;
                    }
                });

                if(hasImages && allImages){
                    //Ready to go
                    for(Restaurant r : restaurants){
                        if(r.getImagePath() != null)
                            r.setImagePath(storage.getPath()+File.separator+r.getUid()+".jpg");
                    }

                    loading.setVisibility(View.GONE);
                    adapter = new RVAdapterRestaurants(restaurants);
                    scrollView.setAdapter(adapter);
                    scrollView.setVisibility(View.VISIBLE);
                    filterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPickFood(filterButton);
                        }
                    });
                }else{
                    //Fetch All Images
                    Log.d("MAD", "Fetching Images "+imageToFetch+" "+imageFetched);

                    for(Restaurant r : restaurants){
                        if(r.getImagePath()!=null)
                            fetchImages(r);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });


    }

    private void fetchImages(final Restaurant restaurant){

        Log.d("MAD", "Entering fetch Images with restaurant "+restaurant.getUsername());
        Log.d("MAD", "Path "+restaurant.getImagePath());

        final File file = new File(storage.getPath()+File.separator+restaurant.getUid()+".jpg");

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child(restaurant.getImagePath()).getFile(file)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("MAD", "Restaurant "+restaurant.getUsername()+" saved on "+file.getPath());
                restaurant.setImagePath(file.getPath());
                imageFetched++;
                if(imageFetched == imageToFetch){
                    loading.setVisibility(View.GONE);
                    sharedPrefer.edit().putBoolean("restaurantFetches", true).apply();
                    //Create RecyclerView
                    adapter = new RVAdapterRestaurants(restaurants);
                    scrollView.setAdapter(adapter);
                    scrollView.setVisibility(View.VISIBLE);
                    filterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPickFood(filterButton);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                restaurant.setImagePath(null);
                Log.d("MAD", "Restaurant "+restaurant.getUsername()+" not saved on "+file.getPath());
                imageFetched++;
                if(imageFetched == imageToFetch){

                    allImages = true;
                    sharedPrefer.edit().putBoolean("restaurantFetches", true).apply();
                    loading.setVisibility(View.GONE);
                    //Create RecyclerView
                    adapter = new RVAdapterRestaurants(restaurants);

                    scrollView.setAdapter(adapter);
                    scrollView.setVisibility(View.VISIBLE);
                    filterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPickFood(filterButton);
                        }
                    });
                }
            }
        });

    }

    public void hideKeyboard(){
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void filter(String text) {
        ArrayList<Restaurant> filteredNames = new ArrayList<>();
        restName = new ArrayList<>();

        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getUsername().toLowerCase().contains(text.toLowerCase()))
                restName.add(restaurants.get(i));
        }
        for (int j = 0; j < restName.size(); j++) {
            if (restCuisine.contains(restName.get(j))) {
                filteredNames.add(restName.get(j));
            }
        }

        if(filteredNames.size() < 5)
            scrollView.clearOnScrollListeners();
        else
            scrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Animation searchAnim = search.getAnimation();

                    if(searchAnim != null && searchAnim.hasStarted() && !searchAnim.hasEnded()){

                    }
                    else{
                        if(dy > 0 && visible){
                            search.animate().translationY(-getResources().getDimensionPixelSize(R.dimen.short60)).setDuration(200).start();
                            search.clearFocus();
                            hideKeyboard();
                            filterButton.animate().translationY(height + getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                            visible = false;
                        }

                        if(dy < 0 && !visible){
                            search.animate().translationY(getResources().getDimensionPixelSize(R.dimen.short30)).setDuration(200).start();
                            filterButton.animate().translationY(height - getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                            visible = true;
                        }

                    }
                }


            });


        adapter.filterList(filteredNames);
    }

    private void filterCuisine(ArrayList<String> text) {
        ArrayList<Restaurant> filteredNames = new ArrayList<>();
        restCuisine = new ArrayList<>();

        if(text.size() == 0) {
            restCuisine = restaurants;
        }

        else {
            for (int i = 0; i < restaurants.size(); i++) {
                ArrayList<String> cuisines = restaurants.get(i).getCuisines();
                if(cuisines != null) {
                    for (String c : cuisines) {
                        for (String s : text) {
                            if (c.toLowerCase().contains(s.toLowerCase())) {
                                if (!restCuisine.contains(restaurants.get(i))) {
                                    restCuisine.add(restaurants.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }
        for(int j = 0; j < restCuisine.size(); j++){
            if(restName.contains(restCuisine.get(j))){
                filteredNames.add(restCuisine.get(j));
            }
        }

        adapter.filterList(filteredNames);
    }

    private void populateCheckedFoods() {
        for(int i = 0; i < 27; i++)
            checkedFoods[i] = false;

        int index = indexFoods.size();

        for(int i = 0; i < index; i++) {
            checkedFoods[indexFoods.get(i)] = true;

        }
    }

    public void showPickFood(View view) {
        populateCheckedFoods();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.AppCompatAlertDialogStyle);

        builder.setMultiChoiceItems(foodCategories, checkedFoods, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(firstTime) {
                    copyIndexFoods.clear();
                    copyIndexFoods.addAll(indexFoods);
                }

                firstTime = false;
                if (isChecked) {
                    selectedFoods.add(String.valueOf(foodCategories[which]));
                    indexFoods.add(which);
                    checkedFoods[which] = true;
                } else {
                    selectedFoods.remove(String.valueOf(foodCategories[which]));
                    if(indexFoods.contains(which))
                        indexFoods.remove(Integer.valueOf(which));
                    checkedFoods[which] = false;
                }
            }
        });

        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(clearFilter) {
                    selectedFoods.clear();
                    clearFilter = false;
                }

                firstTime = true;
                filterCuisine(selectedFoods);
                copyIndexFoods.clear();
                copyIndexFoods.addAll(indexFoods);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearFilter = false;
                firstTime = true;

                indexFoods.clear();
                indexFoods.addAll(copyIndexFoods);

                selectedFoods.clear();
                for (int i = 0; i < indexFoods.size(); i++)
                    selectedFoods.add(String.valueOf(foodCategories[indexFoods.get(i)]));

                dialog.dismiss();
            }
        });

        builder.setNeutralButton(R.string.delete_filter, null);

        builder.setTitle(R.string.dialog_cuisine);

        foodChooseType = builder.create();
        foodChooseType.show();

        foodChooseType.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter = true;
                Arrays.fill(checkedFoods, Boolean.FALSE);
                for(int i = 0; i < indexFoods.size(); i++)
                    foodChooseType.getListView().setItemChecked(indexFoods.get(i), false);
                indexFoods.clear();
                copyIndexFoods.clear();
            }
        });
    }
}
