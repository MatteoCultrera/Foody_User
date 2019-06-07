package com.example.foodyuser;

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

public class RVAdapterReviews extends RecyclerView.Adapter<RVAdapterReviews.CardViewHolder>{

    ArrayList<Review> reviews;

    RVAdapterReviews(ArrayList<Review> reviews){
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review, viewGroup, false);
        return new RVAdapterReviews.CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterReviews.CardViewHolder pvh, int i) {
        Context context = pvh.userName.getContext();
        Review thisReview = reviews.get(i);

        pvh.userName.setText(thisReview.getRestName());
        if(thisReview.getImagePathRest() != null){
            if(thisReview.getImagePathRest().length() > 0){
                File imageFile = new File(thisReview.getImagePathRest());
                RequestOptions options = new RequestOptions();
                options.signature(new ObjectKey(imageFile.getName()+" "+imageFile.lastModified()));
                Glide
                        .with(context)
                        .setDefaultRequestOptions(options)
                        .load(imageFile)
                        .into(pvh.userPicture);
            }else
                pvh.userPicture.setVisibility(View.GONE);
        }else
            pvh.userPicture.setVisibility(View.GONE);

        pvh.rating.setText(thisReview.getRatingString());
        pvh.ratingBar.setRating(thisReview.getRating());

        if(thisReview.getNote() != null){
            if(thisReview.getNote().length() > 0)
                pvh.notes.setText(thisReview.getNote());
            else
                pvh.notes.setVisibility(View.GONE);
        }else
            pvh.notes.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
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
