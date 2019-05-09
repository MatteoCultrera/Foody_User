package com.example.foodyuser;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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

public class RVAdapterMenu extends RecyclerView.Adapter<RVAdapterMenu.CardViewHolder>{

    private List<Card> cards;
    private ArrayList<OrderItem> orders;
    private RestaurantShow fatherClass;

    RVAdapterMenu(RestaurantShow res){
        fatherClass = res;
    }

    public void setProperties(List<Card> cards, ArrayList<OrderItem> orders){
        this.cards = cards;
        this.orders = orders;
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
                if (dishes.get(j).isAvailable()) {
                    final View dish = inflater.inflate(R.layout.menu_item_display, pvh.menuDishes, false);
                    final int toSet = j;
                    final TextView title = dish.findViewById(R.id.food_title);
                    TextView subtitle = dish.findViewById(R.id.food_subtitle);
                    final TextView price = dish.findViewById(R.id.price);
                    final ImageView image = dish.findViewById(R.id.food_image);
                    ImageButton plus = dish.findViewById(R.id.button_plus);
                    ImageButton minus = dish.findViewById(R.id.button_minus);
                    TextView orderQuantity = dish.findViewById(R.id.order_quantity);
                    title.setText(dishes.get(j).getDishName());
                    if(dishes.get(j).getDishDescription().length() == 0)
                        subtitle.setVisibility(View.GONE);
                    else{
                        subtitle.setVisibility(View.VISIBLE);
                        subtitle.setText(dishes.get(j).getDishDescription());
                    }
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

                    int quantity = getOrderQuantity(dishes.get(j).getDishName());

                    //Log.d("PROVA", dishes.get(j).getDishName()+" "+quantity);

                    if(quantity > 0){
                        minus.setVisibility(View.VISIBLE);
                        orderQuantity.setVisibility(View.VISIBLE);
                        plus.setVisibility(View.VISIBLE);
                        orderQuantity.setText(String.valueOf(quantity));

                        plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addItem(dishes.get(toSet).getDishName());
                                notifyItemChanged(pos);
                            }
                        });

                        minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeItem(dishes.get(toSet).getDishName());
                                notifyItemChanged(pos);
                            }
                        });

                    }else{
                        minus.setVisibility(View.GONE);
                        orderQuantity.setVisibility(View.GONE);
                        plus.setVisibility(View.VISIBLE);
                        plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OrderItem e = new OrderItem(1, title.getText().toString(), dishes.get(toSet).getPrice());
                                orders.add(e);
                                notifyItemChanged(pos);
                                fatherClass.updateFAB();
                            }
                        });
                    }


                }
            }
        }

        pvh.title.setText(cards.get(pos).getTitle());
    }

    public void addItem(String name){
        int size = orders.size();
        for(int i = 0; i < size; i++){
            if(orders.get(i).getOrderName().equals(name)){
                orders.get(i).plus();
                fatherClass.updateFAB();
                return;
            }
        }
    }

    public void removeItem(String name){
        int size = orders.size();
        for(int i = 0; i < size; i++){
            if(orders.get(i).getOrderName().equals(name)){
                if(orders.get(i).getPieces() > 1){
                    orders.get(i).minus();
                    fatherClass.updateFAB();
                    return;
                }else{
                    orders.remove(i);
                    fatherClass.updateFAB();
                    return;
                }
            }
        }
    }

    public int getOrderQuantity(String name){
        int size = orders.size();
        for(int i = 0; i < size; i++){
            if(orders.get(i).getOrderName().equals(name)){
                return orders.get(i).getPieces();
            }
        }
        return 0;
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