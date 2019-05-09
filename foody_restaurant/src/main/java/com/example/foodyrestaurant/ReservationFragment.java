package com.example.foodyrestaurant;

import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ReservationFragment extends Fragment {

    private RecyclerView reservation;
    private final String JSON_PATH = "reservations.json";
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Reservation> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private RVAdapterRes adapter;
    private boolean toAdd;
    MainActivity father;

    public ReservationFragment() {}

    public void setFather(MainActivity father){
        this.father = father;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservation = view.findViewById(R.id.reservation_display);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onPause(){
        super.onPause();
        File file = new File(storageDir, JSON_PATH);
        String json = jsonHandler.resToJSON(reservations);
        jsonHandler.saveStringToFile(json, file);
    }

    private void init(View view){
        toAdd = true;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(storageDir, JSON_PATH);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);
        reservations = new ArrayList<>();
        adapter = new RVAdapterRes(reservations);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("restaurant");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBRestaurant reservationDB = ds.getValue(ReservationDBRestaurant.class);
                    ArrayList<Dish> dishes = new ArrayList<>();
                    for(OrderItem o : reservationDB.getDishesOrdered()){
                        Dish dish = new Dish();
                        dish.setQuantity(o.getPieces());
                        dish.setDishName(o.getOrderName());
                        dish.setPrice(o.getPrice());
                        dishes.add(dish);
                    }
                    String orderID = reservationDB.getReservationID().substring(28);
                    Reservation reservation = new Reservation(orderID, dishes, Reservation.prepStatus.PENDING,
                            reservationDB.isAccepted(), reservationDB.getOrderTimeBiker(), reservationDB.getNameUser(),
                            reservationDB.getNumberPhone(), reservationDB.getResNote(), "",
                            sharedPreferences.getString("email", ""), reservationDB.getUserAddress());
                    reservation.setUserUID(reservationDB.getReservationID().substring(0, 28));
                    reservation.setDeliveryTime(reservationDB.getOrderTime());
                    reservation.setTotalPrice(reservationDB.getTotalCost());
                    reservations.add(reservation);
                }

                reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });

                reservation.setAdapter(adapter);


                //Add the notification that advise the biker when a new reservation has been assigned to him
                DatabaseReference bikerReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("restaurant").child(firebaseUser.getUid());
                bikerReservations.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ReservationDBRestaurant reservationDB = dataSnapshot.getValue(ReservationDBRestaurant.class);
                        for (Reservation r : reservations){
                            String orderID = reservationDB.getReservationID().substring(28);
                            if (r.getReservationID().equals(orderID)){
                                toAdd = false;
                            }
                        }
                        if (toAdd) {
                            ArrayList<Dish> dishes = new ArrayList<>();
                            for(OrderItem o : reservationDB.getDishesOrdered()) {
                                Dish dish = new Dish();
                                dish.setQuantity(o.getPieces());
                                dish.setDishName(o.getOrderName());
                                dish.setPrice(o.getPrice());
                            }
                            Reservation.prepStatus status;
                            if (reservationDB.getStatus().equals("pending")){
                                status = Reservation.prepStatus.PENDING;
                            } else if (reservationDB.getStatus().equals("doing")){
                                status = Reservation.prepStatus.DOING;
                            } else{
                                status = Reservation.prepStatus.DONE;
                            }
                            Reservation reservation = new Reservation(reservationDB.getReservationID(),dishes,
                                    status,reservationDB.isAccepted(),reservationDB.getOrderTime(),reservationDB.getNameUser(),
                                    reservationDB.getNumberPhone(),reservationDB.getResNote(),null,sharedPreferences.getString("email",null),
                                    reservationDB.getUserAddress());

                            int index;
                            for (index = 0; index < reservations.size(); index++) {
                                if (reservation.getOrderTime().compareTo(reservations.get(index).getOrderTime()) > 0)
                                    break;
                            }
                            reservations.add(index, reservation);
                            adapter.notifyItemInserted(index);
                            adapter.notifyItemRangeChanged(index, reservations.size());

                            father.setNotification(1);
                        }
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reservations = jsonHandler.getReservations(file);
                reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });
                reservation.setAdapter(adapter);
            }
        });
    }
}
