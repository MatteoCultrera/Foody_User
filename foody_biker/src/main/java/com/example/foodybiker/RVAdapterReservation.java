package com.example.foodybiker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class RVAdapterReservation extends RecyclerView.Adapter<RVAdapterReservation.CardViewHolder>{

    private List<Reservation> reservations;
    private ReservationFragment fatherFragment;

    RVAdapterReservation(List<Reservation> reservations, ReservationFragment fatherFragment){
        this.reservations = reservations;
        this.fatherFragment = fatherFragment;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_item, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder pvh,final int i) {

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