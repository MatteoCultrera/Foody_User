package com.example.foodyrestaurant;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ReservationFragment extends Fragment {

    private RecyclerView reservation;
    private final String JSON_PATH = "reservations.json";
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Reservation> reservations;
    private String restaurantUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservation = view.findViewById(R.id.reservation_display);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onPause(){
        super.onPause();
        File file = new File(storageDir, JSON_PATH);
        String json = jsonHandler.resToJSON(reservations);
        jsonHandler.saveStringToFile(json, file);
        Log.d("MAD", ""+json);
    }

    private void init(View view){
        String json;
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(storageDir, JSON_PATH);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);

        reservations = jsonHandler.getReservations(file);

        if(reservations.size() == 0) {
            reservations = new ArrayList<>();

            ArrayList<Dish> dishes = new ArrayList<>();
            Dish toAdd = new Dish("Margherita", "pizza", 2.0f, null);
            toAdd.setQuantity(1);
            dishes.add(toAdd);
            toAdd = new Dish("Patatine", "pizza", 2.0f, null);
            toAdd.setQuantity(3);
            dishes.add(toAdd);
            toAdd = new Dish("Coca Cola", "pizza", 2.0f, null);
            toAdd.setQuantity(6);
            dishes.add(toAdd);
            Reservation res1 = new Reservation(getResources().getString(R.string.idRes) + " " +"252850",
                    dishes, Reservation.prepStatus.PENDING, false,"12:30", "Daniele Leto",
                    "3469489722", "Suonare campanello giallo", "Foody Expert",
                    "danieleleto@gmail.com", "Viale Dei Nanni 5, Torino");
            dishes = new ArrayList<>();
            toAdd = new Dish("Cotoletta", "pizza", 2.0f, null);
            toAdd.setQuantity(2);
            dishes.add(toAdd);
            toAdd = new Dish("Quattro Stagioni", "pizza", 2.0f, null);
            toAdd.setQuantity(4);
            dishes.add(toAdd);
            Reservation res2 = new Reservation(getResources().getString(R.string.idRes) + " " +"252851",
                    dishes, Reservation.prepStatus.PENDING, false,"13:00", "Matteo Cultrera",
                    "3333333333", null, "Foody Beginner",
                    "matteocult@gmail.com", "Via Abruzzi 37, Torino");
            dishes = new ArrayList<>();
            toAdd = new Dish("Coca Cola", "pizza", 2.0f, null);
            toAdd.setQuantity(3);
            dishes.add(toAdd);
            toAdd = new Dish("Vegetariana", "pizza", 2.0f, null);
            toAdd.setQuantity(4);
            dishes.add(toAdd);
            Reservation res3 = new Reservation(getResources().getString(R.string.idRes) + " " +"300000",
                    dishes, Reservation.prepStatus.PENDING, false,"13:00", "Matteo Cultrera",
                    "3333333333", null, "Foody Beginner",
                    "matteocult@gmail.com", "Via Abruzzi 37, Torino");
            dishes = new ArrayList<>();
            toAdd = new Dish("Margherita", "pizza", 2.0f, null);
            toAdd.setQuantity(1);
            dishes.add(toAdd);
            toAdd = new Dish("Patatine", "pizza", 2.0f, null);
            toAdd.setQuantity(3);
            dishes.add(toAdd);
            toAdd = new Dish("Coca Cola", "pizza", 2.0f, null);
            toAdd.setQuantity(6);
            dishes.add(toAdd);
            Reservation res4 = new Reservation(getResources().getString(R.string.idRes) + " " +"400000",
                    dishes, Reservation.prepStatus.PENDING, false,"12:30", "Mattia Cara",
                    "3469489722", "Suonare campanello giallo", "Foody Expert",
                    "mattCara@gmail.com", "Via Boggio 55, Torino");
            dishes = new ArrayList<>();
            toAdd = new Dish("Cotoletta", "pizza", 2.0f, null);
            toAdd.setQuantity(2);
            dishes.add(toAdd);
            toAdd = new Dish("Quattro Stagioni", "pizza", 2.0f, null);
            toAdd.setQuantity(4);
            dishes.add(toAdd);
            toAdd = new Dish("Margherita", "pizza", 2.0f, null);
            toAdd.setQuantity(1);
            dishes.add(toAdd);
            toAdd = new Dish("Patatine", "pizza", 2.0f, null);
            toAdd.setQuantity(3);
            dishes.add(toAdd);
            toAdd = new Dish("Coca Cola", "pizza", 2.0f, null);
            toAdd.setQuantity(6);
            dishes.add(toAdd);
            Reservation res5 = new Reservation(getResources().getString(R.string.idRes) + " " +"500000",
                    dishes, Reservation.prepStatus.PENDING, false,"14:15", "Fabio Carfì",
                    "3469489722", "Suonare campanello giallo, perchè ho bisogno della pizza calda e consegnata in tempo",
                    "Foody Beginner", "fabCarfi@gmail.com", "Via Tripoli 101, Torino");

            reservations.add(res1);
            reservations.add(res2);
            reservations.add(res3);
            reservations.add(res4);
            reservations.add(res5);

            reservations.sort(new Comparator<Reservation>() {
                @Override
                public int compare(Reservation o1, Reservation o2) {
                    return o1.getOrderTime().compareTo(o2.getOrderTime());
                }
            });

            json = jsonHandler.resToJSON(reservations);
            jsonHandler.saveStringToFile(json, file);
        }

        final RVAdapterRes adapter = new RVAdapterRes(reservations);
        reservation.setAdapter(adapter);

        //Add the notification to the restaurant, when a new reservation is added
        DatabaseReference restaurantReservations = FirebaseDatabase.getInstance().getReference().child("Reservations")
                                        .child("Restaurants").child(restaurantUid);
        restaurantReservations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Reservation res = dataSnapshot.getValue(Reservation.class);
                int index;
                for(index=0; index < reservations.size(); index++){
                    if(res.getOrderTime().compareTo(reservations.get(index).getOrderTime()) > 0)
                        break;
                }
                reservations.add(index, res);
                adapter.notifyItemInserted(index);
                adapter.notifyItemRangeChanged(index, reservations.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
