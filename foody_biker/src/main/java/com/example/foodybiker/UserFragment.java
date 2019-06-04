package com.example.foodybiker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
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
        this.logout = view.findViewById(R.id.logout_button);

        monTime = view.findViewById(R.id.monTime);
        tueTime = view.findViewById(R.id.tueTime);
        wedTime = view.findViewById(R.id.wedTime);
        thuTime = view.findViewById(R.id.thuTime);
        friTime = view.findViewById(R.id.friTime);
        satTime = view.findViewById(R.id.satTime);
        sunTime = view.findViewById(R.id.sunTime);

        name.setText(sharedPref.getString("name",getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email",getResources().getString(R.string.email_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        monTime.setText(sharedPref.getString("monTime", getResources().getString(R.string.free)));
        tueTime.setText(sharedPref.getString("tueTime", getResources().getString(R.string.free)));
        wedTime.setText(sharedPref.getString("wedTime", getResources().getString(R.string.free)));
        thuTime.setText(sharedPref.getString("thuTime", getResources().getString(R.string.free)));
        friTime.setText(sharedPref.getString("friTime", getResources().getString(R.string.free)));
        satTime.setText(sharedPref.getString("satTime", getResources().getString(R.string.free)));
        sunTime.setText(sharedPref.getString("sunTime", getResources().getString(R.string.free)));

        File profileImage = new File(sharedPref.getString("imgLocale", ""));
        if(profileImage.exists()){
            RequestOptions options = new RequestOptions();
            options.signature(new ObjectKey(profileImage.getName()+" "+profileImage.lastModified()));
            Glide
                    .with(view)
                    .setDefaultRequestOptions(options)
                    .load(profileImage.getPath())
                    .into(profilePicture);
        }else{
            Glide
                    .with(profilePicture.getContext())
                    .load(R.drawable.profile_placeholder)
                    .into(profilePicture);
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
                File profile = new File(sharedPref.getString("imgLocale",""));
                if(profile.exists())
                    profile.delete();
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
