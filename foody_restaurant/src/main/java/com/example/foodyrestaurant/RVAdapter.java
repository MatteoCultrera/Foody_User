package com.example.foodyrestaurant;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_card_display, viewGroup, false);
        CardViewHolder pvh = new CardViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        cardViewHolder.title.setText(cards.get(i).getTitle());
        ArrayList<Dish> dishes = cards.get(i).getDishes();
        Context context = cardViewHolder.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (int j = 0; j < dishes.size(); j++){
            View dish = inflater.inflate(R.layout.menu_item_display, cardViewHolder.menuDishes, false);
            TextView title = dish.findViewById(R.id.food_title);
            TextView subtitle = dish.findViewById(R.id.food_subtitle);
            TextView price = dish.findViewById(R.id.price);
            ImageView image = dish.findViewById(R.id.food_image);
            title.setText(dishes.get(j).getDishName());
            subtitle.setText(dishes.get(j).getDishDescription());
            price.setText(dishes.get(j).getPrice());
            if(dishes.get(j).getImage() == null)
                image.setVisibility(View.GONE);
            cardViewHolder.menuDishes.addView(dish);
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
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);
            menuDishes = (LinearLayout) itemView.findViewById(R.id.menu_dishes);
            outside = (ConstraintLayout) itemView.findViewById(R.id.outside);
        }
    }

}