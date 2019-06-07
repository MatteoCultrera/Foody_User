package com.example.foodybiker;

import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterHistory extends RecyclerView.Adapter<RVAdapterHistory.CardViewHolder>{

    private ArrayList<Reservation> reservations;

    RVAdapterHistory(ArrayList<Reservation> reservations){
        this.reservations = reservations;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_item, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder pvh, final int i) {

        pvh.lastDiv.setVisibility(View.GONE);
        pvh.accept.setVisibility(View.GONE);
        pvh.decline.setVisibility(View.GONE);

        final Reservation currentRes = reservations.get(i);
        pvh.restaurantName.setText(currentRes.getRestaurantName());
        pvh.restaurantAddress.setText(currentRes.getRestaurantAddress());
        pvh.restaurantTime.setText(currentRes.getRestaurantPickupTime());
        pvh.userName.setText(currentRes.getUserName());
        pvh.userAddress.setText(currentRes.getUserAddress());
        pvh.userTime.setText(currentRes.getUserDeliveryTime());
        pvh.date.setVisibility(View.VISIBLE);
        pvh.date.setText(getDate(currentRes.getDate()));
        if(currentRes.isAccepted())
            pvh.date.setTextColor(pvh.date.getContext().getResources().getColor(R.color.accept, pvh.date.getContext().getTheme()));
        else
            pvh.date.setTextColor(pvh.date.getContext().getResources().getColor(R.color.heart_red, pvh.date.getContext().getTheme()));

    }

    private String getDate(String date){
        String[] nums = date.split("-");
        int day = Integer.valueOf(nums[2]);
        int month = Integer.valueOf(nums[1]);
        int year = Integer.valueOf(nums[0]);

        return String.format("%02d/%02d/%d",day, month, year);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName, restaurantAddress, restaurantTime, userName, userAddress, userTime, date;
        View lastDiv;
        MaterialButton accept, decline;

        CardViewHolder(View itemView) {
            super(itemView);
            restaurantName =  itemView.findViewById(R.id.pending_order_restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.pending_order_restaurant_address);
            restaurantTime = itemView.findViewById(R.id.pending_order_pickup_time);
            userName = itemView.findViewById(R.id.pending_order_user_name);
            userAddress = itemView.findViewById(R.id.pending_order_user_address);
            userTime = itemView.findViewById(R.id.pending_order_deliver_time);
            lastDiv = itemView.findViewById(R.id.pending_order_user_div);
            accept = itemView.findViewById(R.id.pending_order_accept);
            decline = itemView.findViewById(R.id.pending_order_decline);
            date = itemView.findViewById(R.id.date);

            CardView cv = itemView.findViewById(R.id.pending_order_card);
            ConstraintLayout layout = itemView.findViewById(R.id.pending_order_main_layout);

            cv.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
    }
}