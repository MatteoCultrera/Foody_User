package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class RVAdapterEditItem extends RecyclerView.Adapter<RVAdapterEdit.CardEdit>{

    ArrayList<Dish> dishes;

    public RVAdapterEditItem(ArrayList<Dish> dishes){
        this.dishes = dishes;
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    @Override
    public RVAdapterEdit.CardEdit onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_main_edit, viewGroup, false);
        final RVAdapterEdit.CardEdit pvh = new RVAdapterEdit.CardEdit(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(final RVAdapterEdit.CardEdit cardViewHolder, int i) {


    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class DishEdit extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        EditText title;
        CheckBox box;
        ImageView arrow;

        public DishEdit(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            title = itemView.findViewById(R.id.edit_title);
            box = itemView.findViewById(R.id.checkFood);
            arrow = itemView.findViewById(R.id.frontArrow);
        }
    }
}
