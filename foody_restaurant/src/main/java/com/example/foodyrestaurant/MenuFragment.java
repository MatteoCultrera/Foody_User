package com.example.foodyrestaurant;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MenuFragment extends Fragment {

    private RecyclerView menu;
    private File storageDir;
    private final JsonHandler jsonHandler = new JsonHandler();
    private ArrayList<Card> cards;
    private FirebaseUser user;

    public MenuFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menu = view.findViewById(R.id.menu_display);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onPause(){
        super.onPause();
        final String JSON_PATH = "menu.json";
        File file = new File(storageDir, JSON_PATH);
        if(cards!= null){
            String json = jsonHandler.toJSON(cards);
            jsonHandler.saveStringToFile(json, file);

            DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                    .child("restaurantsMenu").child(user.getUid()).child("Card");
            HashMap<String, Object> child = new HashMap<>();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getDishes().size() != 0)
                    child.put(Integer.toString(i), cards.get(i));
            }
            database.updateChildren(child);
        }
    }

    private void init(View view){
        storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        menu.setLayoutManager(llm);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        final FloatingActionButton editMode = view.findViewById(R.id.edit_mode);
        final ImageView profileImage = view.findViewById(R.id.mainImage);
        final ImageView profileShadow = view.findViewById(R.id.shadow);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference databaseI = FirebaseDatabase.getInstance().getReference().child("restaurantsInfo");
                Query query = databaseI.child(firebaseAuth.getCurrentUser().getUid()).child("info");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RestaurantInfo info = dataSnapshot.getValue(RestaurantInfo.class);
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        if(info.getImagePath() != null) {
                            mStorageRef.child(info.getImagePath()).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide
                                                    .with(profileImage.getContext())
                                                    .load(uri)
                                                    .into(profileImage);
                                            Glide
                                                    .with(profileShadow.getContext())
                                                    .load(R.drawable.shadow)
                                                    .into(profileShadow);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Glide
                                                    .with(profileImage.getContext())
                                                    .load(R.drawable.profile_placeholder)
                                                    .into(profileImage);
                                            Glide
                                                    .with(profileShadow.getContext())
                                                    .load(R.drawable.shadow)
                                                    .into(profileShadow);
                                        }
                                    });
                        }else{
                            Glide
                                    .with(profileImage.getContext())
                                    .load(R.drawable.profile_placeholder)
                                    .into(profileImage);
                            Glide
                                    .with(profileShadow.getContext())
                                    .load(R.drawable.shadow)
                                    .into(profileShadow);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Glide
                                .with(profileImage.getContext())
                                .load(R.drawable.profile_placeholder)
                                .into(profileImage);
                        Glide
                                .with(profileShadow.getContext())
                                .load(R.drawable.shadow)
                                .into(profileShadow);
                    }
                });


                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = database.child("restaurantsMenu").child(user.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cards = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            for (DataSnapshot ds1 : ds.getChildren()) {
                                Card card = ds1.getValue(Card.class);
                                cards.add(card);
                            }
                        }
                        RVAdapter adapter = new RVAdapter(cards);
                        menu.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("SWSW", databaseError.getMessage());
                    }
                });
                editMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MenuEdit.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
