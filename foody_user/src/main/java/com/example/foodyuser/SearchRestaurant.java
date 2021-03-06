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
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ArrayList<String> favouriteRest;
    private boolean[] checkedFoods = new boolean[27];
    private ArrayList<Integer> indexFoods;
    private ArrayList<Integer> copyIndexFoods;
    private ArrayList<String> selectedFoods;
    private SharedPreferences sharedPrefer;
    private TextView favouriteButton, filterButton;
    private ConstraintLayout mainConstraint;

    private String[] foodCategories;
    private int imageToFetch, imageFetched;
    private SpinKitView loading;
    private ConstraintLayout parent;
    private RVAdapterRestaurants adapter;
    private boolean clearFilter = false;
    private boolean firstTime = true;
    private AlertDialog foodChooseType;
    private int height;
    private boolean allImages;
    private boolean favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);
        imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        final View v = new View(this);
        foodCategories = getResources().getStringArray(R.array.foodcategory_array);
        scrollView = findViewById(R.id.list_restaurants);
        loading = findViewById(R.id.loading_restaurants);
        mainConstraint = findViewById(R.id.main_constraint);
        filterButton = findViewById(R.id.filter_button_text);
        favouriteButton = findViewById(R.id.favs_button_text);
        search = findViewById(R.id.search_restaurant);
        parent = findViewById(R.id.parent_restaurants);
        sharedPrefer = getSharedPreferences("myPreference", MODE_PRIVATE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
    }

    private void init(){
        allImages = sharedPrefer.getBoolean("restaurantFetches",false);
        Log.d("MADPROVA","All images "+allImages);
        visible = true;
        favourite = false;
        search.clearFocus();
        search.setY(getResources().getDimensionPixelSize(R.dimen.short30));
        mainConstraint.setY(height - getResources().getDimensionPixelSize(R.dimen.short100));
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
                        mainConstraint.animate().translationY(height + getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                        visible = false;
                    }

                    if(dy < 0 && !visible){
                        search.animate().translationY(getResources().getDimensionPixelSize(R.dimen.short30)).setDuration(200).start();
                        mainConstraint.animate().translationY(height - getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
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
        switchPrefs(false);
        switchFilters(false);

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


    private void switchPrefs(boolean enabled){
        if(enabled){
            favouriteButton.setTextColor(getColor(R.color.whiteText));
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill, 0, 0, 0);
            favouriteButton.setCompoundDrawableTintList(ContextCompat.getColorStateList(this, R.color.whiteText));
            favouriteButton.setBackgroundResource(R.drawable.favs_background_en);
        }else{
            favouriteButton.setTextColor(getColor(R.color.colorAccent));
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty, 0, 0, 0);
            favouriteButton.setCompoundDrawableTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
            favouriteButton.setBackgroundResource(R.drawable.favs_background_dis);
        }
    }

    private void switchFilters(boolean enabled){
        if(enabled){
            filterButton.setTextColor(getColor(R.color.whiteText));
            filterButton.setCompoundDrawableTintList(ContextCompat.getColorStateList(this, R.color.whiteText));
            filterButton.setBackgroundResource(R.drawable.filters_background_en);
        }else{
            filterButton.setTextColor(getColor(R.color.colorAccent));
            filterButton.setCompoundDrawableTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
            filterButton.setBackgroundResource(R.drawable.filters_background);
        }
    }

    private void fetchRestaurants(final Boolean hasImages){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        favouriteRest = new ArrayList<>();
        DatabaseReference databaseFav = FirebaseDatabase.getInstance().getReference().child("endUsers").child(firebaseUser.getUid())
                .child("favourites");
        databaseFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    favouriteRest.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    DataSnapshot ds1 = ds.child("info");
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

                    if(restaurant.getImagePath()!=null && !hasImages){
                        imageToFetch++;
                    }

                    Long totalReviews = -1L;

                    if(ds.child("totalReviews").exists()){
                        totalReviews = ds.child("totalReviews").getValue(Long.class);
                    }

                    if(ds.child("meanDeliveryTime").exists() && ds.child("meanFoodQuality").exists() && ds.child("meanRestaurantService").exists() && totalReviews!=-1){
                        Double meanDeliveryTime = ds.child("meanDeliveryTime").getValue(Double.class);
                        Double meanFoodQuality = ds.child("meanFoodQuality").getValue(Double.class);
                        Double meanRestaurantService = ds.child("meanRestaurantService").getValue(Double.class);
                        restaurant.setMeanDeliveryTime(meanDeliveryTime/totalReviews);
                        restaurant.setMeanDeliveryTime(meanFoodQuality/totalReviews);
                        restaurant.setMeanDeliveryTime(meanRestaurantService/totalReviews);
                        restaurant.setTotalMean((meanDeliveryTime + meanFoodQuality + meanRestaurantService)/(totalReviews*3));
                    }else{
                        restaurant.setMeanDeliveryTime(-1.d);
                        restaurant.setMeanDeliveryTime(-1.d);
                        restaurant.setMeanDeliveryTime(-1.d);
                        restaurant.setTotalMean(-1.d);
                    }

                    restaurants.add(restaurant);
                    restName.add(restaurant);
                    restCuisine.add(restaurant);

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

                if(hasImages){
                    Log.d("MADPROVA", "Has Images with all images "+allImages);
                    //Ready to go
                    for(Restaurant r : restaurants){
                        if(r.getImagePath() != null)
                            r.setImagePath(storage.getPath()+File.separator+r.getUid()+".jpg");
                    }

                    allImages = sharedPrefer.getBoolean("restaurantFetches",false);

                    if(allImages){
                        showList();
                    }
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

    private void showList(){
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
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favouriteRest != null && favouriteRest.size() != 0){
                    filterFavourite(view);
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.no_favourite), Toast.LENGTH_SHORT).show();
                }
                switchPrefs(favourite);
            }
        });
    }

    private void fetchImages(final Restaurant restaurant){

        Log.d("MAD2", "Entering fetch Images with restaurant "+restaurant.getUsername());
        Log.d("MAD2", "Path "+restaurant.getImagePath());

        final File file = new File(storage.getPath()+File.separator+restaurant.getUid()+".jpg");

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child(restaurant.getImagePath()).getFile(file)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                restaurant.setImagePath(file.getPath());
                imageFetched++;
                Log.d("MADPROVA", "Restaurant "+restaurant.getUsername()+" "+imageToFetch+" "+imageFetched+" saved on "+file.getPath()+" ");
                if(imageFetched == imageToFetch){
                    sharedPrefer.edit().putBoolean("restaurantFetches", true).apply();
                    showList();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                restaurant.setImagePath(null);
                Log.d("MAD", "Restaurant "+restaurant.getUsername()+restaurant.getUsername()+" "+imageToFetch+" "+imageFetched+" not saved on "+file.getPath());
                imageFetched++;
                if(imageFetched == imageToFetch){
                    sharedPrefer.edit().putBoolean("restaurantFetches", true).apply();
                    showList();
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

    public void filterFavourite(View view){
        ArrayList<Restaurant> filteredFav = new ArrayList<>();

        if(favourite)
            favourite = false;
        else
            favourite = true;

        if(favourite) {
            for (int i = 0; i < restaurants.size(); i++) {
                if (favouriteRest.contains(restaurants.get(i).getUid())) {
                    if (restName != null && restName.size() != 0) {
                        if (restName.contains(restaurants.get(i))) {
                            if (restCuisine != null && restCuisine.size() != 0) {
                                if (restCuisine.contains(restaurants.get(i))) {
                                    filteredFav.add(restaurants.get(i));
                                }
                            }
                        }
                    } else if (restCuisine != null && restCuisine.size() != 0) {
                        if (restCuisine.contains(restaurants.get(i))) {
                            filteredFav.add(restaurants.get(i));
                        }
                    } else
                        filteredFav.add(restaurants.get(i));
                }
            }
        }
        else{
            if(restCuisine != null && restName != null){
                for(Restaurant r : restCuisine){
                    if(restName.contains(r)){
                        filteredFav.add(r);
                    }
                }
            } else if (restCuisine != null){
                filteredFav = restCuisine;
            } else if (restName != null){
                filteredFav = restName;
            } else {
                filteredFav = restaurants;
            }
        }

        filteredFav.sort(new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                if (r1.isOpen() && !r2.isOpen())
                    return -1;
                if (!r1.isOpen() && r2.isOpen())
                    return 1;
                return 0;
            }
        });

        adapter.filterList(filteredFav);
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
                if(favourite && favouriteRest.contains(restName.get(j).getUid()))
                    filteredNames.add(restName.get(j));
                else if (!favourite)
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
                            mainConstraint.animate().translationY(height + getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
                            visible = false;
                        }

                        if(dy < 0 && !visible){
                            search.animate().translationY(getResources().getDimensionPixelSize(R.dimen.short30)).setDuration(200).start();
                            mainConstraint.animate().translationY(height - getResources().getDimensionPixelSize(R.dimen.short100)).setDuration(200).start();
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
            //Non ho filtri
            switchFilters(false);
        } else {
            //Ho filtri
            switchFilters(true);
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
                if(favourite && favouriteRest.contains(restCuisine.get(j).getUid()))
                    filteredNames.add(restCuisine.get(j));
                else if(!favourite)
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
