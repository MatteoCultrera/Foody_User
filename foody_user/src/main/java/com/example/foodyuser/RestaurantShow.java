package com.example.foodyuser;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class RestaurantShow extends AppCompatActivity {

    //TextView title, cuisines, deliveryPrice, distance;
    Toolbar toolbar;
    ImageView image;
    Restaurant thisRestaurant;
    ArrayList<Card> cards;
    RecyclerView menu;
    File documentsDir;
    JsonHandler handlerOrders;
    ArrayList<OrderItem> orders;
    RVAdapterMenu menuAdapter;
    ConstraintLayout totalLayout;
    TextView total;
    int shortAnimDuration;

    String reName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_show);

        if(!init())
            finish();
    }

    private boolean init(){
        shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        toolbar = findViewById(R.id.toolbar);
        totalLayout = findViewById(R.id.price_show_layout);
        total = findViewById(R.id.price_show);
        //cuisines = findViewById(R.id.restaurant_cuisines);
        //deliveryPrice = findViewById(R.id.restaurant_del_price);
        //distance = findViewById(R.id.restaurant_dist);
        image = findViewById(R.id.restaurant_image);
        menu = findViewById(R.id.menu);
        documentsDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        handlerOrders = new JsonHandler();
        menuAdapter = new RVAdapterMenu(this);

        File order = new File(documentsDir, getString(R.string.order_file_name));

        orders = handlerOrders.getOrders(order);

        totalLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


        Bundle extras = getIntent().getExtras();
        toolbar.setTitle(extras.getString("restaurant_name",""));
        reName = extras.getString("restaurant_name","");
        fetchRestaurant();
        fetchMenu();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        menu.setLayoutManager(llm);
        updateFAB();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        return true;
    }

    public void updateFAB(){
        if(orders.size() == 0){
            totalDisappear();
        }else{
            totalAppear();
        }

    }

    private void totalAppear(){
        setTotal();
        total.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.short10));
        totalLayout.setBackgroundResource(R.drawable.price_background);
        totalLayout.setClickable(true);
        totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch order Activity
                Intent intent = new Intent(totalLayout.getContext(), Order.class);

                startActivity(intent);

            }
        });

    }

    private void totalDisappear(){
        total.setText("");
        total.setCompoundDrawablePadding(0);
        totalLayout.setBackgroundResource(R.drawable.price_background_dis);
        totalLayout.setClickable(false);
        totalLayout.setOnClickListener(null);
    }

    private void setTotal(){
        int size  = orders.size();
        float totalPrice=0;
        for(int i = 0; i < size; i++){
            totalPrice+=orders.get(i).getPrice()*orders.get(i).getPieces();
        }
        total.setText(String.format("%.2f €", totalPrice));
    }

    private void fetchRestaurant(){

        if(thisRestaurant != null)
            return;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsInfo").child(reName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    thisRestaurant = ds.getValue(Restaurant.class);
                    //cuisines.setText(thisRestaurant.getKitchensString());
                    //deliveryPrice.setText(thisRestaurant.getDeliveryPriceString());
                    //distance.setText(thisRestaurant.getDistanceString());
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    mStorageRef.child("images/"+reName+"_profile.jpeg").getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Glide
                                            .with(getApplicationContext())
                                            .load(uri)
                                            .into(image);
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }

    private void fetchMenu(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsMenu").child(reName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cards = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Card card = ds1.getValue(Card.class);
                        cards.add(card);
                    }
                }
                menuAdapter.setProperties(cards, orders);
                menu.setAdapter(menuAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHandler handler = new JsonHandler();
        if(orders!=null){
            String jsonOrders = handler.ordersToJSON(orders);
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File toSave = new File(directory, getString(R.string.order_file_name));
            handler.saveStringToFile(jsonOrders, toSave);
            Log.d("PROVA",jsonOrders);
        }
    }
}
