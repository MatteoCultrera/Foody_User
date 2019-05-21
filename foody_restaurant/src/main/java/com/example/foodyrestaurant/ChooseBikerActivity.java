package com.example.foodyrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChooseBikerActivity extends AppCompatActivity {

    private ArrayList<BikerComplete> bikers;
    private FirebaseUser firebaseUser;
    private File storage;
    private SharedPreferences preferences;
    private double latitude, longitude;
    private RecyclerView recyclerView;
    private RVAdapterChooseBiker chooseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_biker);
        storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        preferences = getApplicationContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("restaurantsInfo");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latitude = dataSnapshot.child("info").child("latitude").getValue(Double.class);
                longitude = dataSnapshot.child("info").child("longitude").getValue(Double.class);
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Cannot Fetch Pos", Toast.LENGTH_SHORT);
                finish();
            }
        });

    }

    private void init(){
        Calendar calendar = Calendar.getInstance(Locale.ITALY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (dayOfWeek == -1)
            dayOfWeek = 6;
        final int day = dayOfWeek;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALY);
        final String time = sdf.format(calendar.getTime());

        bikers = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_choose_biker);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        chooseAdapter = new RVAdapterChooseBiker(bikers, this);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
        Query query = database;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if(d.child("status").exists()) {
                        String status = d.child("status").getValue(String.class);
                        if (status.compareTo("busy") == 0){
                            continue;
                        }
                    }
                    final BikerInfo biker = d.child("info").getValue(BikerInfo.class);
                    if(biker.getDaysTime() != null){
                        String intervalTime = biker.getDaysTime().get(day).replace(" ", "");
                        if (!intervalTime.startsWith("L") && !intervalTime.startsWith("F")) {
                            String[] splits = intervalTime.split("-");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.ITALY);
                            try{
                                Date date = sdf2.parse(splits[1]);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                cal.add(Calendar.MINUTE, -30);
                                String newTime = sdf2.format(cal.getTime());
                                if (splits[0].compareTo(time) > 0 && newTime.compareTo(time) < 0) {
                                    continue;
                                }
                            } catch(ParseException e){
                                e.getMessage();
                            }
                        }
                        else{
                            continue;
                        }
                    }
                    biker.setBikerID(d.getKey());
                    Double latitude, longitude;

                    try{
                        latitude = d.child("location").child("latitude").getValue(Double.class);
                        longitude = d.child("location").child("longitude").getValue(Double.class);
                    }catch (NullPointerException e){
                        continue;
                    }

                    if(latitude == null || longitude == null)
                        continue;

                    final LatLng position = new LatLng(latitude,longitude);

                    if(biker.getPath()!=null){
                        final File f = new File(storage, d.getKey()+".jpg");
                        if(f.exists()){
                            biker.setPath(f.getPath());
                            bikers.add(new BikerComplete(biker, position, false));
                        }else{
                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                            mStorageRef
                                    .child(biker.getPath())
                                    .getFile(f)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                                                    biker.setPath(f.getPath());
                                                    bikers.add(new BikerComplete(biker, position, true));
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    if(f.exists())
                                                        f.delete();
                                                    biker.setPath(null);
                                                    bikers.add(new BikerComplete(biker, position, false));
                                                }
                                            }
                                    );

                        }
                    }else{
                        biker.setPath(null);
                        bikers.add(new BikerComplete(biker, position, false));
                    }

                }

                for(BikerComplete b : bikers){
                    b.distance = haversineDistance(latitude, longitude, b.position.latitude, b.position.longitude);
                }

                bikers.sort(new Comparator<BikerComplete>() {
                    @Override
                    public int compare(BikerComplete o1, BikerComplete o2) {
                        if(o1.distance < o2.distance)
                            return -1;
                        if(o1.distance > o2.distance)
                            return 1;
                        return 0;
                    }
                });

                recyclerView.setAdapter(chooseAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Cannot Fetch Biker",Toast.LENGTH_SHORT);
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    public double haversineDistance(double initialLat, double initialLong,
                                    double finalLat, double finalLong){
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat-initialLat);
        double dLon = toRadians(finalLong-initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }

    public void bikerChosen(final int pos){

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("restaurant");
        Query query = database.child(firebaseUser.getUid()).child(getIntent().getStringExtra("ReservationID"));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ReservationDBRestaurant res = dataSnapshot.getValue(ReservationDBRestaurant.class);
                res.setWaitingBiker(true);
                ReservationDBBiker biker = new ReservationDBBiker(
                        getIntent().getStringExtra("ReservationID"),
                        res.getOrderTime(), res.getOrderTimeBiker(), preferences.getString("name",""),
                        res.getNameUser(), preferences.getString("address",""), res.getUserAddress(), firebaseUser.getUid());

                HashMap<String, Object> map = new HashMap<>();
                map.put(getIntent().getStringExtra("ReservationID"),biker);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("reservations").child("Bikers")
                        .child(bikers.get(pos).biker.getBikerID());
                reference.updateChildren(map);

                DatabaseReference db2 = database.child(firebaseUser.getUid()).child(getIntent().getStringExtra("ReservationID"));
                db2.child("waitingBiker").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    class BikerComplete{
        BikerInfo biker;
        LatLng position;
        boolean imageAdded;
        double distance;

        public BikerComplete(BikerInfo biker, LatLng position, boolean imageAdded){
            this.biker = biker; this.position = position; this.imageAdded = imageAdded;
        }

        public String getDistanceString(){
            return String.format("%.2f Km", distance);
        }
    }
}
