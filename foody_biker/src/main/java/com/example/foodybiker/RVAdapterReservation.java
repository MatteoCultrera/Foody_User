package com.example.foodybiker;

import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RVAdapterReservation extends RecyclerView.Adapter<RVAdapterReservation.CardViewHolder>{

    private List<Reservation> reservations;
    private ReservationFragment fatherFragment;
    private boolean orderActive;

    RVAdapterReservation(List<Reservation> reservations, ReservationFragment fatherFragment, boolean orderActive){
        this.reservations = reservations;
        this.fatherFragment = fatherFragment;
        this.orderActive = orderActive;
    }


    void setOrderActive(boolean orderActive) {
        this.orderActive = orderActive;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_item, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder pvh, final int i) {
        final Reservation currentRes = reservations.get(i);
        final  int pos = i;
        pvh.restaurantName.setText(currentRes.getRestaurantName());
        pvh.restaurantAddress.setText(currentRes.getRestaurantAddress());
        pvh.restaurantTime.setText(currentRes.getRestaurantPickupTime());
        pvh.userName.setText(currentRes.getUserName());
        pvh.userAddress.setText(currentRes.getUserAddress());
        pvh.userTime.setText(currentRes.getUserDeliveryTime());

        if(orderActive){
            pvh.accept.setVisibility(View.GONE);
            pvh.decline.setVisibility(View.GONE);
            pvh.lastDiv.setVisibility(View.INVISIBLE);
        }else{
            pvh.accept.setVisibility(View.VISIBLE);
            pvh.decline.setVisibility(View.VISIBLE);
            pvh.lastDiv.setVisibility(View.VISIBLE);

            pvh.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fatherFragment.setActiveReservation(currentRes);
                    fatherFragment.removeItem(pos);
                    fatherFragment.updateTitles();

                    /*DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child("reservations").child("Bikers").child(reservations.get(i).get);
                    HashMap<String, Object> child = new HashMap<>();
                    ArrayList<OrderItem> dishes = new ArrayList<>();
                    for(Dish d : reservations.get(i).getDishesOrdered()){
                        OrderItem order = new OrderItem();
                        order.setPieces(d.getQuantity());
                        order.setOrderName(d.getDishName());
                        order.setPrice(d.getPrice());
                        dishes.add(order);
                    }
                    String reservationID = reservations.get(i).getUserUID() + reservations.get(i).getReservationID();
                    ReservationDBUser reservation = new ReservationDBUser(reservationID,
                            firebaseUser.getUid(), dishes, true, null, reservations.get(i).getDeliveryTime(),
                            "doing", reservations.get(i).getTotalPrice());
                    child.put(reservationID, reservation);
                    database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            });

            pvh.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fatherFragment.removeItem(pos);
                    fatherFragment.updateTitles();
                }
            });
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName, restaurantAddress, restaurantTime, userName, userAddress, userTime;
        View lastDiv;
        MaterialButton accept, decline;

        CardViewHolder(View itemView) {
            super(itemView);
            restaurantName =  itemView.findViewById(R.id.pending_order_restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.pending_order_restaurant_address);
            restaurantTime = itemView.findViewById(R.id.pending_order_pickup_time);
            userName = itemView.findViewById(R.id.pending_order_user_name);
            userAddress = itemView.findViewById(R.id.pending_order_user_address);
            userTime = itemView.findViewById(R.id.pending_order_deliver_time);
            lastDiv = itemView.findViewById(R.id.pending_order_user_div);
            accept = itemView.findViewById(R.id.pending_order_accept);
            decline = itemView.findViewById(R.id.pending_order_decline);

            CardView cv = itemView.findViewById(R.id.pending_order_card);
            ConstraintLayout layout = itemView.findViewById(R.id.pending_order_main_layout);

            cv.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        }
    }
}