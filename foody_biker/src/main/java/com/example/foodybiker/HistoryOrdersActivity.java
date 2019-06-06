package com.example.foodybiker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryOrdersActivity extends AppCompatActivity {


    private TextView time;
    private SpinKitView loading;
    private ImageButton back;
    private RecyclerView recyclerView;
    private boolean isPaused;
    private boolean isReady;
    private String date, month, year;
    private File storage;
    private SharedPreferences shared;
    private final String HISTORY_DIRECTORY= "historyDirectory";
    private ArrayList<Reservation> reservations;
    int imagesToFetch, imagesFetched;
    private boolean addAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);

        time = findViewById(R.id.time_history);
        loading = findViewById(R.id.loading_history);
        back = findViewById(R.id.back_history);
        recyclerView = findViewById(R.id.recycler_history);
        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("date")){
        date = extras.getString("date","");
        String[] parts = date.split("-");
        month = parts[0];
        year = parts[1];
        }else {
            finish();
        }
        time.setText(String.format("%s %s", getMonthLong(month), year));
        shared = getSharedPreferences("myPreference", MODE_PRIVATE);
        isReady = false;

        addAfter = false;
    }


    private void init(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+HISTORY_DIRECTORY);

        if(!storage.exists()){
            storage.mkdir();
        }


       fetchReservationsFromDB();

        if(addAfter){
            loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            //RVAdapterHistory history = new RVAdapterHistory(reservations);
            //recyclerView.setAdapter(history);
            addAfter = false;
        }


    }

    private void fetchReservationsFromDB(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("archive").child("Bikers").child(shared.getString("id","")).child(date);
        reservations = new ArrayList<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                    Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                            reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                            reservationDB.getOrderTime(), reservationDB.getRestaurantID(), reservationDB.getNotes(), false);
                    reservation.setReservationID(ds.getKey());
                    reservation.setUserId();
                    reservation.setUserPhone(reservationDB.getUserPhone());
                    reservation.setRestPhone(reservationDB.getRestPhone());
                    reservations.add(reservation);

                }

                if(!isPaused){
                    //ALL SET UP
                    RVAdapterHistory history = new RVAdapterHistory(reservations);
                    recyclerView.setAdapter(history);
                    loading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else if(isPaused)
                    addAfter = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });


    }


    private String getMonthLong(String value){
        String toRet = null;
        switch (value){
            case "0":
                toRet = getString(R.string.january);
                break;
            case "1":
                toRet = getString(R.string.february);
                break;
            case "2":
                toRet = getString(R.string.march);
                break;
            case "3":
                toRet = getString(R.string.april);
                break;
            case "4":
                toRet = getString(R.string.mayL);
                break;
            case "5":
                toRet = getString(R.string.june);
                break;
            case "6":
                toRet = getString(R.string.july);
                break;
            case "7":
                toRet = getString(R.string.august);
                break;
            case "8":
                toRet = getString(R.string.september);
                break;
            case "9":
                toRet = getString(R.string.october);
                break;
            case "10":
                toRet = getString(R.string.november);
                break;
            case "11":
                toRet = getString(R.string.december);
                break;

        }
        return toRet;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }
}
