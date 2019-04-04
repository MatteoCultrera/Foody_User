package com.example.foodybiker;
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


public class User extends AppCompatActivity {

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
                Intent intent = new Intent(User.this, Setup.class);
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

    private void init(){

        CircleImageView profilePicture = findViewById(R.id.profilePicture);
        this.editMode = findViewById(R.id.edit_mode);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);
        this.city = findViewById(R.id.city);

        this.monTime = findViewById(R.id.monTime);
        this.tueTime = findViewById(R.id.tueTime);
        this.wedTime = findViewById(R.id.wedTime);
        this.thuTime = findViewById(R.id.thuTime);
        this.friTime = findViewById(R.id.friTime);
        this.satTime = findViewById(R.id.satTime);
        this.sunTime = findViewById(R.id.sunTime);

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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
        outState.putString("city", city.getText().toString());
        outState.putString("monTime", monTime.getText().toString());
        outState.putString("tueTime", tueTime.getText().toString());
        outState.putString("wedTime", wedTime.getText().toString());
        outState.putString("thuTime", thuTime.getText().toString());
        outState.putString("friTime", friTime.getText().toString());
        outState.putString("satTime", satTime.getText().toString());
        outState.putString("sunTime", sunTime.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        name.setText(savedInstanceState.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(savedInstanceState.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(savedInstanceState.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(savedInstanceState.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        city.setText(savedInstanceState.getString("city", getResources().getString(R.string.city_hint)));
        monTime.setText(savedInstanceState.getString("monTime", getResources().getString(R.string.free)));
        tueTime.setText(savedInstanceState.getString("tueTime", getResources().getString(R.string.free)));
        wedTime.setText(savedInstanceState.getString("wedTime", getResources().getString(R.string.free)));
        thuTime.setText(savedInstanceState.getString("thuTime", getResources().getString(R.string.free)));
        friTime.setText(savedInstanceState.getString("friTime", getResources().getString(R.string.free)));
        satTime.setText(savedInstanceState.getString("satTime", getResources().getString(R.string.free)));
        sunTime.setText(savedInstanceState.getString("sunTime", getResources().getString(R.string.free)));
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

        setContentView(R.layout.activity_profile);

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
}
