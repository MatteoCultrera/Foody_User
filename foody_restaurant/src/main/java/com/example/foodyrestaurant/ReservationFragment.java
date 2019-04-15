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

        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish toAdd = new Dish("Margherita","pizza",2.0f, null);
        toAdd.setQuantity(1);
        dishes.add(toAdd);
        toAdd = new Dish("Patatine","pizza",2.0f, null);
        toAdd.setQuantity(1);
        dishes.add(toAdd);
        toAdd = new Dish("Coca Cola","pizza",2.0f, null);
        toAdd.setQuantity(1);
        dishes.add(toAdd);
        Reservation res1 = new Reservation(getResources().getString(R.string.reservation) + "252850", dishes, Reservation.prepStatus.PENDING,"12:30");
        Reservation res2 = new Reservation(getResources().getString(R.string.reservation) + "252851", dishes, Reservation.prepStatus.PENDING,"12:45");
        Reservation res3 = new Reservation(getResources().getString(R.string.reservation) + "300000", dishes, Reservation.prepStatus.PENDING, "12:45");
        Reservation res4 = new Reservation(getResources().getString(R.string.reservation) + "400000", dishes, Reservation.prepStatus.PENDING,"12:30");
        Reservation res5 = new Reservation(getResources().getString(R.string.reservation) + "500000", dishes, Reservation.prepStatus.PENDING,"12:45");
        Reservation res6 = new Reservation(getResources().getString(R.string.reservation) + "600000", dishes, Reservation.prepStatus.PENDING, "12:45");
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);
        reservations.add(res4);
        reservations.add(res5);
        reservations.add(res6);
        RVAdapterRes adapter = new RVAdapterRes(reservations);
        //adapter.setHasStableIds(true);
        reservation.setAdapter(adapter);
    }
}
