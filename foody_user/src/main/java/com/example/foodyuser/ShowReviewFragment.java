package com.example.foodyuser;


import android.os.Bundle;
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
        loading = currentView.findViewById(R.id.spin_kit_reviews);
        recyclerMenu = currentView.findViewById(R.id.recycler_reviews);
        LinearLayoutManager llm = new LinearLayoutManager(currentView.getContext());
        recyclerMenu.setLayoutManager(llm);
        if(reviews!=null){
            Log.d("MAD","Reviews Initialised on createVIew()");
            recyclerMenu.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            show = new RVAdapterShowRestaurantReviews(reviews, father);
            recyclerMenu.setAdapter(show);
        }else{
            recyclerMenu.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }
        return currentView;
    }

    public void dataChanged(){
        show.notifyDataSetChanged();
    }

    public void init(ArrayList<Review> reviews){

        this.reviews = reviews;

        Log.d("PROVA","Init with"+(recyclerMenu == null?"no Recycler":"Recycler"));


        show = new RVAdapterShowRestaurantReviews(reviews, father);
        if(recyclerMenu != null){
            recyclerMenu.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            recyclerMenu.setAdapter(show);
        }
    }

    public void removeReviews(){
        Log.d("PROVA","removeCards with"+(recyclerMenu == null?"no Recycler":"Recycler"));
        this.reviews = null;
        if(recyclerMenu != null){
            recyclerMenu.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }
    }

    public boolean notReady(){
        return reviews == null;
    }

}
