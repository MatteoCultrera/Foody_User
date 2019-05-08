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
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    TextView restaurantName, restaurantAddress, userName,
            userAddress, notes, orderDelivered, primaryText, secondaryText, pickupTime, deliveryTime;
    ConstraintLayout orderDeliveredLayout, mainLayout, noteLayout;
    boolean canClick;
    CardView card;
    ArrayList<Reservation> reservations;
    Reservation activeReservation;
    RecyclerView orderList;
    ImageButton switchButton;
    RVAdapterReservation adapter;

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
                    //TODO: notify server that order was delivered
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

        notes.setMovementMethod(new ScrollingMovementMethod());
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        orderList.setLayoutManager(llm);

        adapter = new RVAdapterReservation(reservations, this, activeReservation!=null);

        orderList.setAdapter(adapter);

        setActiveReservation(activeReservation);
        setInterface(activeReservation!=null);


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
            secondaryText.setText(reservations.size()+" "+getString(R.string.pending_orders));
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

        }
    }

    public void removeItem(int pos){
        reservations.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, reservations.size());
    }




}
