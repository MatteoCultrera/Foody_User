package com.example.foodyrestaurant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ReservationFragment extends Fragment {

    private RecyclerView pending_recycler, doing_recycler;
    private File storageDir;
    private ArrayList<Reservation> pending_reservations = new ArrayList<>();
    private ArrayList<Reservation> doing_reservations = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RVAdapterRes adapterPending, adapterDoing;
    private boolean pending;
    private ImageButton switchInterface;
    private TextView stringUp, stringDown;
    private MainActivity father;
    private ImageView notification;

    public ReservationFragment() {}

    public void setFather(MainActivity father){
        this.father = father;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        pending_recycler = view.findViewById(R.id.pending_display);
        doing_recycler = view.findViewById(R.id.doing_display);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        notification = view.findViewById(R.id.notification_reservation);
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        pending_recycler.setLayoutManager(llm);
        LinearLayoutManager llm2 = new LinearLayoutManager(view.getContext());
        doing_recycler.setLayoutManager(llm2);

        adapterPending = new RVAdapterRes(pending_reservations, this);
        adapterDoing = new RVAdapterRes(doing_reservations, this);
        stringUp = view.findViewById(R.id.string_up);
        stringDown = view.findViewById(R.id.string_down);
        switchInterface = view.findViewById(R.id.switch_button);
        if(sharedPreferences.getBoolean("notificationKitchen", false))
            addNotification();
        else
            notification.setVisibility(View.GONE);

        switchInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pending = !pending;
                notification.setVisibility(View.GONE);
                setInterface();
            }
        });
        pending_recycler.setAdapter(adapterPending);
        doing_recycler.setAdapter(adapterDoing);
        pending = true;
        setInterface();
    }

    public boolean hasNotification(){
        return notification.getVisibility() == View.VISIBLE;
    }

    public void addNotification(){
        if(doing_recycler.getVisibility() == View.VISIBLE){
            notification.setVisibility(View.VISIBLE);
        }
    }

    public void addPendingOrder(DataSnapshot ds) {
        ReservationDBRestaurant reservationDB = ds.getValue(ReservationDBRestaurant.class);
        ArrayList<Dish> dishes = new ArrayList<>();
        for (OrderItem o : reservationDB.getDishesOrdered()) {
            Dish dish = new Dish();
            dish.setQuantity(o.getPieces());
            dish.setDishName(o.getOrderName());
            dish.setPrice(o.getPrice());
            dishes.add(dish);
        }
        Reservation.prepStatus status;
        if (reservationDB.getStatus().equals("Pending")) {
            status = Reservation.prepStatus.PENDING;
        } else if (reservationDB.getStatus().equals("Doing")) {
            status = Reservation.prepStatus.DOING;
        } else {
            status = Reservation.prepStatus.DONE;
        }

        String orderID = reservationDB.getReservationID().substring(28);
        Reservation reservation = new Reservation(orderID, dishes, status,
                reservationDB.isAccepted(), reservationDB.getOrderTimeBiker(), reservationDB.getNameUser(),
                reservationDB.getNumberPhone(), reservationDB.getResNote(), "",
                sharedPreferences.getString("email", ""), reservationDB.getUserAddress());
        reservation.setUserUID(reservationDB.getReservationID().substring(0, 28));
        reservation.setDeliveryTime(reservationDB.getOrderTime());
        reservation.setTotalPrice(reservationDB.getTotalCost());
        reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
        reservation.setRestaurantName(sharedPreferences.getString("name", null));

        int i;
        for (i = 0; i < pending_reservations.size(); i++) {
            if (reservation.getOrderTime().compareTo(pending_reservations.get(i).getOrderTime()) < 0)
                break;
        }

        pending_reservations.add(i, reservation);
        adapterPending.notifyItemInserted(i);
        adapterPending.notifyItemRangeChanged(i, pending_reservations.size());
    }

    public void addDoingOrder(DataSnapshot ds){
        ReservationDBRestaurant reservationDB = ds.getValue(ReservationDBRestaurant.class);
        ArrayList<Dish> dishes = new ArrayList<>();
        for (OrderItem o : reservationDB.getDishesOrdered()) {
            Dish dish = new Dish();
            dish.setQuantity(o.getPieces());
            dish.setDishName(o.getOrderName());
            dish.setPrice(o.getPrice());
            dishes.add(dish);
        }
        Reservation.prepStatus status;
        if (reservationDB.getStatus().equals("Pending")) {
            status = Reservation.prepStatus.PENDING;
        } else if (reservationDB.getStatus().equals("Doing")) {
            status = Reservation.prepStatus.DOING;
        } else {
            status = Reservation.prepStatus.DONE;
        }

        String orderID = reservationDB.getReservationID().substring(28);
        Reservation reservation = new Reservation(orderID, dishes, status,
                reservationDB.isAccepted(), reservationDB.getOrderTimeBiker(), reservationDB.getNameUser(),
                reservationDB.getNumberPhone(), reservationDB.getResNote(), "",
                sharedPreferences.getString("email", ""), reservationDB.getUserAddress());
        reservation.setUserUID(reservationDB.getReservationID().substring(0, 28));
        reservation.setDeliveryTime(reservationDB.getOrderTime());
        reservation.setTotalPrice(reservationDB.getTotalCost());
        reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
        reservation.setRestaurantName(sharedPreferences.getString("name", null));

        int i;
        for (i = 0; i < doing_reservations.size(); i++) {
            if (reservation.getOrderTime().compareTo(doing_reservations.get(i).getOrderTime()) < 0)
                break;
        }

        doing_reservations.add(i, reservation);
        adapterDoing.notifyItemInserted(i);
        adapterDoing.notifyItemRangeChanged(i, doing_reservations.size());
    }

    public void removeDoingOrder(DataSnapshot ds){
        if(doing_reservations.size() == 0)
            return;

        String key = ds.getKey();
        int i;
        for(i = 0; i < doing_reservations.size(); i++){
            if(key.contains(doing_reservations.get(i).getReservationID())){
                break;
            }
        }

        doing_reservations.remove(i);
        adapterDoing.notifyItemRemoved(i);
        adapterDoing.notifyItemRangeChanged(i,doing_reservations.size());
    }

    public void clearNotification(){
        notification.setVisibility(View.GONE);
    }

    public void addInDoing(Reservation toAdd){
        int index;

        for(index = 0; index  < doing_reservations.size(); index++ ){
            if(toAdd.getOrderTime().compareTo(doing_reservations.get(index).getOrderTime()) > 0)
                break;
        }

        doing_reservations.add(toAdd);
        adapterDoing.notifyItemInserted(index);
        adapterDoing.notifyItemRangeChanged(index, doing_reservations.size());
    }

    private void setInterface(){
        if(pending){
            stringUp.setText(getString(R.string.pending_orders));
            stringDown.setText(getString(R.string.doing_orders));
            pending_recycler.setVisibility(View.VISIBLE);
            doing_recycler.setVisibility(View.GONE);
        }else{
            stringUp.setText(getString(R.string.doing_orders));
            stringDown.setText(getString(R.string.pending_orders));
            pending_recycler.setVisibility(View.GONE);
            doing_recycler.setVisibility(View.VISIBLE);
        }
    }
}