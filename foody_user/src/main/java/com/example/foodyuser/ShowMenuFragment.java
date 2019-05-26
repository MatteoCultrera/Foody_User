package com.example.foodyuser;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowMenuFragment extends Fragment {


    private ArrayList<Card> cards;
    private View currentView;
    private RecyclerView recyclerMenu;
    private RVAdapterShowRestaurantMenu adapter;
    private RVAdapterShowRestaurantMenu show;

    public ShowMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        currentView = inflater.inflate(R.layout.fragment_show_menu, container, false);
        recyclerMenu = currentView.findViewById(R.id.recycler_menu);
        LinearLayoutManager llm = new LinearLayoutManager(currentView.getContext());
        recyclerMenu.setLayoutManager(llm);
        if(cards!=null){
            Log.d("MAD","Initialised on createVIew()");
            show = new RVAdapterShowRestaurantMenu(cards);
            recyclerMenu.setAdapter(show);
        }
        return currentView;
    }

    public void init(ArrayList<Card> cards){
        Log.d("MAD","Called Init");
       this.cards = cards;

       for(Card d: cards)
           for (Dish i : d.getDishes())
               Log.d("MAD","Dish "+i.getDishName()+" "+(i.getImage()==null?"has no Image":"has Image"));

       RVAdapterShowRestaurantMenu show = new RVAdapterShowRestaurantMenu(cards);
       if(recyclerMenu != null)
           recyclerMenu.setAdapter(show);
    }

}
