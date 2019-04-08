package com.example.foodyrestaurant;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    public CardEdit onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_card_edit, viewGroup, false);
        final CardEdit pvh = new CardEdit(v);


        pvh.title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    pvh.title.setSelection(pvh.title.getText().length());
                    if(pvh.title.length() > 0)
                        pvh.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                    pvh.title.setError(null);
                }else{
                    pvh.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    if(pvh.title.length() == 0){
                       pvh.title.setError("ERRORE");
                    }
                }
            }
        });


        pvh.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pvh.title.setSelection(pvh.title.getText().length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cards.get(i).setTitle(s.toString());
                if(s.length() > 0) {
                    Log.d("SIMONA", "Putte image ");
                    pvh.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    pvh.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                } else {
                    Log.d("SIMONA", "removed image ");
                    pvh.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pvh.title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP && pvh.title.length() > 0 && pvh.title.hasFocus()) {
                    if(event.getRawX() >= (pvh.title.getRight() - pvh.title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        pvh.title.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        pvh.title.setImeOptions(EditorInfo.IME_ACTION_DONE);
        pvh.title.setRawInputType(InputType.TYPE_CLASS_TEXT);

        return pvh;
    }

    @Override
    public void onBindViewHolder(final CardEdit cardViewHolder, final int i) {
        cardViewHolder.title.setText(cards.get(i).getTitle().toUpperCase());
        final ArrayList<Dish> dishes = cards.get(i).getDishes();
        final Context context = cardViewHolder.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        for (int j = 0; j < dishes.size(); j++){
            View dish = inflater.inflate(R.layout.menu_item_edit, cardViewHolder.menuDishes, false);
            final TextInputEditText title = dish.findViewById(R.id.food_title);
            EditText subtitle = dish.findViewById(R.id.food_subtitle);
            EditText price = dish.findViewById(R.id.price);
            ImageView image = dish.findViewById(R.id.food_image);
            title.setText(dishes.get(j).getDishName());
            subtitle.setText(dishes.get(j).getDishDescription());
            price.setText(dishes.get(j).getPrice());

            /*

            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    title.setSelection(title.getText().length());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    dishes.get(i).setDishName(s.toString());
                    Log.d("EDITBIATCH", "dishes "+dishes.get(i).getDishName());
                    if(s.length() > 0) {
                        Log.d("SIMONA", "Putte image ");
                        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                    } else {
                        Log.d("SIMONA", "removed image ");
                        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            */
            cardViewHolder.menuDishes.addView(dish);
        }

        cardViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        cardViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, cards.size());
                Toast.makeText(context, "item "+i+" removed", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CardEdit extends RecyclerView.ViewHolder {
        CardView cv;
        ImageButton delete;
        LinearLayout menuDishes;
        EditText title;

        public CardEdit(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            delete = (ImageButton) itemView.findViewById(R.id.trash);
            title = (EditText) itemView.findViewById(R.id.title);
            menuDishes = (LinearLayout) itemView.findViewById(R.id.menu_dishes);


        }
    }

}