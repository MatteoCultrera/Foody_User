package com.example.foodyuser;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ReservationFragment extends Fragment {

    private RecyclerView reservationRecycler;
    private final String JSON_PATH = "reservations.json";
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Reservation> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private View thisView;
    private MainActivity father;
    private int pending;
    private int doing;
    private int done;

    public ReservationFragment() {
    }

    public void setFather(MainActivity father){
        this.father=father;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservationRecycler = view.findViewById(R.id.user_reservation_order_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thisView = view;
    }

    @Override
    public void onPause(){
        super.onPause();
        File file = new File(storageDir, JSON_PATH);
        if (reservations != null) {
            String json = jsonHandler.resToJSON(reservations);
            jsonHandler.saveStringToFile(json, file);
        }
    }

    private void init(View view){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(storageDir, JSON_PATH);


        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservationRecycler.setLayoutManager(llm);
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);

        pending = sharedPreferences.getInt("pending",0);
        doing = sharedPreferences.getInt("doing",0);
        done = sharedPreferences.getInt("done",0);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("users");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reservations = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBUser reservationDBUser = ds.getValue(ReservationDBUser.class);
                    ArrayList<Dish> dishes = new ArrayList<>();
                    for(OrderItem o : reservationDBUser.getDishesOrdered()){
                        Dish dish = new Dish();
                        dish.setQuantity(o.getPieces());
                        dish.setDishName(o.getOrderName());
                        dish.setPrice(o.getPrice());
                        dishes.add(dish);
                    }
                    Reservation.prepStatus status;
                    String orderID = reservationDBUser.getReservationID().substring(28);
                    if (reservationDBUser.getStatus().toLowerCase().equals("pending")){
                        status = Reservation.prepStatus.PENDING;
                    } else if (reservationDBUser.getStatus().toLowerCase().equals("doing")){
                        status = Reservation.prepStatus.DOING;
                    } else{
                        status = Reservation.prepStatus.DONE;
                    }
                    Reservation reservation = new Reservation(orderID, dishes, status,
                            reservationDBUser.isAccepted(), reservationDBUser.getOrderTime(), sharedPreferences.getString("name", ""),
                            sharedPreferences.getString("phoneNumber", ""), reservationDBUser.getResNote(), "",
                            sharedPreferences.getString("email", ""), sharedPreferences.getString("address", ""));
                    reservation.setRestaurantID(reservationDBUser.getRestaurantID());
                    reservation.setDeliveryTime(reservationDBUser.getOrderTime());
                    reservation.setRestaurantName(reservationDBUser.getRestaurantName());
                    reservation.setRestaurantAddress(reservationDBUser.getRestaurantAddress());
                    reservation.setTotalCost(reservationDBUser.getTotalCost());
                    reservations.add(reservation);
                }
                reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {

                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });

                final RVAdapterRes adapter = new RVAdapterRes(reservations);
                reservationRecycler.setAdapter(adapter);

                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int pending2 = 0;
                        int doing2 = 0;
                        int done2 = 0;
                        int index = 0;
                        if (reservations != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                for (DataSnapshot ds2 : ds.getChildren()) {

                                    ReservationDBUser reservationDBUser = ds2.getValue(ReservationDBUser.class);

                                    if (reservationDBUser.getStatus().toLowerCase().equals("pending")) {
                                        pending2++;
                                    } else if (reservationDBUser.getStatus().toLowerCase().equals("doing")) {
                                        if (reservationDBUser.getStatus().equals(reservations.get(index).getPreparationStatusString())) {
                                            reservations.get(index).setPreparationStatus(Reservation.prepStatus.DOING);
                                            adapter.notifyItemChanged(index);
                                        }
                                        doing2++;
                                    } else {
                                        if (reservationDBUser.getStatus().equals(reservations.get(index).getPreparationStatusString()))
                                            if (reservationDBUser.isAccepted()) {
                                                reservations.get(index).setPreparationStatus(Reservation.prepStatus.DONE);
                                                adapter.notifyItemChanged(index);
                                            } else {
                                                reservations.get(index).setPreparationStatus(Reservation.prepStatus.REJECTED);
                                            }
                                        done2++;
                                    }

                                    index++;
                                }
                            }

                            if (pending != pending2 || doing != doing2 || done != done2) {
                                sharedPreferences.edit().putInt("pending", pending2).apply();
                                sharedPreferences.edit().putInt("doing", doing2).apply();
                                sharedPreferences.edit().putInt("done", done2).apply();
                                father.setNotification(1);
                            }
                        }
                    }

                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError){

                        }

                });
               }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reservations = jsonHandler.getReservations(file);
                reservations.sort(new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getOrderTime().compareTo(o2.getOrderTime());
                    }
                });

                RVAdapterRes adapter = new RVAdapterRes(reservations);
                reservationRecycler.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        init(thisView);
    }
}
