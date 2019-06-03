package com.example.foodyuser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.foody_library.Review;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterShowRestaurantReviews extends RecyclerView.Adapter<RVAdapterShowRestaurantReviews.CardViewHolder>{

    ArrayList<Review> reviews;
    RestaurantView grandFather;

    RVAdapterShowRestaurantReviews(ArrayList<Review> reviews, RestaurantView grandFather){
        this.reviews = reviews;
        this.grandFather = grandFather;
    }


    @Override
    public int getItemCount() {
        return reviews.size();
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterShowRestaurantReviews.CardViewHolder pvh, int i) {
        Context context = pvh.userName.getContext();
        Review current = reviews.get(i);


        pvh.userName.setText(current.getUserName());
        if(current.getImagePath()!=null){
            if(current.getImagePath().length() > 0){
                File imageFile = new File(current.getImagePath());
                RequestOptions options = new RequestOptions();
                options.signature(new ObjectKey(imageFile.getName()+" "+imageFile.lastModified()));
                Glide
                        .with(context)
                        .setDefaultRequestOptions(options)
                        .load(imageFile)
                        .into(pvh.userPicture);
            }else {
                pvh.userPicture.setVisibility(View.GONE);
            }
        }else{
            pvh.userPicture.setVisibility(View.GONE);
        }
        pvh.rating.setText(current.getRatingString());
        pvh.ratingBar.setRating(current.getRating());

        if(current.getNote()!=null){
            if(current.getNote().length() > 0){
                pvh.notes.setText(current.getNote());
            }else{
                pvh.notes.setVisibility(View.GONE);
            }
        }else{
            pvh.notes.setVisibility(View.GONE);
        }

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userPicture;
        TextView userName, notes, rating;
        RatingBar ratingBar;

        CardViewHolder(View itemView) {
            super(itemView);
            userPicture = itemView.findViewById(R.id.rating_picture);
            userName = itemView.findViewById(R.id.rating_username);
            notes = itemView.findViewById(R.id.rating_comment);
            rating = itemView.findViewById(R.id.rating_value);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}