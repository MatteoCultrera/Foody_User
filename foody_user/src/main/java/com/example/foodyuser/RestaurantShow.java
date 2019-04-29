package com.example.foodyuser;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.SharedElementCallback;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RestaurantShow extends AppCompatActivity {

    TextView title, cuiusines, deliveryPrice, distance;
    ImageView image;
    Restaurant thisRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_show);

        if(init()==false)
            finish();
    }

    private boolean init(){

        title = findViewById(R.id.restaurant_title);
        cuiusines = findViewById(R.id.restaurant_cuisines);
        deliveryPrice = findViewById(R.id.restaurant_del_price);
        distance = findViewById(R.id.restaurant_dist);
        image = findViewById(R.id.restaurant_image);
        image.setImageResource(R.drawable.user_static_background);
        Bundle extras = getIntent().getExtras();
        title.setText(extras.getString("restaurant_name",""));
        fetchRestaurant();

        if(thisRestaurant == null){
            Log.d("SWSW", "Restaurant not Found");
            return false;
        }

        cuiusines.setText(thisRestaurant.getKitchensString());
        deliveryPrice.setText(thisRestaurant.getDeliveryPriceString());
        distance.setText(thisRestaurant.getDistanceString());

        return true;
    }

    private void fetchRestaurant(){

        if(thisRestaurant != null)
            return;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsInfo").child(title.getText().toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Restaurant restaurant = ds1.getValue(Restaurant.class);
                        if(restaurant.getName().equals(title.getText().toString())){
                            thisRestaurant = restaurant;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }
}
