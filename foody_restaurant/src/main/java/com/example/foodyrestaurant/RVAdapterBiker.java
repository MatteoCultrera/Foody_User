package com.example.foodyrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class RVAdapterBiker extends RecyclerView.Adapter<RVAdapterBiker.CardViewHolder>{

    private final List<BikerFragment.ReservationBiker> reservations;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private BikerFragment fatherClass;
    private File storageDir;

    RVAdapterBiker(List<BikerFragment.ReservationBiker> reservations, BikerFragment fatherClass){
        this.reservations = reservations;
        this.fatherClass = fatherClass;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public RVAdapterBiker.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.biker_card_display, viewGroup, false);
        storageDir = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = viewGroup.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        return new RVAdapterBiker.CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterBiker.CardViewHolder pvh, final int i) {

        final BikerFragment.ReservationBiker current = reservations.get(i);
        LayoutInflater inflater = LayoutInflater.from(pvh.idOrder.getContext());

        pvh.idOrder.setText(current.getReservation().getReservationID());
        pvh.time.setText(current.getReservation().getDeliveryTime());
        pvh.status.setText(current.getReservation().getPreparationStatusString());
        if(current.hasBiker()){
            pvh.bikerInfoLayout.setVisibility(View.VISIBLE);
            pvh.callBiker.setVisibility(View.VISIBLE);
            pvh.callBiker.setBackgroundTintList(ContextCompat.getColorStateList(pvh.callBiker.getContext(), R.color.colorAccent));
            pvh.callBiker.setEnabled(true);
            pvh.callBiker.setText(pvh.callBiker.getContext().getString(R.string.order_delivered));
            pvh.callBiker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if(current.getBiker().getPath() != null){
                if(current.getBiker().getPath().length() !=0){
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    mStorageRef.child(current.getBiker().getPath())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide
                                    .with(pvh.idOrder.getContext())
                                    .load(uri)
                                    .into(pvh.bikerImage);
                        }
                    });
                }else{
                    pvh.bikerImage.setVisibility(View.GONE);
                }
            }else{
                pvh.bikerImage.setVisibility(View.GONE);
            }

            pvh.bikerName.setText(current.getBiker().getUsername());
            pvh.bikerLevel.setText("Biker Beginner");


        }else{
            pvh.bikerInfoLayout.setVisibility(View.GONE);
            pvh.callBiker.setVisibility(View.VISIBLE);
            if(current.isWaitingBiker()){
                pvh.callBiker.setClickable(false);
                pvh.callBiker.setEnabled(false);
                pvh.callBiker.setTextColor(pvh.callBiker.getContext().getResources().getColor(R.color.whiteText,pvh.callBiker.getContext().getTheme()));
                pvh.callBiker.setIconTint(ContextCompat.getColorStateList(pvh.callBiker.getContext(), R.color.whiteText));
                pvh.callBiker.setBackgroundTintList(ContextCompat.getColorStateList(pvh.callBiker.getContext(), R.color.colorPrimary));
                pvh.callBiker.setText(pvh.callBiker.getContext().getString(R.string.waiting_biker));
                pvh.callBiker.setOnClickListener(null);
            }else{
                pvh.callBiker.setClickable(true);
                pvh.callBiker.setEnabled(true);
                pvh.callBiker.setBackgroundTintList(ContextCompat.getColorStateList(pvh.callBiker.getContext(), R.color.colorAccent));
                pvh.callBiker.setText(pvh.callBiker.getContext().getString(R.string.call_biker));
                pvh.callBiker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(pvh.callBiker.getContext().getApplicationContext(), ChooseBikerActivity.class);
                        intent.putExtra("ReservationID", current.getCompleteRes());
                        pvh.callBiker.getContext().startActivity(intent);
                    }
                });
            }
        }

        pvh.dishesLayout.removeAllViews();
        for(Dish d: current.getReservation().getDishesOrdered()){
            final View dish = inflater.inflate(R.layout.reservation_item_display, pvh.dishesLayout, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(d.getStringForRes());
            pvh.dishesLayout.addView(dish);
        }

        Log.d("BIKERFETCH", "waiting biker: "+current.isWaitingBiker()+"");


    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView idOrder, time, status, bikerName, bikerLevel;
        LinearLayout dishesLayout;
        ConstraintLayout bikerInfoLayout;
        MaterialButton callBiker;
        CircleImageView bikerImage;
        ImageButton phoneButton;

        CardViewHolder(View itemView) {
            super(itemView);
            idOrder = itemView.findViewById(R.id.id_order_biker);
            time = itemView.findViewById(R.id.time_biker);
            status = itemView.findViewById(R.id.status_biker);
            bikerName = itemView.findViewById(R.id.biker_name);
            bikerLevel = itemView.findViewById(R.id.biker_level);
            dishesLayout = itemView.findViewById(R.id.order_list_biker);
            bikerInfoLayout = itemView.findViewById(R.id.info_biker);
            callBiker = itemView.findViewById(R.id.main_button);
            bikerImage = itemView.findViewById(R.id.biker_image);
            phoneButton = itemView.findViewById(R.id.phone_biker);
        }
    }
}