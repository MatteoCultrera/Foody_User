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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    private final List<Reservation> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    RVAdapterRes(List<Reservation> reservations){
        this.reservations = reservations;
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
    public void onBindViewHolder(@NonNull final CardViewHolder pvh,final int i) {
        final Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = pvh.getAdapterPosition();

        final ArrayList<Dish> dishes = reservations.get(pos).getDishesOrdered();

        pvh.idOrder.setText(reservations.get(i).getReservationID());
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

        pvh.menuDishes.removeAllViews();

        for(int j = 0; j < dishes.size(); j++){
            final int toSet = j;
            final View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(dishes.get(j).getStringForRes());

            if(reservations.get(i).getPreparationStatus() == Reservation.prepStatus.DONE)
                pvh.menuDishes.setVisibility(View.GONE);
            if(dishes.get(j).isPrepared())
                foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            foodTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reservations.get(i).isAccepted()) {
                        if(dishes.get(toSet).isPrepared()) {
                            foodTitle.setPaintFlags(0);
                            reservations.get(i).incrementToBePrepared();
                            dishes.get(toSet).setPrepared(false);
                            reservations.get(i).setPreparationStatus(Reservation.prepStatus.DOING);
                            pvh.status.setText(reservations.get(i).getPreparationStatusString());
                        } else {
                            foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            reservations.get(i).incrementDishDone();
                            dishes.get(toSet).setPrepared(true);
                            if(reservations.get(i).getToBePrepared() == 0) {
                                reservations.get(i).setPreparationStatus(Reservation.prepStatus.DONE);
                                pvh.status.setText(reservations.get(i).getPreparationStatusString());
                                pvh.menuDishes.setVisibility(View.GONE);
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
                pvh.buttonLayout.setVisibility(View.GONE);
                reservations.get(i).setAccepted(true);
                reservations.get(i).setPreparationStatus(Reservation.prepStatus.DOING);
                pvh.status.setText(reservations.get(i).getPreparationStatusString());

                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(reservations.get(i).getUserUID());
                HashMap<String, Object> child = new HashMap<>();
                ArrayList<OrderItem> dishes = new ArrayList<>();
                for(Dish d : reservations.get(i).getDishesOrdered()){
                    OrderItem order = new OrderItem();
                    order.setPieces(d.getQuantity());
                    order.setOrderName(d.getDishName());
                    dishes.add(order);
                }
                String reservationID = reservations.get(i).getUserUID() + reservations.get(i).getReservationID();
                ReservationDBUser reservation = new ReservationDBUser(reservationID,
                        firebaseUser.getUid(), dishes, true, null, reservations.get(i).getDeliveryTime(),
                        "doing");
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
                        "", dishes, true, null, reservations.get(i).getUserPhone(),
                        reservations.get(i).getUserName(), reservations.get(i).getDeliveryTime(),
                        reservations.get(i).getOrderTime(), "doing", reservations.get(i).getUserAddress());
                childRest.put(reservationID, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference databaseBiker = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("Bikers").child("pYlVkIDv53f4RAxAJgCCwPPiuoz2");
                HashMap<String, Object> childBiker = new HashMap<>();
                ReservationDBBiker reservationBiker = new ReservationDBBiker(reservationID, reservations.get(i).getDeliveryTime(),
                        reservations.get(i).getOrderTime(), sharedPreferences.getString("name", null),
                        reservations.get(i).getUserName(), sharedPreferences.getString("address", null),
                        reservations.get(i).getUserAddress());
                childBiker.put(reservationID, reservationBiker);
                databaseBiker.updateChildren(childBiker).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        pvh.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(reservations.get(i).getUserUID());
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
                        firebaseUser.getUid(), dishes, false, null, null, "done");
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
                        "", dishes, false, null, reservations.get(i).getUserPhone(),
                        reservations.get(i).getUserName(), null, null, "done",
                        reservations.get(i).getUserAddress());
                childRest.put(reservationID, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                reservations.get(i).setAccepted(false);
                reservations.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, reservations.size());
            }
        });

        pvh.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pvh.additionalLayout.getVisibility() == View.VISIBLE) {
                    if(reservations.get(i).getPreparationStatus() == Reservation.prepStatus.DONE)
                        pvh.menuDishes.setVisibility(View.GONE);
                    pvh.additionalLayout.setVisibility(View.GONE);
                    pvh.plus.setImageResource(R.drawable.expand_white);
                } else {
                    pvh.menuDishes.setVisibility(View.VISIBLE);
                    pvh.additionalLayout.setVisibility(View.VISIBLE);
                    pvh.plus.setImageResource(R.drawable.collapse_white);
                }
            }
        });

        pvh.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+reservations.get(i).getUserPhone()));
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
        }
    }
}