package com.example.foodyuser;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RVAdapterShowRestaurantMenu extends RecyclerView.Adapter<RVAdapterShowRestaurantMenu.CardViewHolder>{

    ArrayList<Card> cards;

    RVAdapterShowRestaurantMenu(ArrayList<Card> cards){
        this.cards = cards;

    }


    @Override
    public int getItemCount() {
        return cards.size();
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_card_display, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterShowRestaurantMenu.CardViewHolder pvh, int i) {
        Context context = pvh.cv.getContext();
        Card current = cards.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);

        pvh.title.setText(current.getTitle());

        pvh.menuDishes.removeAllViews();
        for(Dish d: current.getDishes()){
            if(d.isAvailable()){
                final View dish = inflater.inflate(R.layout.menu_item_display, pvh.menuDishes, false);
                final TextView title = dish.findViewById(R.id.food_title);
                TextView subtitle = dish.findViewById(R.id.food_subtitle);
                final TextView price = dish.findViewById(R.id.price);
                final ImageView image = dish.findViewById(R.id.food_image);
                ImageButton plus = dish.findViewById(R.id.button_plus);
                ImageButton minus = dish.findViewById(R.id.button_minus);
                TextView orderQuantity = dish.findViewById(R.id.order_quantity);

                title.setText(d.getDishName());
                if(d.getDishDescription().length() == 0)
                    subtitle.setVisibility(View.GONE);
                else{
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(d.getDishDescription());
                }
                price.setText(String.format(Locale.UK, "%.2f", d.getPrice()) + " €");

                if(d.getImage()==null)
                    image.setVisibility(View.GONE);
                else{
                    File imageFile = new File(d.getImage().getPath());
                    RequestOptions options = new RequestOptions();
                    options.signature(new ObjectKey(imageFile.getName()+" "+imageFile.lastModified()));
                    Glide
                            .with(pvh.cv.getContext())
                            .setDefaultRequestOptions(options)
                            .load(d.getImage())
                            .into(image);
                }

                pvh.menuDishes.addView(dish);
            }
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView title;
        final LinearLayout menuDishes;
        final ConstraintLayout outside;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            title = itemView.findViewById(R.id.title);
            menuDishes = itemView.findViewById(R.id.menu_dishes);
            outside = itemView.findViewById(R.id.outside);
        }
    }
}