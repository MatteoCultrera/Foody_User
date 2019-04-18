package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    private final List<Reservation> reservations;

    public RVAdapterRes(List<Reservation> reservations){
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
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder pvh, final int i) {
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
            foodTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reservations.get(i).getPreparationStatus() == Reservation.prepStatus.DOING) {
                        foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        reservations.get(i).incrementToBePrepared(1);
                        if(reservations.get(i).getToBePrepared() == 0 && reservations.get(i).isAccepted()) {
                            reservations.get(i).setPreparationStatus(Reservation.prepStatus.DONE);
                            pvh.status.setText(reservations.get(i).getPreparationStatusString());
                            if(pvh.additionalLayout.getVisibility() == View.GONE)
                                pvh.menuDishes.setVisibility(View.GONE);
                        }
                        dishes.get(toSet).setPrepared(true);
                    } else {
                        foodTitle.setPaintFlags(0);
                        if (reservations.get(i).isAccepted()) {
                            reservations.get(i).incrementToBePrepared(0);
                            reservations.get(i).setPreparationStatus(Reservation.prepStatus.DOING);
                            pvh.status.setText(reservations.get(i).getPreparationStatusString());
                        }
                        dishes.get(toSet).setPrepared(false);
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
            }
        });

        pvh.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservations.get(i).setAccepted(false);
                reservations.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, reservations.size());
            }
        });

        pvh.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvh.menuDishes.setVisibility(View.VISIBLE);
                if(pvh.additionalLayout.getVisibility() == View.VISIBLE) {
                    if (reservations.get(i).getToBePrepared() == 0)
                        pvh.menuDishes.setVisibility(View.GONE);
                    pvh.additionalLayout.setVisibility(View.GONE);
                    pvh.plus.setImageResource(R.drawable.expand_white);
                } else {
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