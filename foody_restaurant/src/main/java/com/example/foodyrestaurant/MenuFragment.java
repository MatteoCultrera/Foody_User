package com.example.foodyrestaurant;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.foodyrestaurant.R;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    private FloatingActionButton editMode;
    RecyclerView menu;
    private ArrayList<Card> cards;
    LinearLayoutManager llm;
    ImageView profileImage, profileShadow;


    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menu = (RecyclerView) view.findViewById(R.id.menu_display);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        llm = new LinearLayoutManager(view.getContext());
        menu.setLayoutManager(llm);

        this.editMode = view.findViewById(R.id.edit_mode);
        this.profileImage = view.findViewById(R.id.mainImage);
        this.profileShadow = view.findViewById(R.id.shadow);

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);
        Glide
                .with(this)
                .load(R.drawable.pizza)
                .into(profileImage);

        cards = new ArrayList<>();
        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","pizza","2", null));
        dishes.add(new Dish("Paperino","pizza","2", null));
        dishes.add(new Dish("Fottiti","pizza","2", null));
        dishes.add(new Dish("Margerita","pizza","2", null));


        for(int i =0; i < 20;i++){
            Card c = new Card("Pizza "+i);
            c.setDishes(dishes);
            cards.add(c);
        }




        RVAdapter adapter = new RVAdapter(cards);
        menu.setAdapter(adapter);

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuEdit.class);
                startActivity(intent);
            }
        });

    }

}
