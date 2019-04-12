package com.example.foodyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private FloatingActionButton editMode;
    RecyclerView menu;
    private JsonHandler jsonHandler;
    private ArrayList<Card> cards;
    LinearLayoutManager llm;

    ImageView profileImage, profileShadow;

    private final String JSON_PATH = "menu.json";
    private File storageDir;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menu = view.findViewById(R.id.menu_display);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        String json;
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(storageDir, JSON_PATH);
        llm = new LinearLayoutManager(view.getContext());
        menu.setLayoutManager(llm);
        cards = new ArrayList<>();

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

        jsonHandler = new JsonHandler();

        cards = jsonHandler.getCards(file);

        if (cards.size() == 0) {

        cards = new ArrayList<>();

        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","Pomodoro, Mozzarella, Basilico","3,50 €", null));
        dishes.add(new Dish("Vegetariana","Verdure di Stagione, Pomodoro, Mozzarella","8,00 €", null));
        dishes.add(new Dish("Quattro Stagioni","Pomodoro, Mozzarella, Prosciutto, Carciofi, Funghi, Olive, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Quattro Formaggi","Mozzarella, Gorgonzola, Fontina, Stracchino","7,00 €", null));
        Card c = new Card("PIZZA");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3,50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("PRIMI");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3,50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("SECONDI");
        c.setDishes(dishes);
        cards.add(c);

        }

        json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, file);

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
