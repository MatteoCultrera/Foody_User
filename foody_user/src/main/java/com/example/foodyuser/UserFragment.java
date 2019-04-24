package com.example.foodyuser;
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
    private TextView bio;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private File storageDir;
    private SharedPreferences sharedPref;

    public UserFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void firstStart(){

        SharedPreferences.Editor edit = sharedPref.edit();

        if(!sharedPref.contains("name"))
            edit.putString("name",getString(R.string.name_Walter));

        if(!sharedPref.contains("email"))
            edit.putString("email",getString(R.string.mail_Walter));

        if(!sharedPref.contains("address"))
            edit.putString("address",getString(R.string.address_Walter));

        if(!sharedPref.contains("phoneNumber"))
            edit.putString("phoneNumber",getString(R.string.phone_Walter));

        if(!sharedPref.contains("bio"))
            edit.putString("bio",getString(R.string.bio_Walter));

        edit.apply();

    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_user);

        //Shared Preferences definition
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);

        firstStart();

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }*/

    private void init(View view){

        CircleImageView profilePicture = view.findViewById(R.id.profilePicture);
        this.editMode = view.findViewById(R.id.edit_mode);
        this.name = view.findViewById(R.id.userName);
        this.email = view.findViewById(R.id.emailAddress);
        this.address = view.findViewById(R.id.address);
        this.phoneNumber = view.findViewById(R.id.phoneNumber);
        this.bio = view.findViewById(R.id.bio);

        //setup of the Shared Preferences to save value in (key, value) format

        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        bio.setText(sharedPref.getString("bio", getResources().getString(R.string.bio_hint)));

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

    @Override
    public void onResume() {
        super.onResume();
        Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        firstStart();

        init(Objects.requireNonNull(getView()));
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
        outState.putString("bio", bio.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        name.setText(savedInstanceState.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(savedInstanceState.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(savedInstanceState.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(savedInstanceState.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        bio.setText(savedInstanceState.getString("bio", getResources().getString(R.string.biography)));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setContentView(R.layout.fragment_user);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, Setup.class);
                File pl = new File(storageDir, PLACEHOLDER_CAMERA);
                if(!pl.delete()){
                    System.out.println("Delete Failure");
                }
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.fragment_user);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, Setup.class);
                File pl = new File(storageDir, PLACEHOLDER_CAMERA);
                if(!pl.delete()){
                    System.out.println("Delete Failure");
                }
                startActivity(intent);
            }
        });


    }*/
}
