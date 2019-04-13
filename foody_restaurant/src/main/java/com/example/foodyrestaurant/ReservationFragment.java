package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    RecyclerView reservation;
    private ArrayList<Reservation> reservations;
    LinearLayoutManager llm;

    ImageView profileImage, profileShadow;


    public ReservationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservation = view.findViewById(R.id.reservation_display);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);

        this.profileImage = view.findViewById(R.id.mainImage);
        this.profileShadow = view.findViewById(R.id.shadow);

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);
        Glide
                .with(this)
                .load(R.drawable.pizza)
                .into(profileImage);

        reservations = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margherita","pizza",2.0f, null));
        dishes.add(new Dish("Paperino","pizza",2.0f, null));
        dishes.add(new Dish("Margerita","pizza",2.0f, null));
        Reservation res = new Reservation(getResources().getString(R.string.reservation) + " N° 1", dishes);
        Reservation res2 = new Reservation(getResources().getString(R.string.reservation) + " N° 2", dishes);
        reservations.add(res);
        reservations.add(res2);

        RVAdapterRes adapter = new RVAdapterRes(reservations);
        reservation.setAdapter(adapter);
    }
}
