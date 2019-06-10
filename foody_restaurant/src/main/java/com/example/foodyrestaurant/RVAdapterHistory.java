package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RVAdapterHistory extends RecyclerView.Adapter<RVAdapterHistory.CardViewHolder>{

    private final ArrayList<Reservation> reservations;

    RVAdapterHistory(ArrayList<Reservation> reservations){
        this.reservations = reservations;
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    @NonNull
    @Override
    public RVAdapterHistory.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_card_display, viewGroup, false);

        return new RVAdapterHistory.CardViewHolder(v);
    }

    private String getDate(String date){
        String[] nums = date.split("-");
        int day = Integer.valueOf(nums[2]);
        int month = Integer.valueOf(nums[1]);
        int year = Integer.valueOf(nums[0]);

        return String.format("%02d/%02d/%d",day, month, year);

    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterHistory.CardViewHolder pvh, int i) {
        final Context context = pvh.cv.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final int pos = pvh.getAdapterPosition();

        final ArrayList<Dish> dishes = reservations.get(pos).getDishesOrdered();

        String resId = reservations.get(i).getUserUID();
        String first2Letters = "";
        for(char c : resId.toCharArray()){
            if(first2Letters.length() == 2)
                break;
            else{
                if(Character.isAlphabetic(c))
                    first2Letters += c;
            }
        }
        int id_size = reservations.get(i).getReservationID().length();
        String lastFour = reservations.get(i).getReservationID().substring(id_size-4,id_size);
        String orderId = (first2Letters + lastFour).toUpperCase();
        pvh.idOrder.setText(orderId);
        String status1;
        switch (reservations.get(pos).getPreparationStatus()){
            case PENDING:
                status1 = pvh.accept.getContext().getString(R.string.pending);
                break;
            case DOING:
                status1 = pvh.accept.getContext().getString(R.string.doing);
                break;
            case DONE:
                status1 = pvh.accept.getContext().getString(R.string.done);
                break;
            default:
                status1=" ";
                break;
        }
        pvh.status.setText(status1);
        pvh.time.setText(getDate(reservations.get(i).getDate())+"\n"+reservations.get(i).getOrderTime());

        if(reservations.get(i).isAccepted())
            pvh.time.setTextColor(context.getResources().getColor(R.color.accept, context.getTheme()));
        else
            pvh.time.setTextColor(context.getResources().getColor(R.color.heart_red, context.getTheme()));

        pvh.userName.setText(reservations.get(i).getUserName());

        if(reservations.get(i).getResNote() == null) {
            pvh.notePlaceholder.setVisibility(View.GONE);
            pvh.notes.setVisibility(View.GONE);
            pvh.separatorInfoNote.setVisibility(View.GONE);
        } else {
            pvh.notes.setText(reservations.get(i).getResNote());
        }

        pvh.accept.setVisibility(View.GONE);
        pvh.decline.setVisibility(View.GONE);

        pvh.hintLayout.setVisibility(View.GONE);

        pvh.menuDishes.removeAllViews();

        for(int j = 0; j < dishes.size(); j++){
            final int toSet = j;
            final View dish = inflater.inflate(R.layout.reservation_item_display, pvh.menuDishes, false);
            final TextView foodTitle = dish.findViewById(R.id.food_title_res);
            foodTitle.setText(dishes.get(j).getStringForRes());

            if(dishes.get(j).isPrepared())
                foodTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                pvh.menuDishes.addView(dish);
        }


        pvh.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pvh.additionalLayout.getVisibility() == View.VISIBLE) {
                    pvh.additionalLayout.setVisibility(View.GONE);
                    pvh.plus.setImageResource(R.drawable.expand_white);
                } else {
                    pvh.additionalLayout.setVisibility(View.VISIBLE);
                    pvh.plus.setImageResource(R.drawable.collapse_white);
                }
            }
        });

        pvh.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+reservations.get(pos).getUserPhone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {
        final CardView cv;
        final TextView idOrder;
        final TextView time;
        final TextView status;
        final LinearLayout menuDishes;
        final ConstraintLayout buttonLayout;
        final MaterialButton accept;
        final MaterialButton decline;
        final FloatingActionButton plus;
        final ConstraintLayout additionalLayout;
        final TextView userName;
        final TextView notes;
        final AppCompatImageButton phone;
        final TextView notePlaceholder;
        final View separatorInfoNote;
        final ConstraintLayout layoutCard, hintLayout;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            idOrder = itemView.findViewById(R.id.idOrder);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            menuDishes = itemView.findViewById(R.id.orderList);
            buttonLayout = itemView.findViewById(R.id.buttonLayout);
            accept = itemView.findViewById(R.id.acceptOrder);
            decline = itemView.findViewById(R.id.declineOrder);
            plus = itemView.findViewById(R.id.plusReservation);
            additionalLayout = itemView.findViewById(R.id.constrGone);
            userName = itemView.findViewById(R.id.user_name);
            notes = itemView.findViewById(R.id.note_text);
            phone = itemView.findViewById(R.id.call_user);
            notePlaceholder = itemView.findViewById(R.id.note_placeholder);
            separatorInfoNote = itemView.findViewById(R.id.separator2);
            layoutCard = itemView.findViewById(R.id.layout_card_reservation);
            hintLayout = itemView.findViewById(R.id.hint_layout);
        }
    }
}