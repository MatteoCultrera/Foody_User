package com.example.foodyuser;

import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Order extends AppCompatActivity {

    private ArrayList<OrderItem> orders, copyOrders;
    private RVAdapterOrder adapter;
    private RecyclerView ordersList;
    private TextView total;
    private ImageView backButton;
    private MaterialButton placeOrder;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
    }

    public void init(){
        sharedPreferences = this.getSharedPreferences("myPreference", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        backButton = findViewById(R.id.backButton);
        placeOrder = findViewById(R.id.place_order);
        JsonHandler handler =  new JsonHandler();
        final File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File orderFile = new File(directory, getString(R.string.order_file_name));
        Bundle extras = getIntent().getExtras();
        final String restID = extras.getString("restaurantID","");
        final String restName = extras.getString("restaurantName", "");
        final String restAddress = extras.getString("restaurantAddress", null);

        orders = handler.getOrders(orderFile);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inserting the reservation inside the user reservation DB
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(firebaseUser.getUid());
                HashMap<String, Object> child = new HashMap<>();
                copyOrders = orders;
                final String identifier = firebaseUser.getUid() + System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 40);
                calendar2.add(Calendar.MINUTE, 20);
                String deliveryTime = new SimpleDateFormat("HH:mm", Locale.UK).format(calendar.getTime());
                String bikerTime = new SimpleDateFormat("HH:mm", Locale.UK).format(calendar2.getTime());
                final ReservationDBUser reservation = new ReservationDBUser(identifier, restID, copyOrders, false, null,
                        deliveryTime, "Pending", total.getText().toString());
                reservation.setRestaurantName(restName);
                reservation.setRestaurantAddress(restAddress);
                child.put(identifier, reservation);
                database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                //Inserting the new reservation inside the restaurant reservations DB
                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("restaurant").child(restID);
                HashMap<String, Object> childRest = new HashMap<>();
                ReservationDBRestaurant reservationRest = new ReservationDBRestaurant(identifier, "", copyOrders, false,
                        null,sharedPreferences.getString("phoneNumber", null),
                        sharedPreferences.getString("name", null), deliveryTime, bikerTime, "Pending",
                        sharedPreferences.getString("address", null), total.getText().toString());
                childRest.put(identifier, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                orders.clear();
                closeActivity();
            }
        });

        total = findViewById(R.id.total_price);
        ordersList = findViewById(R.id.order_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        ordersList.setLayoutManager(llm);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new RVAdapterOrder(orders, this);
        ordersList.setAdapter(adapter);

        updatePrice();

    }

    public void removeItem(int index){
        orders.remove(index);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(index, orders.size());
    }

    public void closeActivity(){
        if(orders.size() == 0){
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File orderFile = new File(directory, getString(R.string.order_file_name));
            if(orderFile.exists())
                orderFile.delete();
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHandler handler = new JsonHandler();
        if(orders!=null && orders.size() > 0){
            Log.d("TRYUNODUE", "Orders Big");
            String jsonOrders = handler.ordersToJSON(orders);
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File toSave = new File(directory, getString(R.string.order_file_name));
            handler.saveStringToFile(jsonOrders, toSave);
        }
    }

    public void updatePrice(){
        float tp=0;

        for(int i = 0; i < orders.size(); i++)
            tp += orders.get(i).getTotal();

        tp+=RestaurantShow.getRestDeliveryPrice()*0.5;
        total.setText(String.format(Locale.UK, "%.2f €", tp));
    }
}
