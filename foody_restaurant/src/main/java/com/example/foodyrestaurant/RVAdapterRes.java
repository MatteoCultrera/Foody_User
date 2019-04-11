package com.example.foodyrestaurant;

import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    List<Reservation> reservations;

    public RVAdapterRes(List<Reservation> reservations){
        this.reservations = reservations;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_card_display, viewGroup, false);
        CardViewHolder pvh = new CardViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        cardViewHolder.title.setText(reservations.get(i).getReservationID());
        ArrayList<Dish> dishes = reservations.get(i).getDishesOrdered();
        Context context = cardViewHolder.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (int j = 0; j < dishes.size(); j++){
            View dish = inflater.inflate(R.layout.reservation_item_display, cardViewHolder.menuDishes, false);
            TextView title = dish.findViewById(R.id.food_title_res);
            title.setText(dishes.get(j).getDishName());
            cardViewHolder.menuDishes.addView(dish);
        }

        if (i == 0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) cardViewHolder.cv.getLayoutParams();
            layoutParams.setMargins(0, getPixelValue(context,50), 0, getPixelValue(context,6));
            cardViewHolder.cv.requestLayout();
        }

    }

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        LinearLayout menuDishes;
        ConstraintLayout outside;

        public CardViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            title = itemView.findViewById(R.id.title);
            menuDishes = itemView.findViewById(R.id.menu_dishes);
            outside = itemView.findViewById(R.id.outside);
        }
    }

}