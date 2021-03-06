package com.example.foodyrestaurant;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterChooseBiker extends RecyclerView.Adapter<RVAdapterChooseBiker.CardViewHolder>{

    ArrayList<ChooseBikerActivity.BikerComplete> bikers;
    ChooseBikerActivity fatherClass;

    RVAdapterChooseBiker(ArrayList<ChooseBikerActivity.BikerComplete> bikers, ChooseBikerActivity fatherClass){
        this.bikers = bikers;
        this.fatherClass = fatherClass;
    }

    @Override
    public int getItemCount() {
            return bikers.size();
    }

    @NonNull
    @Override
    public RVAdapterChooseBiker.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.choose_biker_card, viewGroup, false);

                return new RVAdapterChooseBiker.CardViewHolder(v);
            }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterChooseBiker.CardViewHolder pvh, int i) {

        final ChooseBikerActivity.BikerComplete currentBiker = bikers.get(i);

        if(currentBiker.biker.getPath() == null){
            pvh.profilePicture.setVisibility(View.GONE);
        }else{
            if(currentBiker.biker.getPath().length() == 0){
                pvh.profilePicture.setVisibility(View.GONE);
            }else{
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                mStorageRef.child(currentBiker.biker.getPath())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide
                                .with(pvh.profilePicture.getContext())
                                .load(uri)
                                .into(pvh.profilePicture);
                    }
                });

            }
        }

        pvh.username.setText(currentBiker.biker.getUsername());
        pvh.level.setText("Biker Beginner");
        if(currentBiker.biker.getNumberPhone() != null)
            pvh.phoneNumber.setText(currentBiker.biker.getNumberPhone());
        else
            pvh.callLayout.setVisibility(View.GONE);
        pvh.distance.setText(currentBiker.getDistanceString());

        pvh.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fatherClass.bikerChosen(pvh.getAdapterPosition());
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePicture;
        TextView username, level, phoneNumber, distance;
        ConstraintLayout callLayout, distanceLayout;
        MaterialButton choose;

        CardViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.choose_biker_profile_image);
            username = itemView.findViewById(R.id.choose_biker_name);
            level = itemView.findViewById(R.id.choose_biker_level);
            phoneNumber = itemView.findViewById(R.id.choose_biker_phone_number);
            distance = itemView.findViewById(R.id.choose_biker_distance);
            callLayout = itemView.findViewById(R.id.choose_biker_phone_layout);
            distanceLayout = itemView.findViewById(R.id.choose_biker_distance_layout);
            choose = itemView.findViewById(R.id.choose_biker_button);
        }
    }
}