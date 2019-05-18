package com.example.foodyrestaurant;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterChooseBiker extends RecyclerView.Adapter<RVAdapterChooseBiker.CardViewHolder>{

    RVAdapterChooseBiker(){

    }

    @Override
    public int getItemCount() {
            return 0;
    }

    @NonNull
    @Override
    public RVAdapterChooseBiker.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.choose_biker_card, viewGroup, false);

                return new RVAdapterChooseBiker.CardViewHolder(v);
            }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterChooseBiker.CardViewHolder pvh, int i) {

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        CardViewHolder(View itemView) {
            super(itemView);

        }
    }
}