package com.example.foodyrestaurant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.foody_library.Review;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterReview extends RecyclerView.Adapter<RVAdapterReview.CardViewHolder>{

    ArrayList<Review> reviews;

    public RVAdapterReview(ArrayList<Review> reviews){
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterReview.CardViewHolder pvh, int i) {
        Context context = pvh.userName.getContext();
        Review thisReview = reviews.get(i);

        pvh.userName.setText(thisReview.getUserName());
        pvh.ratingBar.setRating(thisReview.getRating());
        pvh.rating.setText(thisReview.getRatingString());
        if(thisReview.getImagePath() != null){
            if(thisReview.getImagePath().length() > 0){
                File userPicture = new File(thisReview.getImagePath());
                RequestOptions options = new RequestOptions();
                options.signature(new ObjectKey(userPicture.getName()+" "+userPicture.lastModified()));
                Glide
                        .with(context)
                        .setDefaultRequestOptions(options)
                        .load(userPicture)
                        .into(pvh.userPicture);
            }else
                pvh.userPicture.setVisibility(View.GONE);
        }else
            pvh.userPicture.setVisibility(View.GONE);

        if(thisReview.getNote() != null){
            if(thisReview.getNote().length() > 0){
                pvh.notes.setText(thisReview.getNote());
            }else
                pvh.notes.setVisibility(View.GONE);
        }else
            pvh.notes.setVisibility(View.GONE);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
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
