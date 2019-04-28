package com.example.foodyuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RestaurantsList extends AppCompatActivity {

    private RecyclerView restaurantList;
    private ArrayList<Restaurant> restaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

    }

    private void init(){

        LinearLayoutManager llm = new LinearLayoutManager(this);
        restaurantList.setLayoutManager(llm);

        ArrayList<String> kitchens = new ArrayList<>();

        //TODO: fetch info from server instead of stub infos
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

        RVAdapterRestaurants adapter = new RVAdapterRestaurants(restaurants);
        restaurantList.setAdapter(adapter);


    }

}