package com.example.foodyuser;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterRes extends RecyclerView.Adapter<RVAdapterRes.CardViewHolder>{

    private final List<Reservation> reservations;
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
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder pvh,final int i) {

        Reservation currentRes = reservations.get(i);
        LayoutInflater inflater = LayoutInflater.from(pvh.restName.getContext());

        pvh.restName.setText(currentRes.getRestaurantName());
        pvh.restAddress.setText(currentRes.getRestaurantAddress());

        ArrayList<Dish> dishes = currentRes.getDishesOrdered();
        pvh.dishes.removeAllViews();
        if(dishes!=null){
            for(Dish d: dishes){

                View dish = inflater.inflate(R.layout.reservation_item_display_current_order, pvh.dishes, false);
                TextView name = dish.findViewById(R.id.current_item_food_name);
                TextView price = dish.findViewById(R.id.current_item_food_price);

                name.setText(d.getQuantity()+" x "+d.getDishName());
                float priceFloat = d.getPrice()*d.getQuantity();
                price.setText(String.format("%.2f €", priceFloat));

                pvh.dishes.addView(dish);
            }
        }



        Resources resources = pvh.status.getContext().getResources();
        String status;
        switch(currentRes.getPreparationStatusString().toLowerCase()){
            case("pending"):
                status = resources.getString(R.string.pending);
                break;
            case("done"):
                status = resources.getString(R.string.done);
                break;
            case("doing"):
                status = resources.getString(R.string.doing);
                break;
            default:
                status = "";
                break;
        }
        pvh.status.setText(String.format(resources.getString(R.string.order_status), status));
        pvh.total.setText(currentRes.getTotalCost());
        pvh.deliveryTime.setText(currentRes.getDeliveryTime());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        TextView restName, restAddress, total, deliveryTime, status;
        LinearLayout dishes;

        CardViewHolder(View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.user_reservation_restaurant_image);
            restName = itemView.findViewById(R.id.user_reservation_restaurant_name);
            restAddress = itemView.findViewById(R.id.user_reservation_restaurant_address);
            total = itemView.findViewById(R.id.user_reservation_total);
            deliveryTime = itemView.findViewById(R.id.user_reservation_delivery_time);
            dishes = itemView.findViewById(R.id.user_reservation_dish_list);
            status = itemView.findViewById(R.id.user_reservation_order_status_string);
        }
    }
}