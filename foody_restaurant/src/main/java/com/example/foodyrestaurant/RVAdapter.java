package com.example.foodyrestaurant;

import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder>{

    List<Card> cards;

    public RVAdapter(List<Card> cards){
        this.cards = cards;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_card_display, viewGroup, false);
        CardViewHolder pvh = new CardViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder pvh, int i) {

        Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = pvh.getAdapterPosition();

        ArrayList<Dish> dishes = cards.get(pos).getDishes();

        pvh.menuDishes.removeAllViews();

            for (int j = 0; j < dishes.size(); j++){
                View dish = inflater.inflate(R.layout.menu_item_display, pvh.menuDishes, false);
                TextView title = dish.findViewById(R.id.food_title);
                final TextView titleF = dish.findViewById(R.id.food_title);
                TextView subtitle = dish.findViewById(R.id.food_subtitle);
                final TextView subtitleF = dish.findViewById(R.id.food_subtitle);
                TextView price = dish.findViewById(R.id.price);
                final TextView priceF = dish.findViewById(R.id.price);
                ImageView image = dish.findViewById(R.id.food_image);
                title.setText(dishes.get(j).getDishName());
                subtitle.setText(dishes.get(j).getDishDescription());
                price.setText(dishes.get(j).getPrice());
                if(dishes.get(j).getImage() == null)
                    image.setVisibility(View.GONE);
                pvh.menuDishes.addView(dish);
                dishes.get(j).setAdded(true);
                final Switch enabler = dish.findViewById(R.id.enabler);
                enabler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (!b) {
                            titleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.errorColor));
                            subtitleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.errorColor));
                            priceF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.errorColor));
                        }
                        else {
                            titleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                            subtitleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.secondaryText));
                            priceF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                        }
                    }
                });
            }


        pvh.title.setText(cards.get(pos).getTitle());

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
        boolean isInflated;

        public CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            title = itemView.findViewById(R.id.title);
            menuDishes = itemView.findViewById(R.id.menu_dishes);
            outside = itemView.findViewById(R.id.outside);
            isInflated = false;
        }
    }

}