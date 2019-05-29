package com.example.foodyuser;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foody_library.UserInfo;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {

    private CircleImageView profilePicture;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private TextView bio;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private File storageDir;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;
    private FirebaseAuth firebaseAuth;
    private MaterialButton logout;
    private String imagePath;
    private UserInfo info;

    public UserFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {;
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view){
        FloatingActionButton editMode;
        sharedPref = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        profilePicture = view.findViewById(R.id.profilePicture);
        editMode = view.findViewById(R.id.edit_mode);
        this.name = view.findViewById(R.id.userName);
        this.email = view.findViewById(R.id.emailAddress);
        this.address = view.findViewById(R.id.address);
        this.phoneNumber = view.findViewById(R.id.phoneNumber);
        this.bio = view.findViewById(R.id.bio);
        this.logout = view.findViewById(R.id.logout_button);

        //setup of the Shared Preferences to save value in (key, value) format
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("endUsers");
                Query query = database.child(firebaseAuth.getCurrentUser().getUid()).child("info");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        info = dataSnapshot.getValue(UserInfo.class);
                        name.setText(info.getUsername());
                        email.setText(info.getEmail());
                        address.setText(info.getAddress());
                        phoneNumber.setText(info.getNumberPhone());
                        bio.setText(info.getBiography());
                        edit.putString("name", info.getUsername());
                        edit.putString("email", info.getEmail());
                        imagePath = info.getImagePath();

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

                        if (!address.getText().toString().equals(getResources().getString(R.string.address_hint)))
                            edit.putString("address", info.getAddress());
                        if (!phoneNumber.getText().toString().equals(getResources().getString(R.string.phone_hint)))
                            edit.putString("phoneNumber", info.getNumberPhone());
                        if (!bio.getText().toString().equals(getResources().getString(R.string.bio_hint)))
                            edit.putString("bio", info.getBiography());
                        edit.putString("Path", imagePath);
                        edit.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
                        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
                        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
                        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
                        bio.setText(sharedPref.getString("bio", getResources().getString(R.string.bio_hint)));
                    }
                });
            }
        }).start();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setup.class);
                File pl = new File(storageDir, PLACEHOLDER_CAMERA);
                if(!pl.delete()){
                    System.out.println("Delete Failure");
                }
                intent.putExtra("imagePath", info.getImagePath());
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
    public void onResume() {
        super.onResume();
        Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init(Objects.requireNonNull(getView()));
    }
}
