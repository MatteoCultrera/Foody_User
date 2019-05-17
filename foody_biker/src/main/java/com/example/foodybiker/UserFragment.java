package com.example.foodybiker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private TextView city;
    private CircleImageView profilePicture;
    private TextView monTime, tueTime, wedTime, thuTime, friTime, satTime,sunTime;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private File storageDir;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;
    private FirebaseAuth firebaseAuth;
    private MaterialButton logout;
    private String imagePath;

    public UserFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view){
        sharedPref = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        profilePicture = view.findViewById(R.id.profilePicture);
        this.editMode = view.findViewById(R.id.edit_mode);
        this.name = view.findViewById(R.id.userName);
        this.email = view.findViewById(R.id.emailAddress);
        this.address = view.findViewById(R.id.address);
        this.phoneNumber = view.findViewById(R.id.phoneNumber);
        this.city = view.findViewById(R.id.city);
        this.logout = view.findViewById(R.id.logout_button);

        monTime = view.findViewById(R.id.monTime);
        tueTime = view.findViewById(R.id.tueTime);
        wedTime = view.findViewById(R.id.wedTime);
        thuTime = view.findViewById(R.id.thuTime);
        friTime = view.findViewById(R.id.friTime);
        satTime = view.findViewById(R.id.satTime);
        sunTime = view.findViewById(R.id.sunTime);

        //setup of the Shared Preferences to save value in (key, value) format
        if (!email.getText().toString().equals("email")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
                    Query query = database.child(firebaseAuth.getCurrentUser().getUid()).child("info");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                BikerInfo info = dataSnapshot.getValue(BikerInfo.class);
                                name.setText(info.getUsername());
                                email.setText(info.getEmail());
                                address.setText(info.getAddress());
                                phoneNumber.setText(info.getNumberPhone());
                                city.setText(info.getCity());
                                imagePath = info.getPath();
                                monTime.setText(info.getDaysTime().get(0));
                                tueTime.setText(info.getDaysTime().get(1));
                                wedTime.setText(info.getDaysTime().get(2));
                                thuTime.setText(info.getDaysTime().get(3));
                                friTime.setText(info.getDaysTime().get(4));
                                satTime.setText(info.getDaysTime().get(5));
                                sunTime.setText(info.getDaysTime().get(6));
                                edit.putString("name", info.getUsername());
                                edit.putString("email", info.getEmail());
                                if (!address.getText().toString().equals(getResources().getString(R.string.address_hint)))
                                    edit.putString("address", info.getAddress());
                                if (!phoneNumber.getText().toString().equals(getResources().getString(R.string.phone_hint)))
                                    edit.putString("phoneNumber", info.getNumberPhone());
                                if (!city.getText().toString().equals(getResources().getString(R.string.city_hint)))
                                    edit.putString("city", info.getCity());
                                if (!info.getDaysTime().get(0).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("monState", true);
                                }
                                if (!info.getDaysTime().get(1).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("tueState", true);
                                }
                                if (!info.getDaysTime().get(2).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("wedState", true);
                                }
                                if (!info.getDaysTime().get(3).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("thuState", true);
                                }
                                if (!info.getDaysTime().get(4).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("friState", true);
                                }
                                if (!info.getDaysTime().get(5).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("satState", true);
                                }
                                if (!info.getDaysTime().get(6).equals(getResources().getString(R.string.free))){
                                    edit.putBoolean("sunState", true);
                                }

                                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

                                if(imagePath!=null){
                                    mStorageRef.child(imagePath).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Glide
                                                            .with(profilePicture.getContext())
                                                            .load(uri)
                                                            .into(profilePicture);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Glide
                                                    .with(profilePicture.getContext())
                                                    .load(R.drawable.profile_placeholder)
                                                    .into(profilePicture);
                                        }
                                    });
                                }else{
                                    Glide
                                            .with(profilePicture.getContext())
                                            .load(R.drawable.profile_placeholder)
                                            .into(profilePicture);
                                }
                                edit.putString("monTime", info.getDaysTime().get(0));
                                edit.putString("tueTime", info.getDaysTime().get(1));
                                edit.putString("wedTime", info.getDaysTime().get(2));
                                edit.putString("thuTime", info.getDaysTime().get(3));
                                edit.putString("friTime", info.getDaysTime().get(4));
                                edit.putString("satTime", info.getDaysTime().get(5));
                                edit.putString("sunTime", info.getDaysTime().get(6));
                                edit.putString("Path", imagePath);

                                edit.apply();
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
                            email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
                            address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
                            phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
                            city.setText(sharedPref.getString("city", getResources().getString(R.string.city_hint)));
                            monTime.setText(sharedPref.getString("monTime", getResources().getString(R.string.free)));
                            tueTime.setText(sharedPref.getString("tueTime", getResources().getString(R.string.free)));
                            wedTime.setText(sharedPref.getString("wedTime", getResources().getString(R.string.free)));
                            thuTime.setText(sharedPref.getString("thuTime", getResources().getString(R.string.free)));
                            friTime.setText(sharedPref.getString("friTime", getResources().getString(R.string.free)));
                            satTime.setText(sharedPref.getString("satTime", getResources().getString(R.string.free)));
                            sunTime.setText(sharedPref.getString("sunTime", getResources().getString(R.string.free)));
                        }
                    });
                }
                }).run();
            }

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setup.class);
                File pl = new File(storageDir, PLACEHOLDER_CAMERA);
                if(!pl.delete()){
                    System.out.println("Delete Failure");
                }
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                sharedPref.edit().clear().apply();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init(Objects.requireNonNull(getView()));
    }
}
