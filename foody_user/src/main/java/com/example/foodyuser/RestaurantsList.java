package com.example.foodyuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RestaurantsList extends AppCompatActivity {

    private EditText searchField;
    //private RecyclerView queryResult;

    private RecyclerView restaurantList;
    private ArrayList<Restaurant> restaurants;
    private RVAdapterRestaurants adapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        searchField = findViewById(R.id.search_field);
        //queryResult = findViewById(R.id.query_result);

        //databaseReference = FirebaseDatabase.getInstance().getReference();

        //queryResult.setHasFixedSize(true);
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

        init();

    }

    private void init(){

        restaurantList = findViewById(R.id.restaurants_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        restaurantList.setLayoutManager(llm);

        ArrayList<String> kitchens = new ArrayList<>();

        //TODO: fetch info from server instead of stub infos
        restaurants = new ArrayList<>();
        kitchens.add("Italian");
        kitchens.add("Pizza");
        restaurants.add(new Restaurant("RossoPomodoro", kitchens, 3.5f,3.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("French");
        kitchens.add("Restaurant");
        restaurants.add(new Restaurant("Les Escargots", kitchens, 3.8f,4.5f));

        kitchens = new ArrayList<String>();
        kitchens.add("Vegetarian");
        kitchens.add("Vegan");
        restaurants.add(new Restaurant("Veggie Town", kitchens, 31.5f,8.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("Italian");
        kitchens.add("Pizza");
        restaurants.add(new Restaurant("RossoPomodoro", kitchens, 3.5f,3.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("French");
        kitchens.add("Restaurant");
        restaurants.add(new Restaurant("Les Escargots", kitchens, 3.8f,4.5f));

        kitchens = new ArrayList<String>();
        kitchens.add("Vegetarian");
        kitchens.add("Vegan");
        restaurants.add(new Restaurant("Veggie Town", kitchens, 31.5f,8.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("Italian");
        kitchens.add("Pizza");
        restaurants.add(new Restaurant("RossoPomodoro", kitchens, 3.5f,3.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("French");
        kitchens.add("Restaurant");
        restaurants.add(new Restaurant("Les Escargots", kitchens, 3.8f,4.5f));

        kitchens = new ArrayList<String>();
        kitchens.add("Vegetarian");
        kitchens.add("Vegan");
        restaurants.add(new Restaurant("Veggie Town", kitchens, 31.5f,8.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("Italian");
        kitchens.add("Pizza");
        restaurants.add(new Restaurant("RossoPomodoro", kitchens, 3.5f,3.2f));

        kitchens = new ArrayList<String>();
        kitchens.add("French");
        kitchens.add("Restaurant");
        restaurants.add(new Restaurant("Les Escargots", kitchens, 3.8f,4.5f));

        kitchens = new ArrayList<String>();
        kitchens.add("Vegetarian");
        kitchens.add("Vegan");
        restaurants.add(new Restaurant("Veggie Town", kitchens, 31.5f,8.2f));

        adapter = new RVAdapterRestaurants(restaurants);
        restaurantList.setAdapter(adapter);

    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Restaurant> filtedNames = new ArrayList<>();

        //looping through existing elements
        for (int i = 0; i < restaurants.size(); i++)
            if(restaurants.get(i).getName().toLowerCase().contains(text.toLowerCase()))
                filtedNames.add(restaurants.get(i));

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filtedNames);
    }
}
