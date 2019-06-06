package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    private final List<Reservation> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private ReservationFragment fatherClass;

    RVAdapterRes(List<Reservation> reservations, ReservationFragment fatherClass){
        this.reservations = reservations;
        this.fatherClass = fatherClass;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = viewGroup.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder pvh, int i) {
        final Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = pvh.getAdapterPosition();

        final ArrayList<Dish> dishes = reservations.get(pos).getDishesOrdered();

        String resId = reservations.get(i).getUserUID();
        String first2Letters = "";
        for(char c : resId.toCharArray()){
            if(first2Letters.length() == 2)
                break;
            else{
                if(Character.isAlphabetic(c))
                    first2Letters += c;
            }
        }
        int id_size = reservations.get(i).getReservationID().length();
        String lastFour = reservations.get(i).getReservationID().substring(id_size-4,id_size);
        String orderId = (first2Letters + lastFour).toUpperCase();
        pvh.idOrder.setText(orderId);
        pvh.status.setText(reservations.get(i).getPreparationStatusString());
        pvh.time.setText(reservations.get(i).getOrderTime());
        pvh.userName.setText(reservations.get(i).getUserName());

        if(reservations.get(i).getResNote() == null) {
            pvh.notePlaceholder.setVisibility(View.GONE);
            pvh.notes.setVisibility(View.GONE);
            pvh.separatorInfoNote.setVisibility(View.GONE);
        } else {
            pvh.notes.setText(reservations.get(i).getResNote());
        }

        if(reservations.get(i).isAccepted()) {
            pvh.accept.setVisibility(View.GONE);
            pvh.decline.setVisibility(View.GONE);
        }

        if(reservations.get(i).getPreparationStatus() != Reservation.prepStatus.DONE || pvh.additionalLayout.getVisibility() == View.GONE){
            pvh.hintLayout.setVisibility(View.GONE);
        }else{
            pvh.hintLayout.setVisibility(View.VISIBLE);
        }

        pvh.menuDishes.removeAllViews();

        for(int j = 0; j < dishes.size(); j++){
            final int toSet = j;
            final View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(dishes.get(j).getStringForRes());

            if(dishes.get(j).isPrepared())
                foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            foodTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reservations.get(pos).isAccepted()) {
                        if(dishes.get(toSet).isPrepared()) {
                            foodTitle.setPaintFlags(0);
                            reservations.get(pos).incrementToBePrepared();
                            dishes.get(toSet).setPrepared(false);
                            reservations.get(pos).setPreparationStatus(Reservation.prepStatus.DOING);
                            pvh.status.setText(reservations.get(pos).getPreparationStatusString());
                            pvh.hintLayout.setVisibility(View.GONE);
                        } else {
                            foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            reservations.get(pos).incrementDishDone();
                            dishes.get(toSet).setPrepared(true);
                            if(reservations.get(pos).getToBePrepared() == 0) {
                                reservations.get(pos).setPreparationStatus(Reservation.prepStatus.DONE);
                                pvh.status.setText(reservations.get(pos).getPreparationStatusString());
                                if(pvh.additionalLayout.getVisibility() == View.VISIBLE){
                                    pvh.hintLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            });
            pvh.menuDishes.addView(dish);
        }

        pvh.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                pvh.buttonLayout.setVisibility(View.GONE);
                reservations.get(pos).setAccepted(true);
                reservations.get(pos).setPreparationStatus(Reservation.prepStatus.DOING);
                pvh.status.setText(reservations.get(pos).getPreparationStatusString());
                */

                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(reservations.get(pos).getUserUID());
                HashMap<String, Object> child = new HashMap<>();
                ArrayList<OrderItem> dishes = new ArrayList<>();
                for(Dish d : reservations.get(pos).getDishesOrdered()){
                    OrderItem order = new OrderItem();
                    order.setPieces(d.getQuantity());
                    order.setOrderName(d.getDishName());
                    order.setPrice(d.getPrice());
                    dishes.add(order);
                }
                String reservationID = reservations.get(pos).getUserUID() + reservations.get(pos).getReservationID();
                ReservationDBUser reservation = new ReservationDBUser(reservationID,
                        firebaseUser.getUid(), dishes, true, reservations.get(pos).getResNote(), reservations.get(pos).getDeliveryTime(),
                        "Doing", reservations.get(pos).getTotalPrice());
                reservation.setRestaurantName(sharedPreferences.getString("name", null));
                reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
                child.put(reservationID, reservation);
                database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("restaurant").child(firebaseUser.getUid());
                HashMap<String, Object> childRest = new HashMap<>();
                ReservationDBRestaurant reservationRest = new ReservationDBRestaurant(reservationID,
                        "", dishes, true, reservations.get(pos).getResNote(), reservations.get(pos).getUserPhone(),
                        reservations.get(pos).getUserName(), reservations.get(pos).getDeliveryTime(),
                        reservations.get(pos).getOrderTime(), "Doing", reservations.get(pos).getUserAddress(), reservations.get(pos).getTotalPrice());
                reservationRest.setWaitingBiker(false);
                reservationRest.setBiker(false);

                sharedPreferences.edit().putString("userPhone", reservations.get(pos).getUserPhone()).apply();
                childRest.put(reservationID, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                Reservation toSwap = reservations.get(pos);
                toSwap.setAccepted(true);
                toSwap.setPreparationStatus(Reservation.prepStatus.DOING);
                fatherClass.addInDoing(toSwap);
                reservations.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, reservations.size());
            }
        });

        pvh.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(reservations.get(pos).getUserUID());
                HashMap<String, Object> child = new HashMap<>();
                final ArrayList<OrderItem> dishes = new ArrayList<>();
                for(Dish d : reservations.get(pos).getDishesOrdered()){
                    OrderItem order = new OrderItem();
                    order.setPieces(d.getQuantity());
                    order.setOrderName(d.getDishName());
                    order.setPrice(d.getPrice());
                    dishes.add(order);
                }
                final String reservationID = reservations.get(pos).getUserUID() + reservations.get(pos).getReservationID();
                ReservationDBUser reservation = new ReservationDBUser(reservationID,
                        firebaseUser.getUid(), dishes, false, reservations.get(pos).getResNote(),
                        reservations.get(pos).getDeliveryTime(), "Done", reservations.get(pos).getTotalPrice());
                reservation.setRestaurantAddress(sharedPreferences.getString("address", null));
                reservation.setRestaurantName(sharedPreferences.getString("name", null));
                child.put(reservationID, reservation);
                database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                Calendar calendar = Calendar.getInstance();
                String monthYear = calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                        .child("archive").child("restaurant").child(firebaseUser.getUid()).child(monthYear);
                HashMap<String, Object> childRest = new HashMap<>();
                final ReservationDBRestaurant reservationRest = new ReservationDBRestaurant(reservationID,
                        "", dishes, false, reservations.get(pos).getResNote(), reservations.get(pos).getUserPhone(),
                        reservations.get(pos).getUserName(), reservations.get(pos).getDeliveryTime(),
                        reservations.get(pos).getOrderTime(), "Done",
                        reservations.get(pos).getUserAddress(), reservations.get(pos).getTotalPrice());
                childRest.put(reservationID, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final DatabaseReference databaseDish = FirebaseDatabase.getInstance().getReference()
                                .child("archive").child("restaurant").child(firebaseUser.getUid()).child("dishesCount");
                        databaseDish.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    HashMap<String, Object> frequencies = new HashMap<>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        frequencies.put(ds.getKey(), ds.getValue(Integer.class));
                                    }
                                    for (int i = 0; i < dishes.size(); i++) {
                                        if(frequencies.containsKey(dishes.get(i).getOrderName())){
                                            Integer count = (Integer) frequencies.get(dishes.get(i).getOrderName());
                                            count += dishes.get(i).getPieces();
                                            frequencies.put(dishes.get(i).getOrderName(), count);
                                        }
                                    }
                                    databaseDish.updateChildren(frequencies);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        final DatabaseReference databaseFreq = FirebaseDatabase.getInstance().getReference()
                                .child("archive").child("restaurant").child(firebaseUser.getUid()).child("frequency");
                        databaseFreq.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    HashMap<String, Object> frequencies = new HashMap<>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        frequencies.put(ds.getKey(), ds.getValue(Integer.class));
                                    }

                                    String hour = reservationRest.getOrderTime().split(":")[0];
                                    Integer count = (Integer) frequencies.get(hour) + 1;
                                    frequencies.put(hour, count);
                                    databaseFreq.updateChildren(frequencies);
                                } else {
                                    HashMap<String, Object> frequencies = new HashMap<>();
                                    for (int i = 0; i < 24; i ++){
                                        frequencies.put(String.valueOf(i), 0);
                                    }

                                    String hour = reservationRest.getOrderTime().split(":")[0];
                                    Integer count = (Integer) frequencies.get(hour) + 1;
                                    frequencies.put(hour, count);
                                    databaseFreq.updateChildren(frequencies);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        final DatabaseReference databaseRej = FirebaseDatabase.getInstance().getReference()
                                .child("archive").child("restaurant").child(firebaseUser.getUid()).child("rejected");
                        databaseRej.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int count = dataSnapshot.getValue(int.class);
                                    count ++;
                                    databaseRej.setValue(count);
                                } else {
                                    databaseRej.setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        DatabaseReference databaseDelete = FirebaseDatabase.getInstance().getReference()
                                .child("archive").child("restaurant").child(firebaseUser.getUid()).child(reservationID);
                        databaseDelete.removeValue();

                        reservations.get(pos).setAccepted(false);
                        reservations.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, reservations.size());
                    }
                });
            }
        });


        pvh.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pvh.additionalLayout.getVisibility() == View.VISIBLE) {
                    if(reservations.get(pos).getPreparationStatus() == Reservation.prepStatus.DONE){
                        pvh.hintLayout.setVisibility(View.GONE);
                    }
                    pvh.additionalLayout.setVisibility(View.GONE);
                    pvh.plus.setImageResource(R.drawable.expand_white);
                } else {
                    pvh.additionalLayout.setVisibility(View.VISIBLE);
                    pvh.plus.setImageResource(R.drawable.collapse_white);
                    if(reservations.get(pos).getPreparationStatus() == Reservation.prepStatus.DONE){
                        pvh.hintLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        pvh.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+reservations.get(pos).getUserPhone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {
        final CardView cv;
        final TextView idOrder;
        final TextView time;
        final TextView status;
        final LinearLayout menuDishes;
        final ConstraintLayout buttonLayout;
        final MaterialButton accept;
        final MaterialButton decline;
        final FloatingActionButton plus;
        final ConstraintLayout additionalLayout;
        final TextView userName;
        final TextView notes;
        final AppCompatImageButton phone;
        final TextView notePlaceholder;
        final View separatorInfoNote;
        final ConstraintLayout layoutCard, hintLayout;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            idOrder = itemView.findViewById(R.id.idOrder);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            menuDishes = itemView.findViewById(R.id.orderList);
            buttonLayout = itemView.findViewById(R.id.buttonLayout);
            accept = itemView.findViewById(R.id.acceptOrder);
            decline = itemView.findViewById(R.id.declineOrder);
            plus = itemView.findViewById(R.id.plusReservation);
            additionalLayout = itemView.findViewById(R.id.constrGone);
            userName = itemView.findViewById(R.id.user_name);
            notes = itemView.findViewById(R.id.note_text);
            phone = itemView.findViewById(R.id.call_user);
            notePlaceholder = itemView.findViewById(R.id.note_placeholder);
            separatorInfoNote = itemView.findViewById(R.id.separator2);
            layoutCard = itemView.findViewById(R.id.layout_card_reservation);
            hintLayout = itemView.findViewById(R.id.hint_layout);
        }
    }
}