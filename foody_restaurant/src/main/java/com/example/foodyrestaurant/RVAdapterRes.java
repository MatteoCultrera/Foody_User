package com.example.foodyrestaurant;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder pvh, int i) {
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

        ArrayList<Dish> dishes = reservations.get(i).getDishesOrdered();

        pvh.idOrder.setText(reservations.get(i).getReservationID());
        pvh.status.setText(reservations.get(i).getPreparationStatusString());
        pvh.time.setText(reservations.get(i).getOrderTime());

        pvh.menuDishes.removeAllViews();

        for(int j = 0; j < dishes.size(); j++){
            View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(dishes.get(j).getStringForRes());
            pvh.menuDishes.addView(dish);
        }




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

        CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            idOrder = itemView.findViewById(R.id.idOrder);
            menuDishes = itemView.findViewById(R.id.orderList);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
        }
    }

}