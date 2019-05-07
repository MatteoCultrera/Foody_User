package com.example.foodyuser;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class RestaurantShow extends AppCompatActivity {

    private TextView deliveryPrice;
    private Toolbar toolbar;
    private ImageView image;
    private Restaurant thisRestaurant;
    private ArrayList<Card> cards;
    private RecyclerView menu;
    private File documentsDir;
    private JsonHandler handlerOrders;
    private ArrayList<OrderItem> orders;
    private RVAdapterMenu menuAdapter;
    private ConstraintLayout totalLayout;
    private TextView total;
    private int shortAnimDuration;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private boolean unchanged;
    private String dialogCode = "ok";
    private AlertDialog dialogDism;

    String reName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_show);

        if(!init())
            finish();
    }

    private boolean init(){
        unchanged = true;
        shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        totalLayout = findViewById(R.id.price_show_layout);
        total = findViewById(R.id.price_show);
        //cuisines = findViewById(R.id.restaurant_cuisines);
        deliveryPrice = findViewById(R.id.restaurant_del_price);
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
        reName = extras.getString("restaurant_name","");
        fetchRestaurant();
        fetchMenu();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        menu.setLayoutManager(llm);
        updateFAB();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToRestList(v);
            }
        });

        Log.d("ORDERS", "init: print orders");

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO LIST: on back from cart update the quantity
        Log.d("ORDERS", "onResume: print orders");
        for(int i = 0; i < orders.size(); i++)
            Log.d("ORDERS", "i " + i + " order name " + orders.get(i).getOrderName() + " quantity " + orders.get(i).getPieces());
    }

    public void backToRestList(View view) {
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
                    RestaurantShow.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }


    public void updateFAB(){
        if(orders.size() == 0){
            unchanged = true;
            totalDisappear();
        }else{
            unchanged = false;
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
                intent.putExtra("restaurantID", firebaseUser.getUid());
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
        Query query = database.child("restaurantsInfo").child(reName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    thisRestaurant = ds.getValue(Restaurant.class);
                    //cuisines.setText(thisRestaurant.getKitchensString());
                    deliveryPrice.setText(thisRestaurant.getDeliveryPriceString());
                    //distance.setText(thisRestaurant.getDistanceString());
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    mStorageRef.child("images/restaurants/"+ reName +"_profile.jpeg").getDownloadUrl()
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
                toolbar.setTitle(thisRestaurant.getUsername());
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
