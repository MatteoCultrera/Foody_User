package com.example.foodybiker;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReservationFragment extends Fragment {

    TextView restaurantName, restaurantAddress, userName, userAddress, notes, orderDelivered;
    ConstraintLayout orderDeliveredLayout, mainLayout;
    boolean canDeliver, canClick;
    CardView card;

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

        orderDeliveredLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        canClick = false;
        canDeliver = true;

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
                if(canClick == false && canDeliver == true){
                    orderDelivered.setText("Order Delivered");
                    canClick = true;
                }else{
                    //TODO: notify server that order was delivered
                    card.setVisibility(View.GONE);
                    orderDelivered.setText("");
                    orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background_dis);
                    canClick = false;
                    canDeliver = false;
                }
            }
        });

        notes.setMovementMethod(new ScrollingMovementMethod());

    }

    public void setActiveOrder(){

    }
}
