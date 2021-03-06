package com.example.foodyrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class BikerFragment extends Fragment {

    private RecyclerView notAcceptedRecycler, acceptedRecycler;
    private TextView stringUp, stringDown;
    private ImageButton switchButton;
    private ArrayList<ReservationBiker> reservationList;
    private ArrayList<ReservationBiker> reservationAcceptedList;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private RVAdapterBiker adapterAccepted, adapterNotAccepted;
    private boolean onChooseSide;
    private final int CHOOSE_BIKER = 1;
    private int currentPosition;
    private View thisView;

    public BikerFragment() {}

    public void setFather(MainActivity father){
        MainActivity father1 = father;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        thisView = view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init(thisView);
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
                    if(!reservation.getPreparationStatusString().toLowerCase().equals("pending") && biker.equals("")){
                        reservationList.add(new ReservationBiker(reservation, reservationDB.isWaitingBiker(), reservationDB.getReservationID()));
                    }
                    if(!reservation.getPreparationStatusString().toLowerCase().equals("pending") && !biker.equals("")){
                        reservationAcceptedList.add(new ReservationBiker(reservation,biker, reservationDB.isWaitingBiker(), reservationDB.getReservationID()));
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

                for(int i = 0; i < reservationAcceptedList.size(); i++){
                    reservationAcceptedList.get(i).fetchBiker(i);
                }

                notAcceptedRecycler.setAdapter(adapterNotAccepted);
                acceptedRecycler.setAdapter(adapterAccepted);

                //notification
                DatabaseReference restaurantReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("restaurant").child(firebaseUser.getUid());
                restaurantReservations.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ReservationDBRestaurant reservationDB = dataSnapshot.getValue(ReservationDBRestaurant.class);
                        if(!dataSnapshot.child("attemptedBiker").exists()) {
                            if (reservationDB.isAccepted() && !reservationDB.isBiker()) {
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
                                int i;
                                for(i = 0; i < reservationList.size(); i++) {
                                    if (reservation.getOrderTime().compareTo(reservationList.get(i)
                                            .getReservation().getOrderTime()) > 0)
                                        break;
                                }

                                reservationList.add(i, new ReservationBiker(reservation, biker,
                                        reservationDB.isWaitingBiker(), reservationDB.getReservationID()));
                                adapterNotAccepted.notifyItemChanged(i);
                                adapterNotAccepted.notifyItemRangeChanged(i, reservationList.size());
                            }
                        }
                        String orderID = reservationDB.getReservationID().substring(28);
                        if(reservationDB.isBiker() && reservationDB.getBikerID().compareTo("") != 0){
                            bikerAccepted(orderID, reservationDB.getBikerID());
                        } else{
                            if(dataSnapshot.child("attemptedBiker").exists()) {
                                if (!reservationDB.isWaitingBiker() && reservationDB.getBikerID().compareTo("") == 0) {
                                    bikerRefused(orderID);
                                }
                            }
                        }
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        onChooseSide = true;
        updateInterface();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseSide = !onChooseSide;
                updateInterface();
            }
        });

    }

    private void updateInterface(){
        if(onChooseSide){
            stringUp.setText(getString(R.string.biker_choose));
            stringDown.setText(getString(R.string.biker_wait));
            notAcceptedRecycler.setVisibility(View.VISIBLE);
            acceptedRecycler.setVisibility(View.GONE);
        }else{
            stringUp.setText(getString(R.string.biker_wait));
            stringDown.setText(getString(R.string.biker_choose));
            notAcceptedRecycler.setVisibility(View.GONE);
            acceptedRecycler.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case CHOOSE_BIKER:
                if(resultCode == RESULT_OK){
                    String bikerId = data.getStringExtra("BikerID");
                    reservationList.get(currentPosition).bikerID = bikerId;
                    //TODO: gestire anche il caso dei biker che stanno accettando sul db
                    //ora è solo in locale
                    reservationList.get(currentPosition).waitingBiker = true;
                    adapterNotAccepted.notifyItemChanged(currentPosition);
                }
                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        final File storageImage = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        for(int i = 0; i < reservationAcceptedList.size(); i++){
            if(reservationAcceptedList.get(i).getBiker() != null){
                if(reservationAcceptedList.get(i).getBiker().getPath() != null){
                    File f = new File(storageImage, reservationAcceptedList.get(i).bikerID+".jpg");
                    if(f.exists())
                        f.delete();
                }
            }
        }
    }


    public void bikerAccepted(String reservationID, String bikerID){
        int i;
        for(i = 0; i < reservationList.size(); i++){
            if(reservationList.get(i).getReservation().getReservationID().equals(reservationID)) {
                break;
            }
        }
        if (i == reservationList.size()){
            return;
        }
        ReservationBiker res = reservationList.get(i);
        if(res == null)
            return;
        reservationList.remove(i);
        adapterNotAccepted.notifyItemRemoved(i);
        adapterNotAccepted.notifyItemRangeChanged(i, reservationList.size());
        res.bikerID = bikerID;
        res.fetchBiker(i);
        int j;
        for(j = 0; j < reservationAcceptedList.size(); j++){
            if(reservationAcceptedList.get(j).reservation.getOrderTime().compareTo(res.reservation.getOrderTime()) > 0)
                break;
        }
        reservationAcceptedList.add(j, res);
        adapterAccepted.notifyItemInserted(j);
        adapterAccepted.notifyItemRangeChanged(j, reservationAcceptedList.size());
    }

    public void bikerRefused(String reservationID){
        int i;
        for(i = 0; i < reservationList.size(); i++){
            if(reservationList.get(i).getReservation().getReservationID().equals(reservationID))
                break;
        }
        reservationList.get(i).waitingBiker = false;
        adapterNotAccepted.notifyItemChanged(i);
    }

    class ReservationBiker{
        private Reservation reservation;
        private BikerInfo biker;
        private String bikerID;
        private boolean waitingBiker;
        private String completeRes;

        ReservationBiker(Reservation reservation, String bikerID, boolean waitingBiker, String completeRes){
            this.reservation = reservation;
            this.bikerID = bikerID;
            this.waitingBiker = waitingBiker;
            this.completeRes = completeRes;
        }

        ReservationBiker(Reservation reservation, boolean waitingBiker, String completeRes){
            this.reservation = reservation;
            this.biker = null;
            this.waitingBiker = waitingBiker;
            this.completeRes = completeRes;
        }


        public String getBikerID(){
            return bikerID;
        }

        boolean hasBiker(){
           return this.biker != null;
        }

        Reservation getReservation() {
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

        void fetchBiker(final int pos){
            if(bikerID.length() == 0)
                return;

            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
            Query query = database.child(bikerID).child("info");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    BikerInfo info = dataSnapshot.getValue(BikerInfo.class);
                    String imagePath = info.getPath();
                    if(biker == null){
                        biker = new BikerInfo(info.getUsername(),
                                info.getEmail(), info.getAddress(),
                                info.getCity(), info.getNumberPhone(), info.getDaysTime());
                    }else{
                        biker.setUsername(info.getUsername());
                        biker.setEmail(info.getEmail());
                        biker.setAddress(info.getAddress());
                        biker.setCity(info.getCity());
                        biker.setNumberPhone(info.getNumberPhone());
                        biker.setDaysTime(info.getDaysTime());
                    }

                    if(imagePath!=null){
                        if(imagePath.length() > 0){
                            biker.setPath(imagePath);
                        }else{
                         biker.setPath(null);
                        }
                    }else{
                        biker.setPath(null);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    biker = null;
                    adapterAccepted.notifyItemChanged(pos);
                }
            });
        }

        boolean isWaitingBiker() {
            return waitingBiker;
        }

        public void setWaitingBiker(boolean waitingBiker) {
            this.waitingBiker = waitingBiker;
        }

        String getCompleteRes() {
            return completeRes;
        }

        public void setCompleteRes(String completeRes) {
            this.completeRes = completeRes;
        }

    }
}
