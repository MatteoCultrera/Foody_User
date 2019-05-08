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

    public ReservationFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservation = view.findViewById(R.id.reservation_display);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                init(view);
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        File file = new File(storageDir, JSON_PATH);
        String json = jsonHandler.resToJSON(reservations);
        jsonHandler.saveStringToFile(json, file);
    }

    private void init(View view){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(storageDir, JSON_PATH);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("restaurant");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reservations = new ArrayList<>();
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

                RVAdapterRes adapter = new RVAdapterRes(reservations);
                reservation.setAdapter(adapter);
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
                RVAdapterRes adapter = new RVAdapterRes(reservations);
                reservation.setAdapter(adapter);
            }
        });
    }
}
