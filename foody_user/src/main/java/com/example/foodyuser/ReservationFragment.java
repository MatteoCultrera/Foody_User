package com.example.foodyuser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
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
    private FloatingActionButton button;
    private int pending;
    private int doing;
    private int done;
    private RVAdapterRes adapter;

    public ReservationFragment() {
    }

    public void setFather(MainActivity father){
        this.father=father;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        reservationRecycler = view.findViewById(R.id.user_reservation_order_list);
        button = view.findViewById(R.id.button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thisView = view;
        init(view);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRejected();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        reservationRecycler.setLayoutManager(llm);
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);

        pending = sharedPreferences.getInt("pending",0);
        doing = sharedPreferences.getInt("doing",0);
        done = sharedPreferences.getInt("done",0);

        new Thread(new Runnable() {
            @Override
            public void run() {
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

                        adapter = new RVAdapterRes(reservations);
                        reservationRecycler.setAdapter(adapter);

                        database.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int pending2 = 0;
                                int doing2 = 0;
                                int done2 = 0;
                                int index;
                                if (reservations != null) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        index = 0;
                                        int i;
                                        ReservationDBUser reservationDBUser = ds.getValue(ReservationDBUser.class);
                                        String orderID = reservationDBUser.getReservationID().substring(28);
                                        for (i = 0; i < reservations.size(); i++){
                                            if (reservations.get(i).getReservationID().compareTo(orderID) == 0){
                                                index = i;
                                                break;
                                            }
                                        }
                                        if (i == reservations.size()){
                                            for (index = 0; index < reservations.size(); index++){
                                                if (reservationDBUser.getOrderTime().compareTo(reservations.get(index).getOrderTime()) > 0){
                                                    break;
                                                }
                                            }
                                            ArrayList<Dish> dishes = new ArrayList<>();
                                            for(OrderItem o : reservationDBUser.getDishesOrdered()){
                                                Dish dish = new Dish();
                                                dish.setQuantity(o.getPieces());
                                                dish.setDishName(o.getOrderName());
                                                dish.setPrice(o.getPrice());
                                                dishes.add(dish);
                                            }
                                            Reservation.prepStatus status;
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
                                            reservations.add(index,reservation);
                                            adapter.notifyItemInserted(index);
                                            adapter.notifyItemRangeChanged(index, reservations.size());
                                            pending2++;
                                        } else if (reservationDBUser.getStatus().toLowerCase().equals("doing")) {
                                            reservations.get(index).setPreparationStatus(Reservation.prepStatus.DOING);
                                            adapter.notifyItemChanged(index);
                                            adapter.notifyItemRangeChanged(index, reservations.size());
                                            doing2++;
                                        } else if (reservationDBUser.getStatus().toLowerCase().equals("done")){
                                            if (reservationDBUser.isAccepted()) {
                                                reservations.get(index).setPreparationStatus(Reservation.prepStatus.DONE);
                                                adapter.notifyItemChanged(index);
                                                adapter.notifyItemRangeChanged(index, reservations.size());
                                            } else {
                                                reservations.get(index).setPreparationStatus(Reservation.prepStatus.REJECTED);
                                            }
                                            done2++;
                                        } else {
                                            pending2++;
                                        }
                                    }
                                    if (pending != pending2 || doing != doing2 || done != done2) {
                                        pending = pending2;
                                        doing = doing2;
                                        done = done2;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){
                                //TODO: toast internet not available / internet error
                            }

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //TODO: toast internet not available / internet error
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void removeOrder(String Id){
        int index = 0;
        for(Reservation r : reservations){
            if (Id.contains(r.getReservationID())) {
                reservations.remove(r);
                adapter.notifyItemRemoved(index);
                adapter.notifyItemRangeChanged(index, reservations.size());
                break;
            }
            index++;
        }
    }

    public void removeRejected(){
        int index = 0;
        for(Reservation r : reservations){
            if(r.getPreparationStatusString().equals("Rejected")){
                reservations.remove(r);
                adapter.notifyItemRemoved(index);
                adapter.notifyItemRangeChanged(index, reservations.size());

                Calendar calendar = Calendar.getInstance();
                String monthYear = calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
                DatabaseReference databaseArc = FirebaseDatabase.getInstance().getReference()
                        .child("archive").child("user").child(firebaseUser.getUid()).child(monthYear);
                String orderId = firebaseUser.getUid() + r.getReservationID();
                ArrayList<OrderItem> dishes = new ArrayList<>();
                for(Dish d : r.getDishesOrdered()){
                    OrderItem dish = new OrderItem();
                    dish.setPieces(d.getQuantity());
                    dish.setOrderName(d.getDishName());
                    dish.setPrice(d.getPrice());
                    dishes.add(dish);
                }
                ReservationDBUser reservationDBUser = new ReservationDBUser(orderId, r.getRestaurantID(), dishes,
                        false, r.getResNote(), r.getOrderTime(), r.getPreparationStatusString(), r.getTotalCost());
                reservationDBUser.setRestaurantName(r.getRestaurantName());
                reservationDBUser.setRestaurantAddress(r.getRestaurantAddress());
                HashMap<String, Object> childSelf = new HashMap<>();
                childSelf.put(orderId, reservationDBUser);
                databaseArc.updateChildren(childSelf);

                DatabaseReference databaseRemove = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(firebaseUser.getUid());
                databaseRemove.child(orderId).removeValue();
            }
            index++;
        }
    }
}
