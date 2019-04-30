package com.example.foodybiker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView city;
    private TextView monTime, tueTime, wedTime, thuTime, friTime, satTime,sunTime;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private File storageDir;
    private SharedPreferences sharedPref;

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

        CircleImageView profilePicture = view.findViewById(R.id.profilePicture);
        this.editMode = view.findViewById(R.id.edit_mode);
        this.name = view.findViewById(R.id.userName);
        this.email = view.findViewById(R.id.emailAddress);
        this.address = view.findViewById(R.id.address);
        this.phoneNumber = view.findViewById(R.id.phoneNumber);
        this.city = view.findViewById(R.id.city);

        TextView monTime = view.findViewById(R.id.monTime);
        TextView tueTime = view.findViewById(R.id.tueTime);
        TextView wedTime = view.findViewById(R.id.wedTime);
        TextView thuTime = view.findViewById(R.id.thuTime);
        TextView friTime = view.findViewById(R.id.friTime);
        TextView satTime = view.findViewById(R.id.satTime);
        TextView sunTime = view.findViewById(R.id.sunTime);

        //setup of the Shared Preferences to save value in (key, value) format

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

        String PROFILE_IMAGE = "ProfileImage.jpg";
        File f = new File(storageDir, PROFILE_IMAGE);

        if(f.exists()){
            profilePicture.setImageURI(Uri.fromFile(f));
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

    }

    private void firstStart(){
        SharedPreferences.Editor edit = sharedPref.edit();

        if(!sharedPref.contains("name"))
            edit.putString("name",getString(R.string.name_foo));

        if(!sharedPref.contains("email"))
            edit.putString("email",getString(R.string.mail_foo));

        if(!sharedPref.contains("address"))
            edit.putString("address",getString(R.string.address_foo));

        if(!sharedPref.contains("phoneNumber"))
            edit.putString("phoneNumber",getString(R.string.phone_foo));

        if(!sharedPref.contains("city"))
            edit.putString("city",getString(R.string.city_foo));

        edit.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        firstStart();

        init(Objects.requireNonNull(getView()));
    }
}
