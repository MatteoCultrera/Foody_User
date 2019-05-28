package com.example.foodyuser;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowMenuFragment extends Fragment {


    private ArrayList<Card> cards;
    private View currentView;
    private RecyclerView recyclerMenu;
    private RVAdapterShowRestaurantMenu show;
    SpinKitView loading;
    RestaurantView father;
    private boolean isVisible;


    public ShowMenuFragment() {
        // Required empty public constructor
    }

    public void setFather(RestaurantView father){
        this.father = father;
    }


    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("PROVA","OnCreateView with"+(cards == null?"no Cards":"cards"));

        currentView = inflater.inflate(R.layout.fragment_show_menu, container, false);
        loading = currentView.findViewById(R.id.spin_kit);
        recyclerMenu = currentView.findViewById(R.id.recycler_menu);
        LinearLayoutManager llm = new LinearLayoutManager(currentView.getContext());
        recyclerMenu.setLayoutManager(llm);
        if(cards!=null){
            Log.d("MAD","Initialised on createVIew()");
            recyclerMenu.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            show = new RVAdapterShowRestaurantMenu(cards, father);
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

    public void init(ArrayList<Card> cards){
        if(isVisible)
            this.cards = cards;
        else
            return;

        Log.d("PROVA","Init with"+(recyclerMenu == null?"no Recycler":"Recycler"));


       show = new RVAdapterShowRestaurantMenu(cards, father);
       if(recyclerMenu != null){
           recyclerMenu.setVisibility(View.VISIBLE);
           loading.setVisibility(View.GONE);
           recyclerMenu.setAdapter(show);
       }
    }

    public void removeCards(){
        Log.d("PROVA","removeCards with"+(recyclerMenu == null?"no Recycler":"Recycler"));
        this.cards = null;
        if(recyclerMenu != null){
            recyclerMenu.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

    }

    public boolean getVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
