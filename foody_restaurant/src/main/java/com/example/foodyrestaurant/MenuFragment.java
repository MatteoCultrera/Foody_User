package com.example.foodyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MenuFragment extends Fragment {

    private RecyclerView menu;
    private final String JSON_COPY = "menuCopy.json";
    private final String JSON_PATH = "menu.json";
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Card> cards;

    public MenuFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menu = view.findViewById(R.id.menu_display);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onPause(){
        super.onPause();
        File file = new File(storageDir, JSON_PATH);
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, file);
    }

    private void init(View view){
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        menu.setLayoutManager(llm);

        FloatingActionButton editMode = view.findViewById(R.id.edit_mode);
        ImageView profileImage = view.findViewById(R.id.mainImage);
        ImageView profileShadow = view.findViewById(R.id.shadow);

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);
        Glide
                .with(this)
                .load(R.drawable.pizza)
                .into(profileImage);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsMenu").child("RossoPomodoro");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    cards = new ArrayList<>();
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        Card card = ds2.getValue(Card.class);
                        for (DataSnapshot ds3 : ds2.getChildren()){
                            if (ds3.getKey().compareTo("Dish") == 0){
                                ArrayList<Dish> dishes = new ArrayList<>();
                                for (DataSnapshot ds4 : ds3.getChildren()) {
                                    Dish dish = ds4.getValue(Dish.class);
                                    dishes.add(dish);
                                }
                                card.setDishes(dishes);
                            }
                        }
                        cards.add(card);
                    }
                    RVAdapter adapter = new RVAdapter(cards);
                    menu.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File jsonTemp = new File(storageDir,JSON_COPY);
                if(jsonTemp.exists()) {
                    if(!jsonTemp.delete()){
                        System.out.println("Cannot delete the file.");
                    }
                }
                Intent intent = new Intent(getActivity(), MenuEdit.class);
                startActivity(intent);
            }
        });
    }
}
