package com.example.foodyuser;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foody_library.Review;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowReviewFragment extends Fragment {


    private ArrayList<Review> reviews;
    private View currentView;
    private RecyclerView recyclerMenu;
    private RVAdapterShowRestaurantReviews show;
    private ConstraintLayout noReviews;
    SpinKitView loading;
    RestaurantView father;

    public ShowReviewFragment() {
        // Required empty public constructor
    }

    public void setFather(RestaurantView father){
        this.father = father;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_show_review, container, false);
        noReviews = currentView.findViewById(R.id.no_reviews);
        loading = currentView.findViewById(R.id.spin_kit_reviews);
        recyclerMenu = currentView.findViewById(R.id.recycler_reviews);
        LinearLayoutManager llm = new LinearLayoutManager(currentView.getContext());
        recyclerMenu.setLayoutManager(llm);
        if(reviews!=null && reviews.size() > 0){
            father.enableAddReview();
            Log.d("MAD","Reviews Initialised on createVIew()");
            recyclerMenu.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            noReviews.setVisibility(View.GONE);
            show = new RVAdapterShowRestaurantReviews(reviews, father);
            recyclerMenu.setAdapter(show);
        }else if(reviews != null && reviews.size() == 0){
            father.enableAddReview();
            loading.setVisibility(View.GONE);
            recyclerMenu.setVisibility(View.GONE);
            noReviews.setVisibility(View.VISIBLE);
        } else{
            recyclerMenu.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            noReviews.setVisibility(View.GONE);
        }
        return currentView;
    }

    public void dataChanged(){
        show.notifyDataSetChanged();
    }

    public void init(ArrayList<Review> reviews){

        this.reviews = reviews;

        father.enableAddReview();
        show = new RVAdapterShowRestaurantReviews(reviews, father);
        if(recyclerMenu != null && reviews.size() > 0){
            recyclerMenu.setVisibility(View.VISIBLE);
            noReviews.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            recyclerMenu.setAdapter(show);
        }
        if(recyclerMenu!=null && reviews.size() == 0){
            loading.setVisibility(View.GONE);
            noReviews.setVisibility(View.VISIBLE);
            recyclerMenu.setVisibility(View.GONE);
        }
    }

    public void notifyAdded(int position){
        show.notifyItemInserted(position);
        for(Review r : reviews){
            Log.d("PROVADUE", "Reviews on fragment "+r.getUserName()+" "+r.getUserID());
        }
        Log.d("PROVADUE","Added element at position "+position+" with size "+reviews.size());
        show.notifyItemRangeChanged(position, reviews.size());
    }

    public void removeReviews(){
        Log.d("PROVA","removeCards with"+(recyclerMenu == null?"no Recycler":"Recycler"));
        this.reviews = null;
    }

    public boolean notReady(){
        return reviews == null;
    }

}
