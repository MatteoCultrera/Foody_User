package com.example.foodybiker;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    TextView restaurantName, restaurantAddress, userName, userAddress, notes, orderDelivered, primaryText, secondaryText;
    ConstraintLayout orderDeliveredLayout, mainLayout;
    boolean canClick;
    CardView card;
    ArrayList<Reservation> reservations;
    private String bikerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Reservation activeReservation;
    RecyclerView orderList;
    ImageButton switchButton;

    public ReservationFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void init(final View view){

        //TODO: fetch infos about active reservation and reservations, and delete this stub code

        activeReservation = null;
        reservations = new ArrayList<>();
        reservations.add(
                new Reservation("RossoPomodoro",
                        "Via Piave 17, Torino TO",
                        "12:30",
                        "Simona Currà",
                        "Via Circonvallazione 64, Torino TO",
                        "13:30", null)
        );

        reservations.add(
                new Reservation("Zen Garden",
                        "Via Monte Pasubio 16, Torino TO",
                        "19:30",
                        "Matteo Cultrera",
                        "Via Borgosesia 46, Torino TO",
                        "20:45", null)
        );

        reservations.add(
                new Reservation("La Piola",
                        "Via De Luigis 235, Torino TO",
                        "21:30",
                        "Daniele Leto",
                        "Via Del Mare 46, Torino TO",
                        "20:45", "Campanello rotto, per favore citofonare")
        );

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
                if(canClick == false && activeReservation != null){
                    orderDelivered.setText("Order Delivered");
                    canClick = true;
                }else{
                    //TODO: notify server that order was delivered
                    setInterface(false);
                    canClick = false;
                    setActiveReservation(null);
                }
            }
        });

        notes.setMovementMethod(new ScrollingMovementMethod());
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        orderList.setLayoutManager(llm);

        final RVAdapterReservation adapterReservation = new RVAdapterReservation(reservations, this);

        orderList.setAdapter(adapterReservation);

        setActiveReservation(activeReservation);
        setInterface(activeReservation!=null);

        //Add the notification when the biker receive the new
        DatabaseReference bikerReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                                                .child("biker").child(bikerUid);
        bikerReservations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Reservation res = dataSnapshot.getValue(Reservation.class);
                int index;
                for(index = 0; index<reservations.size(); index++){
                    if(res.getUserDeliveryTime().compareTo(reservations.get(index).getUserDeliveryTime()) > 0)
                        break;
                }
                reservations.add(index,res);
                adapterReservation.notifyItemInserted(index);
                adapterReservation.notifyItemRangeChanged(index, reservations.size());
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

    public void setInterface(Boolean deliveringOrder){
        if(deliveringOrder == true){
            card.setVisibility(View.VISIBLE);
            orderList.setVisibility(View.GONE);
            primaryText.setText(getString(R.string.delivering_order));
            secondaryText.setText(reservations.size()+" "+getString(R.string.pending_orders));
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background);
        }else{
            card.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            primaryText.setText(reservations.size()+" "+getString(R.string.pending_orders));
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background_dis);
            if(activeReservation == null)
                secondaryText.setText(getString(R.string.no_order_deliver));
            else
                secondaryText.setText(getString(R.string.delivering_order));
        }
    }

    public void setActiveReservation(Reservation reservation){
        this.activeReservation = reservation;
        if(reservation == null){
            switchButton.setImageResource(R.drawable.swap_dis);
            switchButton.setClickable(false);
        }else{
            switchButton.setImageResource(R.drawable.swap_white);
            switchButton.setClickable(true);
        }
    }

    public boolean canAccept(){
        return activeReservation == null;
    }


}
