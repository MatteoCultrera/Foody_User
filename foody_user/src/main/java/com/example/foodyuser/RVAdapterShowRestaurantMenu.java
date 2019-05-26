package com.example.foodyuser;

import android.animation.LayoutTransition;
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
    RestaurantView grandFather;

    RVAdapterShowRestaurantMenu(ArrayList<Card> cards, RestaurantView grandFather){
        this.cards = cards;
        this.grandFather = grandFather;
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
        for(final Dish d: current.getDishes()){
            if(d.isAvailable()){
                final View dish = inflater.inflate(R.layout.menu_item_display, pvh.menuDishes, false);
                final TextView title = dish.findViewById(R.id.food_title);
                TextView subtitle = dish.findViewById(R.id.food_subtitle);
                final TextView price = dish.findViewById(R.id.price);
                final ImageView image = dish.findViewById(R.id.food_image);
                ImageButton plus = dish.findViewById(R.id.button_plus);
                final ImageButton minus = dish.findViewById(R.id.button_minus);
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

                plus.setOnClickListener(new plusClick(d,minus, orderQuantity));
                minus.setOnClickListener(new minusClick(d, minus, orderQuantity));

                if(d.getOrderItem() == null){
                    plus.setVisibility(View.VISIBLE);
                    minus.setVisibility(View.GONE);
                    orderQuantity.setVisibility(View.GONE);
                }else{
                    plus.setVisibility(View.VISIBLE);
                    minus.setVisibility(View.VISIBLE);
                    orderQuantity.setVisibility(View.VISIBLE);
                    orderQuantity.setText(d.getOrderItem().getPiecesString());
                }

                LinearLayout layout = dish.findViewById(R.id.layout_plus_minus);
                layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

                pvh.menuDishes.addView(dish);
            }
        }

    }


    private class plusClick implements View.OnClickListener{

        Dish d;
        ImageButton minus;
        TextView quantity;

        public plusClick(Dish d, ImageButton minus, TextView quantity){
            this.d = d;
            this.minus = minus;
            this.quantity = quantity;
        }

        @Override
        public void onClick(View v) {
            if(d.getOrderItem() == null){
                d.setOrderItem(new OrderItem(1, d.getDishName(), d.getPrice()));
                minus.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
                quantity.setText("1");
            }else{
                OrderItem item = d.getOrderItem();
                item.plus();
                quantity.setText(item.getPiecesString());
            }
            grandFather.updateTotal();
        }
    }

    private class minusClick implements View.OnClickListener{

        Dish d;
        ImageButton minus;
        TextView quantity;

        public minusClick(Dish d, ImageButton minus, TextView quantity){
            this.d = d;
            this.minus = minus;
            this.quantity = quantity;
        }

        @Override
        public void onClick(View v) {
            if(minus.getVisibility() == View.GONE)
                return;
            if(d.getOrderItem().getPieces() == 1){
                d.setOrderItem(null);
                minus.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
            }else{
                d.getOrderItem().minus();
                quantity.setText(d.getOrderItem().getPiecesString());
            }
            grandFather.updateTotal();
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