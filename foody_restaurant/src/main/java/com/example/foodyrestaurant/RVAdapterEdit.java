package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.ArrayList;

public class RVAdapterEdit extends RecyclerView.Adapter<RVAdapterEdit.CardEdit>{

    private final ArrayList<Card> cards;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private final MenuEdit menuEdit;

    RVAdapterEdit(ArrayList<Card> cards, MenuEdit menuEdit){
        this.cards = cards;
        this.menuEdit = menuEdit;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @NonNull
    @Override
    public CardEdit onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_main_edit, viewGroup, false);
        return new CardEdit(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardEdit cardViewHolder,final int i) {
        final Context context = cardViewHolder.title.getContext();
        cardViewHolder.title.setText(cards.get(i).getTitle());

        if(!cards.get(i).isEditing()){
            cardViewHolder.layout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   menuEdit.savePlaceholder();
                   Intent intent = new Intent(context.getApplicationContext(), MenuEditItem.class);
                   Bundle b = new Bundle();
                   b.putString("MainName", cards.get(i).getTitle());
                   intent.putExtras(b);
                   cardViewHolder.box.getContext().startActivity(intent);
                }
            });
            cardViewHolder.title.setClickable(true);
            cardViewHolder.title.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   for(int j = 0; j < cards.get(i).getDishes().size();j++){
                       cards.get(i).getDishes().get(j).setEditImage(false);
                   }
                   menuEdit.savePlaceholder();
                   Intent intent = new Intent(cardViewHolder.box.getContext().getApplicationContext(), MenuEditItem.class);
                   Bundle b = new Bundle();
                   b.putString("MainName", cards.get(i).getTitle());
                   intent.putExtras(b);
                   cardViewHolder.box.getContext().startActivity(intent);
                }
            });
        } else {
            cardViewHolder.layout.setOnClickListener(null);
            cardViewHolder.title.setClickable(true);
            cardViewHolder.title.setOnClickListener(null);

        }
        cardViewHolder.box.setOnCheckedChangeListener(null);
        if (menuEdit.getBoolean(i)){
            cardViewHolder.box.setChecked(menuEdit.getBoolean(i));
            sparseBooleanArray.put(i, cardViewHolder.box.isChecked());
        } else{
            cardViewHolder.box.setChecked(cards.get(i).isSelected());
        }
        cardViewHolder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cards.get(cardViewHolder.getAdapterPosition()).setSelected(isChecked);
                sparseBooleanArray.put(i, cards.get(i).isSelected());
            }
        });

        if(cards.get(i).isEditing()){
            cardViewHolder.box.setAlpha(1.0f);
            cardViewHolder.arrow.setAlpha(0.0f);
            float distance = cardViewHolder.box.getContext().getResources().getDimensionPixelSize(R.dimen.short36);
            cardViewHolder.title.setX(distance);
            cardViewHolder.layout.setClickable(true);
        } else {
            cardViewHolder.arrow.setAlpha(1.0f);
            cardViewHolder.box.setAlpha(0.0f);
            cardViewHolder.title.setX(0);
        }
    }

    boolean normalToEdit(RecyclerView.ViewHolder view){
        if(view == null)
            return false;

        CardEdit holder = (CardEdit)view;
        CheckBox box = holder.box;
        EditText text= holder.title;
        ImageView arrow = holder.arrow;

        holder.layout.setOnClickListener(null);

        float distance = box.getContext().getResources().getDimensionPixelSize(R.dimen.short36);
        int shortAnimDuration = box.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        box.setAlpha(0.0f);
        box.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();

        text.animate().translationX(distance).setDuration(shortAnimDuration).start();
        box.animate().translationX(0).setDuration(shortAnimDuration).start();

        holder.title.setClickable(true);
        holder.title.setOnClickListener(null);

        arrow.setAlpha(1.0f);
        arrow.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(null).start();

        return true;
    }

    boolean editToNormal(final RecyclerView.ViewHolder view,final int i){
        if(view == null)
            return false;

        final CardEdit holder = (CardEdit) view;
        final CheckBox box = holder.box;
        EditText text= holder.title;
        ImageView arrow = holder.arrow;

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((CardEdit) view).box.getContext().getApplicationContext(), MenuEditItem.class);
                Bundle b = new Bundle();
                b.putString("MainName", cards.get(i).getTitle());
                intent.putExtras(b);
                holder.box.getContext().startActivity(intent);
            }
        });

        holder.title.setClickable(true);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((CardEdit) view).box.getContext().getApplicationContext(), MenuEditItem.class);
                Bundle b = new Bundle();
                b.putString("MainName", cards.get(i).getTitle());
                intent.putExtras(b);
                holder.box.getContext().startActivity(intent);
            }
        });

        int shortAnimDuration = box.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        box.setAlpha(1.0f);
        box.animate().alpha(0.0f).setDuration(shortAnimDuration).start();
        box.setChecked(cards.get(i).isSelected());

        text.animate().translationX(0).setDuration(shortAnimDuration).start();

        arrow.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();
        text.setFocusable(false);

        return true;
    }



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardEdit extends RecyclerView.ViewHolder {
        final ConstraintLayout layout;
        final EditText title;
        final CheckBox box;
        final ImageView arrow;

        CardEdit(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            title = itemView.findViewById(R.id.edit_title);
            box = itemView.findViewById(R.id.checkFood);
            arrow = itemView.findViewById(R.id.frontArrow);
        }
    }

    SparseBooleanArrayParcelable getSparseBooleanArray(){
        return new SparseBooleanArrayParcelable(sparseBooleanArray);
    }

    void setSparseBooleanArray(SparseBooleanArray sparseBooleanArray){
        this.sparseBooleanArray = sparseBooleanArray;
    }

    public class SparseBooleanArrayParcelable extends SparseBooleanArray implements Parcelable {
        public Parcelable.Creator<SparseBooleanArrayParcelable> CREATOR = new Parcelable.Creator<SparseBooleanArrayParcelable>() {
            @Override
            public SparseBooleanArrayParcelable createFromParcel(Parcel source) {
                SparseBooleanArrayParcelable read = new SparseBooleanArrayParcelable();
                int size = source.readInt();

                int[] keys = new int[size];
                boolean[] values = new boolean[size];

                source.readIntArray(keys);
                source.readBooleanArray(values);

                for (int i = 0; i < size; i++) {
                    read.put(keys[i], values[i]);
                }

                return read;
            }

            @Override
            public SparseBooleanArrayParcelable[] newArray(int size) {
                return new SparseBooleanArrayParcelable[size];
            }
        };

        SparseBooleanArrayParcelable() {

        }

        SparseBooleanArrayParcelable(SparseBooleanArray sparseBooleanArray) {
            for (int i = 0; i < sparseBooleanArray.size(); i++) {
                this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            int[] keys = new int[size()];
            boolean[] values = new boolean[size()];

            for (int i = 0; i < size(); i++) {
                keys[i] = keyAt(i);
                values[i] = valueAt(i);
            }

            dest.writeInt(size());
            dest.writeIntArray(keys);
            dest.writeBooleanArray(values);
        }
    }


}