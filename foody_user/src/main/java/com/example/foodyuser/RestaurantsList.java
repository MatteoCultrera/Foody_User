package com.example.foodyuser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantsList extends AppCompatActivity {

    private EditText searchField;
    private RecyclerView restaurantList;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private ArrayList<Restaurant> restName = new ArrayList<>();
    private ArrayList<Restaurant> restCuisine = new ArrayList<>();
    private RVAdapterRestaurants adapter;
    private boolean add = true;
    private ImageButton back;
    private ImageButton filter;
    private AlertDialog foodChooseType;
    private boolean[] checkedFoods = new boolean[27];
    private ArrayList<String> selectedFoods;
    private String[] foodCategories;
    private ArrayList<Integer> indexFoods;
    private ArrayList<Integer> copyIndexFoods;
    private boolean unchanged, checkString = true;
    private String dialogCode = "ok";
    private boolean clearFilter = false;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        init();
    }

    private void init(){
        restaurantList = findViewById(R.id.restaurants_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        restaurantList.setLayoutManager(llm);
        filter = findViewById(R.id.filterButton);
        back = findViewById(R.id.backButton);
        searchField = findViewById(R.id.search_field);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("restaurantsInfo");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Restaurant restaurant = new Restaurant();
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        restaurant = ds1.getValue(Restaurant.class);
                        if (restaurant.getCuisineTypes() != null) {
                            ArrayList<String> types = new ArrayList<>();
                            for (Integer i : restaurant.getCuisineTypes()) {
                                types.add(foodCategories[i]);
                            }
                            restaurant.setCuisines(types);
                        }
                        restaurants.add(restaurant);
                        restName.add(restaurant);
                        restCuisine.add(restaurant);
                    }
                    restaurant.setUid(ds.getKey());
                }
                adapter = new RVAdapterRestaurants(restaurants);
                restaurantList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });

        selectedFoods = new ArrayList<>();
        indexFoods = new ArrayList<>();
        copyIndexFoods = new ArrayList<>();
        foodCategories = getResources().getStringArray(R.array.foodcategory_array);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                searchField.clearFocus();
                showPickFood(filter);
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void back() {
        super.onBackPressed();
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
                for (String c : cuisines) {
                    for (String s : text) {
                        if (c.toLowerCase().contains(s.toLowerCase())) {
                            if (!restCuisine.contains(restaurants.get(i))){
                                restCuisine.add(restaurants.get(i));
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
                unchanged = false;
                if(clearFilter) {
                    selectedFoods.clear();
                    clearFilter = false;
                }

                firstTime = true;
                filterCuisine(selectedFoods);
                dialogCode = "ok";
                copyIndexFoods.clear();
                copyIndexFoods.addAll(indexFoods);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearFilter = false;
                dialogCode = "ok";
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
        dialogCode = "foodDialog";
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
