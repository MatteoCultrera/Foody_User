package com.example.foodyuser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foody_library.Review;
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
        Review current = reviews.get(i);
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
