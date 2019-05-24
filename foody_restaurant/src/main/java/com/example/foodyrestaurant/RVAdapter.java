package com.example.foodyrestaurant;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder>{

    private final List<Card> cards;
    private MenuFragment father;

    RVAdapter(List<Card> cards, MenuFragment father){
        this.cards = cards;
        this.father = father;
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
    public void onBindViewHolder(@NonNull CardViewHolder pvh, int i) {

        Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = pvh.getAdapterPosition();
        final ArrayList<Dish> dishes = cards.get(pos).getDishes();

        pvh.menuDishes.removeAllViews();

        if (dishes != null) {
            for (int j = 0; j < dishes.size(); j++) {
                View dish = inflater.inflate(R.layout.menu_item_display, pvh.menuDishes, false);
                TextView title = dish.findViewById(R.id.food_title);
                final TextView titleF = dish.findViewById(R.id.food_title);
                TextView subtitle = dish.findViewById(R.id.food_subtitle);
                final TextView subtitleF = dish.findViewById(R.id.food_subtitle);
                TextView price = dish.findViewById(R.id.price);
                final TextView priceF = dish.findViewById(R.id.price);
                final int index = j;
                final ImageView image = dish.findViewById(R.id.food_image);
                title.setText(dishes.get(j).getDishName());
                subtitle.setText(dishes.get(j).getDishDescription());
                price.setText(String.format(Locale.UK, "%.2f", dishes.get(j).getPrice()) + " €");
                if (dishes.get(j).getPathDB() == null)
                    image.setVisibility(View.GONE);
                else {
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    mStorageRef.child(dishes.get(j).getPathDB()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide
                                            .with(image.getContext())
                                            .load(uri)
                                            .into(image);
                                }
                            });
                }
                pvh.menuDishes.addView(dish);
                dishes.get(j).setAdded(true);
                final Switch enabler = dish.findViewById(R.id.enabler);
                if (dishes.get(j).isAvailable()) {
                    enabler.setChecked(dishes.get(j).isAvailable());
                    title.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                    subtitle.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.secondaryText));
                    price.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                } else {
                    enabler.setChecked(dishes.get(j).isAvailable());
                    title.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                    subtitle.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                    price.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                }
                enabler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (!b) {
                            dishes.get(index).setAvailable(false);
                            titleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                            subtitleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                            priceF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.disabledText));
                            image.setAlpha(0.3f);
                            father.updateMenuDB();
                        } else {
                            dishes.get(index).setAvailable(true);
                            titleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                            subtitleF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.secondaryText));
                            priceF.setTextColor(ContextCompat.getColor(enabler.getContext(), R.color.primaryText));
                            image.setAlpha(1f);
                            father.updateMenuDB();
                        }
                    }
                });
            }
        }

        pvh.title.setText(cards.get(pos).getTitle());
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
        final boolean isInflated;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            title = itemView.findViewById(R.id.title);
            menuDishes = itemView.findViewById(R.id.menu_dishes);
            outside = itemView.findViewById(R.id.outside);
            isInflated = false;
        }
    }
}