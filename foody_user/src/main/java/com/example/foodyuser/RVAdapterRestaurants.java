package com.example.foodyuser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RVAdapterRestaurants  extends RecyclerView.Adapter<RVAdapterRestaurants.CardViewHolder>{

    private ArrayList<Restaurant> restaurants;

    RVAdapterRestaurants(ArrayList<Restaurant> restaurants){
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_card_display, viewGroup, false);

        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {

        cardViewHolder.restaurantName.setText(restaurants.get(i).getName());
        cardViewHolder.restaurantDescription.setText(restaurants.get(i).getKitchensString());
        cardViewHolder.restaurantDeliveryPrice.setText(restaurants.get(i).getDeliveryPriceString());
        cardViewHolder.restaurantDistance.setText(restaurants.get(i).getDistanceString());

    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        ImageView restaurantBackground;
        TextView restaurantName;
        TextView restaurantDescription;
        TextView restaurantDeliveryPrice;
        TextView restaurantDistance;

        CardViewHolder(View itemView) {
            super(itemView);
            restaurantBackground = itemView.findViewById(R.id.restaurant_background);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantDescription = itemView.findViewById(R.id.restaurant_description);
            restaurantDeliveryPrice = itemView.findViewById(R.id.restaurant_delivery_price);
            restaurantDistance = itemView.findViewById(R.id.restaurant_distance);
        }
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<Restaurant> filtedNames) {
        this.restaurants = filtedNames;
        notifyDataSetChanged();
    }
}
