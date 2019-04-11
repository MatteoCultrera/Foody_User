package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
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

       cardViewHolder.layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               /*
               Intent intent = new Intent(context.getApplicationContext(), MenuEdit.class);
               context.startActivity(intent);
               */
           }
       });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardEdit extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        EditText title;
        CheckBox box;

        public CardEdit(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            title = itemView.findViewById(R.id.edit_title);
            box = itemView.findViewById(R.id.checkFood);
        }
    }

}