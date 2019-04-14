package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    private RecyclerView reservation;


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
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);

        ImageView profileImage = view.findViewById(R.id.mainImage);
        ImageView profileShadow = view.findViewById(R.id.shadow);

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);
        Glide
                .with(this)
                .load(R.drawable.pizza)
                .into(profileImage);

        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margherita","pizza",2.0f, null));
        dishes.add(new Dish("Paperino","pizza",2.0f, null));
        dishes.add(new Dish("Margerita","pizza",2.0f, null));
        Reservation res = new Reservation(getResources().getString(R.string.reservation) + " N° 1", dishes, Reservation.prepStatus.PENDING);
        Reservation res2 = new Reservation(getResources().getString(R.string.reservation) + " N° 2", dishes, Reservation.prepStatus.DOING);
        reservations.add(res);
        reservations.add(res2);

        RVAdapterRes adapter = new RVAdapterRes(reservations);
        reservation.setAdapter(adapter);
    }
}
