package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


//The image ratio is 7:4

public class User extends AppCompatActivity {

    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private File storageDir;

    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        //Shared Preferences definition
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);

        firstStart();

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, com.example.foodyrestaurant.Setup.class);
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
            edit.putString("name",getString(R.string.namerosso));

        if(!sharedPref.contains("email"))
            edit.putString("email",getString(R.string.mail_rosso));

        if(!sharedPref.contains("address"))
            edit.putString("address",getString(R.string.address_rosso));

        if(!sharedPref.contains("phoneNumber"))
            edit.putString("phoneNumber",getString(R.string.phone_rosso));

        edit.apply();

    }

    private void init(){

        CircleImageView profilePicture = findViewById(R.id.profilePicture);
        this.editMode = findViewById(R.id.edit_mode);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);

        //setup of the Shared Preferences to save value in (key, value) format

        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));

        String PROFILE_IMAGE = "ProfileImage.jpg";
        File f = new File(storageDir, PROFILE_IMAGE);

        if(f.exists()){
            profilePicture.setImageURI(Uri.fromFile(f));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        name.setText(savedInstanceState.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(savedInstanceState.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(savedInstanceState.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(savedInstanceState.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
    }

    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setContentView(R.layout.activity_profile);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, com.example.foodyrestaurant.Setup.class);
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

        setContentView(R.layout.activity_profile);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, com.example.foodyrestaurant.Setup.class);
                File pl = new File(storageDir, PLACEHOLDER_CAMERA);
                if(!pl.delete()){
                    System.out.println("Delete Failure");
                }
                startActivity(intent);
            }
        });


    }
}
