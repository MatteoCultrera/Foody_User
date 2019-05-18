package com.example.foodyrestaurant;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BikerFragment extends Fragment {

    private RecyclerView notAcceptedRecycler, acceptedRecycler;
    private TextView stringUp, stringDown;
    private ImageButton switchButton;
    private ArrayList<ReservationBiker> reservationList;
    private ArrayList<ReservationBiker> reservationAcceptedList;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private RVAdapterBiker adapterAccepted, adapterNotAccepted;

    public BikerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_biker, container, false);
        notAcceptedRecycler = view.findViewById(R.id.not_accepted_display);
        acceptedRecycler = view.findViewById(R.id.accepted_display);
        stringUp = view.findViewById(R.id.string_up_biker);
        stringDown = view.findViewById(R.id.string_down_biker);
        switchButton = view.findViewById(R.id.switch_button_biker);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){

        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        acceptedRecycler.setLayoutManager(llm);
        LinearLayoutManager llm2 = new LinearLayoutManager(view.getContext());
        notAcceptedRecycler.setLayoutManager(llm2);

        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);

        reservationList = new ArrayList<>();
        reservationAcceptedList = new ArrayList<>();

        adapterAccepted = new RVAdapterBiker(reservationAcceptedList, this);
        adapterNotAccepted = new RVAdapterBiker(reservationList, this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("restaurant");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBRestaurant reservationDB = ds.getValue(ReservationDBRestaurant.class);
                    //Fetch Dishes
                    ArrayList<Dish> dishes = new ArrayList<>();
                    for (OrderItem o : reservationDB.getDishesOrdered()) {
                        Dish dish = new Dish();
                        dish.setQuantity(o.getPieces());
                        dish.setDishName(o.getOrderName());
                        dish.setPrice(o.getPrice());
                        dishes.add(dish);
                    }
                    //Fetch Status
                    Reservation.prepStatus status;
                    if (reservationDB.getStatus().toLowerCase().equals("pending")){
                        status = Reservation.prepStatus.PENDING;
                    } else if (reservationDB.getStatus().toLowerCase().equals("doing")){
                        status = Reservation.prepStatus.DOING;
                    } else{
                        status = Reservation.prepStatus.DONE;
                    }
                    //Fetch OrderID
                    String orderID = reservationDB.getReservationID().substring(28);

                    Reservation reservation = new Reservation(orderID, dishes, status,
                            reservationDB.isAccepted(), reservationDB.getOrderTimeBiker(), reservationDB.getNameUser(),
                            reservationDB.getNumberPhone(), reservationDB.getResNote(), "Foody Beginner",
                            "", reservationDB.getUserAddress());

                    reservation.setUserUID(reservationDB.getReservationID().substring(0, 28));
                    reservation.setDeliveryTime(reservationDB.getOrderTime());
                    reservation.setTotalPrice(reservationDB.getTotalCost());
                    reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
                    reservation.setRestaurantName(sharedPreferences.getString("name", null));
                    String biker = reservationDB.getBikerID();
                    if(!reservation.getPreparationStatusString().toLowerCase().equals("pending") && biker.equals(""))
                        reservationList.add(new ReservationBiker(reservation));
                    if(!reservation.getPreparationStatusString().toLowerCase().equals("pending") && !biker.equals("")){
                        reservationAcceptedList.add(new ReservationBiker(reservation,biker));
                    }

                }

                reservationList.sort(new Comparator<ReservationBiker>() {
                    @Override
                    public int compare(ReservationBiker o1, ReservationBiker o2) {
                        return o1.getReservation().getOrderTime().compareTo(o2.getReservation().getOrderTime());
                    }
                });

                reservationAcceptedList.sort(new Comparator<ReservationBiker>() {
                    @Override
                    public int compare(ReservationBiker o1, ReservationBiker o2) {
                        return o1.getReservation().getOrderTime().compareTo(o2.getReservation().getOrderTime());
                    }
                });

                notAcceptedRecycler.setAdapter(adapterNotAccepted);
                acceptedRecycler.setAdapter(adapterAccepted);

                /*
                //Add the notification that advise the restaurant when a new reservation has been assigned to him
                DatabaseReference restaurantReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("restaurant").child(firebaseUser.getUid());
                restaurantReservations.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ReservationDBRestaurant reservationDB = dataSnapshot.getValue(ReservationDBRestaurant.class);
                        toAdd = true;
                        String orderID = null;
                        if(pending_reservations != null && pending_reservations.size() != 0) {
                            for (Reservation r : pending_reservations) {
                                orderID = reservationDB.getReservationID().substring(28);
                                if (r.getReservationID().equals(orderID)) {
                                    toAdd = false;
                                    break;
                                }
                            }
                            if (toAdd && !reservationDB.getStatus().equals("Done")) {
                                ArrayList<Dish> dishes = new ArrayList<>();
                                for (OrderItem o : reservationDB.getDishesOrdered()) {
                                    Dish dish = new Dish();
                                    dish.setQuantity(o.getPieces());
                                    dish.setDishName(o.getOrderName());
                                    dish.setPrice(o.getPrice());
                                }
                                Reservation.prepStatus status;
                                if (reservationDB.getStatus().equals("Pending")) {
                                    status = Reservation.prepStatus.PENDING;
                                } else if (reservationDB.getStatus().equals("Doing")) {
                                    status = Reservation.prepStatus.DOING;
                                } else {
                                    status = Reservation.prepStatus.DONE;
                                }
                                Reservation reservation = new Reservation(orderID, dishes,
                                        status, reservationDB.isAccepted(), reservationDB.getOrderTime(), reservationDB.getNameUser(),
                                        reservationDB.getNumberPhone(), reservationDB.getResNote(), null, sharedPreferences.getString("email", null),
                                        reservationDB.getUserAddress());

                                int index;
                                if(reservation.getPreparationStatusString().toLowerCase().equals("pending")){
                                    for (index = 0; index < pending_reservations.size(); index++) {
                                        if (reservation.getOrderTime().compareTo(pending_reservations.get(index).getOrderTime()) > 0)
                                            break;
                                    }
                                    pending_reservations.add(index, reservation);
                                    adapterPending.notifyItemInserted(index);
                                    adapterPending.notifyItemRangeChanged(index, pending_reservations.size());
                                    father.setNotification(1);
                                }
                            }
                        }
                        if(doing_reservations != null && doing_reservations.size() != 0) {
                            for (Reservation r : doing_reservations) {
                                orderID = reservationDB.getReservationID().substring(28);
                                if (r.getReservationID().equals(orderID)) {
                                    toAdd = false;
                                    break;
                                }
                            }
                            if (toAdd && !reservationDB.getStatus().equals("Done")) {
                                ArrayList<Dish> dishes = new ArrayList<>();
                                for (OrderItem o : reservationDB.getDishesOrdered()) {
                                    Dish dish = new Dish();
                                    dish.setQuantity(o.getPieces());
                                    dish.setDishName(o.getOrderName());
                                    dish.setPrice(o.getPrice());
                                }
                                Reservation.prepStatus status;
                                if (reservationDB.getStatus().equals("Pending")) {
                                    status = Reservation.prepStatus.PENDING;
                                } else if (reservationDB.getStatus().equals("Doing")) {
                                    status = Reservation.prepStatus.DOING;
                                } else {
                                    status = Reservation.prepStatus.DONE;
                                }
                                Reservation reservation = new Reservation(orderID, dishes,
                                        status, reservationDB.isAccepted(), reservationDB.getOrderTime(), reservationDB.getNameUser(),
                                        reservationDB.getNumberPhone(), reservationDB.getResNote(), null, sharedPreferences.getString("email", null),
                                        reservationDB.getUserAddress());

                                int index;
                                if(!reservation.getPreparationStatusString().toLowerCase().equals("pending")){
                                    Log.d("POSITIONDD","Added to doing");
                                    for(index = 0; index < pending_reservations.size(); index++){
                                        if(reservation.getOrderTime().compareTo(doing_reservations.get(index).getOrderTime()) > 0)
                                            break;
                                    }
                                    doing_reservations.add(index, reservation);
                                    adapterDoing.notifyItemInserted(index);
                                    adapterDoing.notifyItemRangeChanged(index, pending_reservations.size());
                                    //father.setNotification(1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                */

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*
                pending_reservations = jsonHandler.getReservations(filePending);
                pending_reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });
                pending_recycler.setAdapter(adapterPending);
                doing_reservations = jsonHandler.getReservations(fileDoing);
                doing_reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });
                doing_recycler.setAdapter(adapterDoing);*/

            }
        });





    }

    class ReservationBiker{
        private Reservation reservation;
        private BikerInfo biker;
        private String bikerID;

        public ReservationBiker(Reservation reservation, String bikerID){
            this.reservation = reservation;
            this.bikerID = bikerID;
        }

        public ReservationBiker(Reservation reservation){
            this.reservation = reservation;
            this.biker = null;
        }

        public boolean hasBiker(){
           return this.biker != null;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public void setReservation(Reservation reservation) {
            this.reservation = reservation;
        }

        public BikerInfo getBiker() {
            return biker;
        }

        public void setBiker(BikerInfo biker) {
            this.biker = biker;
        }

        public void fetchBiker(){
            if(bikerID.length() == 0)
                return;

            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
            Query query = database.child(bikerID).child("info");
        }
    }
}
