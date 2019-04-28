package com.example.foodyuser;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RestaurantsList extends AppCompatActivity {

    private EditText searchField;
    //private RecyclerView queryResult;
    private RecyclerView restaurantList;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private RVAdapterRestaurants adapter;
    private boolean add = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        searchField = findViewById(R.id.search_field);
        //queryResult = findViewById(R.id.query_result);
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsInfo");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    add = true;
                    for (Restaurant rest : restaurants){
                        if (ds.getKey().compareTo(rest.getName()) == 0) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            Restaurant restaurant = ds1.getValue(Restaurant.class);
                            restaurants.add(restaurant);
                        }
                    }
                }
                adapter = new RVAdapterRestaurants(restaurants);
                restaurantList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
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
