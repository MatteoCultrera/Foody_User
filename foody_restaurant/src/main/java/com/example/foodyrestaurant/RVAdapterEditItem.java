package com.example.foodyrestaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterEditItem extends RecyclerView.Adapter<RVAdapterEditItem.DishEdit>{

    ArrayList<Dish> dishes;

    public RVAdapterEditItem(ArrayList<Dish> dishes){
        this.dishes = dishes;
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    @Override
    public RVAdapterEditItem.DishEdit onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item_edit, viewGroup, false);
        DishEdit pvh = new DishEdit(v, new DishNameEditTextListener(), new DishDescriptionEditTextListener());

        return pvh;
    }

    @Override
    public void onBindViewHolder(final RVAdapterEditItem.DishEdit dishViewHolder, int i) {

        Context context = dishViewHolder.cardView.getContext();
        dishViewHolder.dishName.setText(dishes.get(i).getDishName());
        dishViewHolder.dishName.clearFocus();
        dishViewHolder.dishDesc.setText(dishes.get(i).getDishDescription());
        dishViewHolder.dishDesc.clearFocus();
        Float value = Float.valueOf(dishes.get(i).getPrice());
        dishViewHolder.price.setText(String.format("%.2f",value));
        dishViewHolder.price.clearFocus();


        dishViewHolder.dishName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dishViewHolder.dishName.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        dishViewHolder.dishDesc.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dishViewHolder.dishDesc.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


        dishViewHolder.nameListener.updatePosition(dishViewHolder.getAdapterPosition());
        dishViewHolder.descriptionListener.updatePosition(dishViewHolder.getAdapterPosition());

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class DishEdit extends RecyclerView.ViewHolder {
        CardView cardView;
        EditText dishName, dishDesc, price;
        CircleImageView dishPicture;
        DishNameEditTextListener nameListener;
        DishDescriptionEditTextListener descriptionListener;

        public DishEdit(View itemView, DishNameEditTextListener nameListener, DishDescriptionEditTextListener descriptionListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dish_card);
            dishPicture = itemView.findViewById(R.id.dish_image);
            dishName = itemView.findViewById(R.id.dish_name);
            dishDesc = itemView.findViewById(R.id.dish_description);
            price = itemView.findViewById(R.id.dish_price);
            this.nameListener = nameListener;
            this.descriptionListener = descriptionListener;

            dishName.addTextChangedListener(nameListener);
            nameListener.setEditText(dishName);
            dishDesc.addTextChangedListener(descriptionListener);
            descriptionListener.setEditText(dishDesc);

            dishName.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(dishName.getCompoundDrawables()[2]!=null){
                            if(event.getX() >= (dishName.getRight()- dishName.getLeft() - dishName.getCompoundDrawables()[2].getBounds().width())) {
                                dishName.setText("");
                            }
                        }
                    }
                    return false;
                }
            });

            dishDesc.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(dishDesc.getCompoundDrawables()[2]!=null){
                            if(event.getX() >= (dishDesc.getRight()- dishDesc.getLeft() - dishDesc.getCompoundDrawables()[2].getBounds().width())) {
                                dishDesc.setText("");
                            }
                        }
                    }
                    return false;
                }
            });

        }
    }

    private class DishNameEditTextListener implements TextWatcher {
        private int position;
        private EditText editText;

        public void setEditText(EditText text){
            editText = text;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            dishes.get(position).setDishName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(dishes.get(position).getDishName().length() == 0)
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            else
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
        }
    }

    private class DishDescriptionEditTextListener implements TextWatcher {
        private int position;
        private EditText editText;

        public void setEditText(EditText text){
            editText = text;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            dishes.get(position).setDishDescription(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(dishes.get(position).getDishName().length() == 0)
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            else
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
        }
    }
}
