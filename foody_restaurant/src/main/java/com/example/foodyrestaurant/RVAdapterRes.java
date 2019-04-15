package com.example.foodyrestaurant;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;
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

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder resvh, int i) {
        Context context = resvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = resvh.getAdapterPosition();

        ArrayList<Dish> dishes = reservations.get(pos).getDishesOrdered();

        for(int j = 0; j < dishes.size(); j++) {
            Log.d("MAD", "1 ");
            View singleDish = inflater.inflate(R.layout.reservation_item_display, resvh.cons, false);
            Log.d("MAD", "2 ");
            TextView title = singleDish.findViewById(R.id.food_title_res);
            Log.d("MAD", "3 ");
            CheckBox checkBox = singleDish.findViewById(R.id.checkbox);
            Log.d("MAD", "4 ");
            resvh.pickTime.setText(reservations.get(pos).getOrderTime());
            title.setText(dishes.get(j).getDishName());
            Log.d("MAD", "5 ");
            checkBox.setChecked(false);
            Log.d("MAD", "6 ");
            Log.d("MAD", "7 ");
            dishes.get(j).setAdded(true);
            Log.d("MAD", "8 ");
        }

        resvh.orderID.setText(reservations.get(pos).getReservationID());

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        final CardView cv;
        final ImageButton callUser;
        final TextView orderID;
        final TextView pickTime;
        final TextView notes;
        final Button accept;
        final Button decline;
        final LinearLayout cons;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.card_view);
            callUser = itemView.findViewById(R.id.call_user);
            orderID = itemView.findViewById(R.id.user_id);
            pickTime = itemView.findViewById(R.id.pickup_time);
            notes = itemView.findViewById(R.id.notes);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            cons = itemView.findViewById(R.id.linearLayout);
        }
    }

}