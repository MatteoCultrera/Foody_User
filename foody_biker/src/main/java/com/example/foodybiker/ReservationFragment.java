package com.example.foodybiker;

import android.animation.LayoutTransition;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class ReservationFragment extends Fragment {

    TextView restaurantName, restaurantAddress, userName,
            userAddress, notes, orderDelivered, primaryText, secondaryText, pickupTime, deliveryTime;
    ConstraintLayout orderDeliveredLayout, mainLayout, noteLayout;
    boolean canClick;
    CardView card;
    private ArrayList<Reservation> reservations;
    private Reservation activeReservation;
    private RecyclerView orderList;
    private ImageButton switchButton;
    private RVAdapterReservation adapter;
    private boolean toAdd;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private MainActivity father;
    private SharedPreferences sharedPreferences;
    private int pending;

    public ReservationFragment(){}

    public void setFather(MainActivity father){
        this.father = father;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SWSW", "oncreate");
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("SWSW", "onview");
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(final View view){
        final ReservationFragment ref = this;
        toAdd = true;
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        pending = sharedPreferences.getInt("pending", 0);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        activeReservation = null;
        reservations = new ArrayList<>();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("Bikers");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                    if (reservationDB.getStatus() == null) {
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(),null, false);
                        reservation.setReservationID(ds.getKey());
                        reservations.add(reservation);
                    } else if(reservationDB.getStatus().equals("accepted")){
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(),null, true);
                        reservation.setReservationID(ds.getKey());
                        activeReservation = reservation;
                    }
                }
                adapter = new RVAdapterReservation(reservations, ref, activeReservation!=null);
                setActiveReservation(activeReservation);
                setInterface(activeReservation!=null);

                if(reservations.size() != pending){
                    sharedPreferences.edit().putInt("pending", reservations.size()).apply();
                    sharedPreferences.edit().putBoolean("hasNotification", true).apply();
                    father.setNotification(1);
                }

                orderList.setAdapter(adapter);
                notes.setMovementMethod(new ScrollingMovementMethod());
                LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
                orderList.setLayoutManager(llm);

                //notification
                DatabaseReference bikerReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("Bikers").child(firebaseUser.getUid());
                bikerReservations.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ReservationDBBiker reservationDB = dataSnapshot.getValue(ReservationDBBiker.class);
                        if (reservationDB.getStatus() == null) {
                            toAdd = true;
                            if(reservations != null) {
                                for (Reservation r : reservations) {
                                    if (r.getReservationID().equals(reservationDB.getReservationID())) {
                                        toAdd = false;
                                    }
                                }
                            }
                            if (toAdd) {
                                Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                        reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                        reservationDB.getOrderTime(), reservationDB.getRestaurantID(), null, false);
                                reservation.setReservationID(dataSnapshot.getKey());

                                int index;
                                for (index = 0; index < reservations.size(); index++) {
                                    if (reservation.getUserDeliveryTime().compareTo(reservations.get(index).getUserDeliveryTime()) > 0)
                                        break;
                                }
                                reservations.add(index, reservation);
                                adapter.notifyItemInserted(index);
                                adapter.notifyItemRangeChanged(index, reservations.size());
                                if(reservations.size() != pending){
                                    sharedPreferences.edit().putInt("pending", reservations.size()).apply();
                                    sharedPreferences.edit().putBoolean("hasNotification", true).apply();
                                    father.setNotification(1);
                                }

                                father.newReservation(reservation);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        restaurantName = view.findViewById(R.id.pickup_restaurant_name);
        restaurantAddress = view.findViewById(R.id.pickup_restaurant_address);
        userName = view.findViewById(R.id.deliver_user_name);
        userAddress = view.findViewById(R.id.deliver_user_address);
        notes = view.findViewById(R.id.notes_box);
        orderDeliveredLayout = view.findViewById(R.id.order_delivered_layout);
        orderDelivered = view.findViewById(R.id.order_delivered);
        mainLayout = view.findViewById(R.id.main_layout);
        card = view.findViewById(R.id.card_order);
        card.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        orderList = view.findViewById(R.id.order_list);
        primaryText = view.findViewById(R.id.string_up);
        secondaryText = view.findViewById(R.id.string_down);
        switchButton = view.findViewById(R.id.switch_button);
        noteLayout = view.findViewById(R.id.note_layout);
        pickupTime = view.findViewById(R.id.pickup_time);
        deliveryTime = view.findViewById(R.id.deliver_time);

        orderDeliveredLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        canClick = false;

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDelivered.setText("");
                canClick = false;
            }
        });

        orderDeliveredLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canClick && card.getVisibility() == View.VISIBLE){
                    orderDelivered.setText(getString(R.string.order_delivered));
                    canClick = true;
                }else if(card.getVisibility() == View.VISIBLE){
                    DatabaseReference databaseB = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers").child(firebaseUser.getUid());
                    HashMap<String, Object> childB = new HashMap<>();
                    childB.put("status", "free");
                    databaseB.updateChildren(childB).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(father, R.string.error_order, Toast.LENGTH_SHORT).show();
                        }
                    });

                    DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                            .child("reservations").child("Bikers").child(firebaseUser.getUid())
                            .child(activeReservation.getReservationID());
                    HashMap<String, Object> childRest = new HashMap<>();
                    childRest.put("status", "delivered");
                    databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(father, R.string.error_order, Toast.LENGTH_SHORT).show();
                        }
                    });

                    father.noActiveReservation(activeReservation);

                    setInterface(false);
                    canClick = false;
                    setActiveReservation(null);
                    adapter.setOrderActive(false);
                    orderDelivered.setText("");
                    updateTitles();
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDelivered.setText("");
                canClick = false;
                if(card.getVisibility() == View.VISIBLE)
                    setInterface(false);
                else
                    setInterface(true);
                updateTitles();
            }
        });
    }

    public void updateTitles(){
        if(card.getVisibility() == View.GONE){
            primaryText.setText(reservations.size()+" "+getString(R.string.pending_orders));
            if(activeReservation == null){
                secondaryText.setText(getString(R.string.no_order_deliver));
            }else {
                secondaryText.setText(getString(R.string.delivering_order));
            }
        }else{
              if(activeReservation == null){
                primaryText.setText(getString(R.string.no_order_deliver));
            }else {
                primaryText.setText(getString(R.string.delivering_order));
            }
            secondaryText.setText(  reservations.size()+" "+getString(R.string.pending_orders));
        }
    }

    public void setInterface(Boolean deliveringOrder){
        updateTitles();
        if(deliveringOrder ){
            card.setVisibility(View.VISIBLE);
            orderList.setVisibility(View.GONE);
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background);
        }else{
            card.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background_dis);
        }
    }

    public void setActiveReservation(Reservation reservation){
        this.activeReservation = reservation;
        if(reservation == null){
            switchButton.setImageResource(R.drawable.swap_dis);
            switchButton.setClickable(false);
            adapter.setOrderActive(false);
            for(int i = 0; i < reservations.size() ; i++){
                adapter.notifyItemChanged(i);
            }
        }else{
            switchButton.setImageResource(R.drawable.swap_white);
            switchButton.setClickable(true);
            adapter.setOrderActive(true);
            for(int i = 0; i < reservations.size() ; i++){
                adapter.notifyItemChanged(i);
            }
            restaurantName.setText(activeReservation.getRestaurantName());
            restaurantAddress.setText(activeReservation.getRestaurantAddress());
            userName.setText(activeReservation.getUserName());
            userAddress.setText(activeReservation.getUserAddress());
            pickupTime.setText(activeReservation.getRestaurantPickupTime());
            deliveryTime.setText(activeReservation.getUserDeliveryTime());
            if(activeReservation.getNotes() == null){
                noteLayout.setVisibility(View.GONE);
            }else{
                noteLayout.setVisibility(View.VISIBLE);
                notes.setText(activeReservation.getNotes());
            }

            father.thereisActive(activeReservation);
        }
    }

    public void removeItem(int pos){
        reservations.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, reservations.size());
    }
}