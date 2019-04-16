package com.example.foodyrestaurant;

import android.content.Context;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    /*@Override
    public long getItemId(int position) {
        return position;
    }*/

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder pvh, final int i) {
        /*pvh.title.setText(reservations.get(i).getReservationID());
        ArrayList<Dish> dishes = reservations.get(i).getDishesOrdered();
        Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (int j = 0; j < dishes.size(); j++){
            View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            TextView title = dish.findViewById(R.id.food_title_res);
            title.setText(dishes.get(j).getDishName());
            pvh.menuDishes.addView(dish);
        }

        if (i == 0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) pvh.cv.getLayoutParams();
            layoutParams.setMargins(0, getPixelValue(context,50), 0, getPixelValue(context,6));
            pvh.cv.requestLayout();
        }*/

        Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        final ArrayList<Dish> dishes = reservations.get(i).getDishesOrdered();

        pvh.idOrder.setText(reservations.get(i).getReservationID());
        pvh.status.setText(reservations.get(i).getPreparationStatusString());
        pvh.time.setText(reservations.get(i).getOrderTime());

        if(reservations.get(i).isAccepted() && reservations.get(i).getPreparationStatus() == Reservation.prepStatus.DOING) {
            pvh.accept.setVisibility(View.GONE);
            pvh.decline.setVisibility(View.GONE);
        }

        pvh.menuDishes.removeAllViews();

        for(int j = 0; j < dishes.size(); j++){
            final int toSet = j;
            View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(dishes.get(j).getStringForRes());
            foodTitle.setPaintFlags(0);
            foodTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reservations.get(i).getPreparationStatus() == Reservation.prepStatus.DOING && foodTitle.getPaintFlags() == 0) {
                        Log.d("MAD", "Here in true");
                        foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        dishes.get(toSet).setPrepared(true);
                    } else {
                        Log.d("MAD", "ELSE");
                        foodTitle.setPaintFlags(0);
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
                if(pvh.additionalLayout.getVisibility() == View.VISIBLE)
                    pvh.additionalLayout.setVisibility(View.GONE);
                else
                    pvh.additionalLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView idOrder;
        TextView time;
        TextView status;
        LinearLayout menuDishes;
        ConstraintLayout buttonLayout;
        MaterialButton accept;
        MaterialButton decline;
        FloatingActionButton plus;
        ConstraintLayout additionalLayout;
        TextView userName;

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
        }
    }
}