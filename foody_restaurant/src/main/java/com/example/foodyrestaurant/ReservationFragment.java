package com.example.foodyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

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
        String json;
        File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String JSON_PATH = "menu.json";
        File file = new File(storageDir, JSON_PATH);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservation.setLayoutManager(llm);
        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Reservation> reservs = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();

        JsonHandler jsonHandler = new JsonHandler();

        cards = jsonHandler.getCards(file);

        cards = new ArrayList<>();
        dishes.add(new Dish("Margherita", "pizza", 2.0f, null));
        dishes.add(new Dish("Paperino", "pizza", 2.0f, null));
        dishes.add(new Dish("Margerita", "pizza", 2.0f, null));
        Reservation res = new Reservation(getResources().getString(R.string.reservation) + " N° 1", dishes, Reservation.prepStatus.PENDING);
        Reservation res2 = new Reservation(getResources().getString(R.string.reservation) + " N° 2", dishes, Reservation.prepStatus.DOING);
        res.setOrderTime("12:00");
        res2.setOrderTime("13:30");
        res.setDishesOrdered(dishes);
        res2.setDishesOrdered(dishes);
        reservs.add(res);
        reservs.add(res2);
        Log.d("MAD", ""+ reservs.get(0).getReservationID());
        Log.d("MAD", ""+ reservs.get(1).getReservationID());

        json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, file);
        RVAdapterRes adapter = new RVAdapterRes(reservs);
        reservation.setAdapter(adapter);
    }
}
