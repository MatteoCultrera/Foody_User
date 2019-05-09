package com.example.foodyuser;

import android.content.Context;
import android.content.Intent;
import android.drm.DrmInfoStatus;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    private final List<Reservation> reservations;
    RVAdapterRes(List<Reservation> reservations){
        this.reservations = reservations;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder pvh,final int i) {

        Reservation currentRes = reservations.get(i);
        LayoutInflater inflater = LayoutInflater.from(pvh.restName.getContext());

        pvh.restName.setText(currentRes.getRestaurantName()==null?"RossoPomodoro":currentRes.getRestaurantName());
        pvh.restAddress.setText(currentRes.getRestaurantName()==null?"Via Borgosesia 52, Torino TO":currentRes.getRestaurantAddress());

        ArrayList<Dish> dishes = currentRes.getDishesOrdered();
        float total = 0;
        pvh.dishes.removeAllViews();
        if(dishes!=null){
            for(Dish d: dishes){

                View dish = inflater.inflate(R.layout.reservation_item_display_current_order, pvh.dishes, false);
                TextView name = dish.findViewById(R.id.current_item_food_name);
                TextView price = dish.findViewById(R.id.current_item_food_price);

                name.setText(d.getQuantity()+" x "+d.getDishName());
                float priceFloat = d.getPrice()*d.getQuantity();
                total += priceFloat;
                price.setText(String.format("%.2f €", priceFloat));

                pvh.dishes.addView(dish);
            }
        }

        //TODO add delivery price

        pvh.total.setText(String.format("%.2f €", total));
        pvh.deliveryTime.setText(currentRes.getDeliveryTime());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        TextView restName, restAddress, total, deliveryTime;
        LinearLayout dishes;

        CardViewHolder(View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.user_reservation_restaurant_image);
            restName = itemView.findViewById(R.id.user_reservation_restaurant_name);
            restAddress = itemView.findViewById(R.id.user_reservation_restaurant_address);
            total = itemView.findViewById(R.id.user_reservation_total);
            deliveryTime = itemView.findViewById(R.id.user_reservation_delivery_time);
            dishes = itemView.findViewById(R.id.user_reservation_dish_list);

        }
    }
}