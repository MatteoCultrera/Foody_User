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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Order extends AppCompatActivity {

    private ArrayList<OrderItem> orders;
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

        orders = handler.getOrders(orderFile);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(firebaseUser.getUid());
                HashMap<String, Object> child = new HashMap<>();
                String identifier = firebaseUser.getUid() + System.currentTimeMillis();
                ReservationDBUser reservation = new ReservationDBUser(identifier, restID, orders, false, null, null);
                child.put(identifier, reservation);
                database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("restaurant").child(restID);
                HashMap<String, Object> childRest = new HashMap<>();
                ReservationDBRestaurant reservationRest = new ReservationDBRestaurant(identifier, "", orders, false,
                        null,sharedPreferences.getString("phoneNumber", null),
                        sharedPreferences.getString("name", null), null, null);
                childRest.put(identifier, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                File orderFile = new File(directory, getString(R.string.order_file_name));
                if(orderFile.exists())
                    orderFile.delete();
                finish();
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
        finish();
    }

    public void updatePrice(){
        float tp=0;

        for(int i = 0; i < orders.size(); i++)
            tp += orders.get(i).getTotal();

        total.setText(String.format(Locale.UK, "%.2f €", tp));
    }
}
