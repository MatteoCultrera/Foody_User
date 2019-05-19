package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class RVAdapterBiker extends RecyclerView.Adapter<RVAdapterBiker.CardViewHolder>{

    private final List<BikerFragment.ReservationBiker> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private BikerFragment fatherClass;

    RVAdapterBiker(List<BikerFragment.ReservationBiker> reservations, BikerFragment fatherClass){
        this.reservations = reservations;
        this.fatherClass = fatherClass;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public RVAdapterBiker.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.biker_card_display, viewGroup, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = viewGroup.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        return new RVAdapterBiker.CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterBiker.CardViewHolder pvh, int i) {

        BikerFragment.ReservationBiker current = reservations.get(i);
        LayoutInflater inflater = LayoutInflater.from(pvh.idOrder.getContext());

        pvh.idOrder.setText(current.getReservation().getReservationID());
        pvh.time.setText(current.getReservation().getDeliveryTime());
        pvh.status.setText(current.getReservation().getPreparationStatusString());
        if(current.hasBiker()){
            pvh.bikerInfoLayout.setVisibility(View.VISIBLE);
            pvh.callBiker.setVisibility(View.GONE);
        }else{
            pvh.bikerInfoLayout.setVisibility(View.GONE);
            pvh.callBiker.setVisibility(View.VISIBLE);
        }

        pvh.dishesLayout.removeAllViews();
        for(Dish d: current.getReservation().getDishesOrdered()){
            final View dish = inflater.inflate(R.layout.reservation_item_display, pvh.dishesLayout, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(d.getStringForRes());
            pvh.dishesLayout.addView(dish);
        }

        pvh.callBiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pvh.bikerImage.getContext().getApplicationContext(), ChooseBikerActivity.class);
                pvh.bikerImage.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView idOrder, time, status, bikerName, bikerLevel;
        LinearLayout dishesLayout;
        ConstraintLayout bikerInfoLayout;
        MaterialButton callBiker;
        CircleImageView bikerImage;
        ImageButton phoneButton;

        CardViewHolder(View itemView) {
            super(itemView);
            idOrder = itemView.findViewById(R.id.id_order_biker);
            time = itemView.findViewById(R.id.time_biker);
            status = itemView.findViewById(R.id.status_biker);
            bikerName = itemView.findViewById(R.id.biker_name);
            bikerLevel = itemView.findViewById(R.id.biker_level);
            dishesLayout = itemView.findViewById(R.id.order_list_biker);
            bikerInfoLayout = itemView.findViewById(R.id.info_biker);
            callBiker = itemView.findViewById(R.id.main_button);
            bikerImage = itemView.findViewById(R.id.biker_image);
            phoneButton = itemView.findViewById(R.id.phone_biker);
        }
    }
}