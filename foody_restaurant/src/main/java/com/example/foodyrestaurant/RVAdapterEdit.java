package com.example.foodyrestaurant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RVAdapterEdit extends RecyclerView.Adapter<RVAdapterEdit.CardEdit>{

    ArrayList<Card> cards;

    public RVAdapterEdit(ArrayList<Card> cards){
        this.cards = cards;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public CardEdit onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_main_edit, viewGroup, false);
        final CardEdit pvh = new CardEdit(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(final CardEdit cardViewHolder, final int i) {
        final Context context = cardViewHolder.title.getContext();
       cardViewHolder.title.setText(cards.get(i).getTitle());


       if(cards.get(i).isEditing()){
            cardViewHolder.box.setVisibility(View.VISIBLE);
            cardViewHolder.box.setAlpha(1.0f);
            cardViewHolder.arrow.setAlpha(0.0f);
            float distance = cardViewHolder.box.getContext().getResources().getDimensionPixelSize(R.dimen.short36);
            cardViewHolder.title.setX(distance);

           cardViewHolder.layout.setClickable(true);


       }else{
           cardViewHolder.box.setVisibility(View.GONE);
           cardViewHolder.arrow.setAlpha(1.0f);
           cardViewHolder.title.setX(0);
       }

    }

    public boolean normalToEdit(RecyclerView.ViewHolder view){
        if(view == null)
            return false;

        CardEdit holder = (CardEdit)view;
        CheckBox box = holder.box;
        EditText text= holder.title;
        ImageView arrow = holder.arrow;

        float distance = box.getContext().getResources().getDimensionPixelSize(R.dimen.short36);
        int shortAnimDuration = box.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        box.setAlpha(0.0f);
        box.setVisibility(View.VISIBLE);
        box.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();

        box.setX(-distance);

        text.animate().translationX(distance).setDuration(shortAnimDuration).start();
        box.animate().translationX(0).setDuration(shortAnimDuration).start();

        arrow.setAlpha(1.0f);
        arrow.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(null).start();

        return true;
    }

    public boolean editToNormal(RecyclerView.ViewHolder view){
        if(view == null)
            return false;

        CardEdit holder = (CardEdit) view;
        final CheckBox box = holder.box;
        EditText text= holder.title;
        ImageView arrow = holder.arrow;


        float distance = box.getContext().getResources().getDimensionPixelSize(R.dimen.short36);
        int shortAnimDuration = box.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        box.setAlpha(1.0f);
        box.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                box.setVisibility(View.GONE);
            }
        });

        text.animate().translationX(0).setDuration(shortAnimDuration).start();
        box.animate().translationX(-distance).setDuration(shortAnimDuration).start();

        arrow.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();

        return true;
    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardEdit extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        EditText title;
        CheckBox box;
        ImageView arrow;

        public CardEdit(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            title = itemView.findViewById(R.id.edit_title);
            box = itemView.findViewById(R.id.checkFood);
            arrow = itemView.findViewById(R.id.frontArrow);
        }
    }

}