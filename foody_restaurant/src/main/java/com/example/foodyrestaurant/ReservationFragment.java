package com.example.foodyrestaurant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private RecyclerView pending_recycler, doing_recycler;
    private final String JSON_PATH = "reservations.json";
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Reservation> pending_reservations;
    private ArrayList<Reservation> doing_reservations;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private RVAdapterRes adapter;
    private boolean toAdd;
    private boolean pending;
    private TextView stringUp, stringDown;
    MainActivity father;

    public ReservationFragment() {}

    public void setFather(MainActivity father){
        this.father = father;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        pending_recycler = view.findViewById(R.id.pending_display);
        doing_recycler = view.findViewById(R.id.doing_display);
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
        if (pending_reservations != null) {
            String json = jsonHandler.resToJSON(pending_reservations);
            jsonHandler.saveStringToFile(json, file);
        }
    }

    private void init(View view){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(storageDir, JSON_PATH);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        pending_recycler.setLayoutManager(llm);
        LinearLayoutManager llm2 = new LinearLayoutManager(view.getContext());
        doing_recycler.setLayoutManager(llm2);

        pending_reservations = new ArrayList<>();
        adapter = new RVAdapterRes(pending_reservations);
        stringUp = view.findViewById(R.id.string_up);
        stringDown = view.findViewById(R.id.string_down);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("restaurant");
                Query query = database.child(firebaseUser.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ReservationDBRestaurant reservationDB = ds.getValue(ReservationDBRestaurant.class);
                            if(!reservationDB.getStatus().toLowerCase().equals("done") && reservationDB.getStatus() != null) {
                                ArrayList<Dish> dishes = new ArrayList<>();
                                for (OrderItem o : reservationDB.getDishesOrdered()) {
                                    Dish dish = new Dish();
                                    dish.setQuantity(o.getPieces());
                                    dish.setDishName(o.getOrderName());
                                    dish.setPrice(o.getPrice());
                                    dishes.add(dish);
                                }
                                Reservation.prepStatus status;
                                if (reservationDB.getStatus().toLowerCase().equals("pending")){
                                    status = Reservation.prepStatus.PENDING;
                                } else if (reservationDB.getStatus().toLowerCase().equals("doing")){
                                    status = Reservation.prepStatus.DOING;
                                } else{
                                    status = Reservation.prepStatus.DONE;
                                }
                                String orderID = reservationDB.getReservationID().substring(28);
                                Reservation reservation = new Reservation(orderID, dishes, status,
                                        reservationDB.isAccepted(), reservationDB.getOrderTimeBiker(), reservationDB.getNameUser(),
                                        reservationDB.getNumberPhone(), reservationDB.getResNote(), "",
                                        sharedPreferences.getString("email", ""), reservationDB.getUserAddress());
                                reservation.setUserUID(reservationDB.getReservationID().substring(0, 28));
                                reservation.setDeliveryTime(reservationDB.getOrderTime());
                                reservation.setTotalPrice(reservationDB.getTotalCost());
                                reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
                                reservation.setRestaurantName(sharedPreferences.getString("name", null));
                                pending_reservations.add(reservation);
                            }
                        }

                        pending_reservations.sort(new Comparator<Reservation>() {
                            @Override
                            public int compare(Reservation o1, Reservation o2) {
                                return o1.getOrderTime().compareTo(o2.getOrderTime());
                            }
                        });
                        pending_recycler.setAdapter(adapter);
                        //notification code
                        DatabaseReference restaurantReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                                .child("restaurant").child(firebaseUser.getUid());
                        restaurantReservations.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                ReservationDBRestaurant reservationDB = dataSnapshot.getValue(ReservationDBRestaurant.class);
                                toAdd = true;
                                String orderID = null;
                                if(pending_reservations != null && pending_reservations.size() != 0) {
                                    for (Reservation r : pending_reservations) {
                                        if (reservationDB.getReservationID() != null) {
                                            orderID = reservationDB.getReservationID().substring(28);
                                            if (r.getReservationID().equals(orderID)) {
                                                toAdd = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (toAdd && !reservationDB.getStatus().equals("Done")) {
                                        ArrayList<Dish> dishes = new ArrayList<>();
                                        for (OrderItem o : reservationDB.getDishesOrdered()) {
                                            Dish dish = new Dish();
                                            dish.setQuantity(o.getPieces());
                                            dish.setDishName(o.getOrderName());
                                            dish.setPrice(o.getPrice());
                                        }
                                        Reservation.prepStatus status;
                                        if (reservationDB.getStatus().equals("Pending")) {
                                            status = Reservation.prepStatus.PENDING;
                                        } else if (reservationDB.getStatus().equals("Doing")) {
                                            status = Reservation.prepStatus.DOING;
                                        } else {
                                            status = Reservation.prepStatus.DONE;
                                        }
                                        Reservation reservation = new Reservation(orderID, dishes,
                                                status, reservationDB.isAccepted(), reservationDB.getOrderTime(), reservationDB.getNameUser(),
                                                reservationDB.getNumberPhone(), reservationDB.getResNote(), null, sharedPreferences.getString("email", null),
                                                reservationDB.getUserAddress());

                                        int index;
                                        for (index = 0; index < pending_reservations.size(); index++) {
                                            if (reservation.getOrderTime().compareTo(pending_reservations.get(index).getOrderTime()) > 0)
                                                break;
                                        }
                                        pending_reservations.add(index, reservation);
                                        adapter.notifyItemInserted(index);
                                        adapter.notifyItemRangeChanged(index, pending_reservations.size());
                                        father.setNotification(1);
                                    }
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
                        pending = true;
                        setInterface();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        pending_reservations = jsonHandler.getReservations(file);
                        pending_reservations.sort(new Comparator<Reservation>() {
                            @Override
                            public int compare(Reservation o1, Reservation o2) {
                                return o1.getOrderTime().compareTo(o2.getOrderTime());
                            }
                        });
                        pending_recycler.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    private void setInterface(){
        if(pending){
            stringUp.setText(getString(R.string.pending_orders));
            stringDown.setText(getString(R.string.doing_orders));
            pending_recycler.setVisibility(View.VISIBLE);
            doing_recycler.setVisibility(View.GONE);
        }else{
            stringUp.setText(getString(R.string.doing_orders));
            stringDown.setText(getString(R.string.pending_orders));
            pending_recycler.setVisibility(View.GONE);
            doing_recycler.setVisibility(View.VISIBLE);
        }
    }

}
