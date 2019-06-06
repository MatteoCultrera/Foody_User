package com.example.foodyuser;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RVAdapterHistory  extends RecyclerView.Adapter<RVAdapterHistory.CardViewHolder>{

    private final List<Reservation> reservations;
    private String imagePath;
    RVAdapterHistory(List<Reservation> reservations){
        this.reservations = reservations;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public RVAdapterHistory.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);
        return new RVAdapterHistory.CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterHistory.CardViewHolder pvh, final int i) {
        Reservation currentRes = reservations.get(i);
        LayoutInflater inflater = LayoutInflater.from(pvh.restName.getContext());
        Resources resources = pvh.status.getContext().getResources();
        pvh.restName.setText(currentRes.getRestaurantName());
        pvh.restAddress.setText(currentRes.getRestaurantAddress());

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("restaurantsInfo");
                Query query = database.child(reservations.get(i).getRestaurantID()).child("info").child("imagePath");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        imagePath = dataSnapshot.getValue(String.class);
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        mStorageRef.child(imagePath).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide
                                                .with(pvh.profilePicture.getContext())
                                                .load(uri)
                                                .into(pvh.profilePicture);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Glide
                                                .with(pvh.profilePicture.getContext())
                                                .load(R.drawable.profile_placeholder)
                                                .into(pvh.profilePicture);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Glide
                                .with(pvh.profilePicture.getContext())
                                .load(R.drawable.profile_placeholder)
                                .into(pvh.profilePicture);
                    }
                });
            }
        }).start();*/

        File currentImage = new File(currentRes.getImagePathLocale());
        Log.d("TESTHISTORY",""+currentImage.getPath());
        if(currentImage.exists()){
            RequestOptions options = new RequestOptions();
            options.signature(new ObjectKey(currentImage.getName()+" "+currentImage.lastModified()));
            Glide
                    .with(pvh.profilePicture.getContext())
                    .setDefaultRequestOptions(options)
                    .load(currentImage)
                    .into(pvh.profilePicture);

        }else{
            Log.d("TESTHISTORY", "IMAGES NOT FOUND");
            Glide
                    .with(pvh.profilePicture.getContext())
                    .load(R.drawable.profile_placeholder)
                    .into(pvh.profilePicture);
        }

        ArrayList<Dish> dishes = currentRes.getDishesOrdered();
        pvh.dishes.removeAllViews();
        if(dishes!=null){
            for(Dish d: dishes){

                View dish = inflater.inflate(R.layout.reservation_item_display_current_order, pvh.dishes, false);
                TextView name = dish.findViewById(R.id.current_item_food_name);
                TextView price = dish.findViewById(R.id.current_item_food_price);

                name.setText(String.format(resources.getString(R.string.plate), d.getQuantity(), d.getDishName()));
                float priceFloat = d.getPrice()*d.getQuantity();
                price.setText(String.format(resources.getString(R.string.price), priceFloat));

                pvh.dishes.addView(dish);
            }
        }
        String status;
        switch(currentRes.getPreparationStatusString().toLowerCase()){
            case("rejected"):
                status = resources.getString(R.string.rejected);
                break;
            default:
                status = resources.getString(R.string.delivered);
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