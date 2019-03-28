package com.example.foodyuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class User extends AppCompatActivity {

    private CircleImageView profilePicture;
    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private TextView bio;
    private final String PROFILE_IMAGE = "ProfileImage.jpg";

    //Shared Preferences definition
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, Setup.class);
                startActivity(intent);
            }
        });
    }

    private void init(){

        this.profilePicture = findViewById(R.id.profilePicture);
        this.editMode = findViewById(R.id.edit_mode);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);
        this.bio = findViewById(R.id.bio);

        //setup of the Shared Preferences to save value in (key, value) format
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();

        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        bio.setText(sharedPref.getString("bio", getResources().getString(R.string.bio_hint)));
        Bitmap b = BitmapFactory.decodeFile(this.getFilesDir() + "/" + PROFILE_IMAGE);
        profilePicture.setImageBitmap(b);
        edit.apply();

        edit.putString("name", name.getText().toString());
        edit.putString("email", email.getText().toString());
        edit.putString("address", address.getText().toString());
        edit.putString("phoneNumber", phoneNumber.getText().toString());
        edit.putString("bio", bio.getText().toString());
        edit.apply();
    }

    @Override
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

    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        name.setText(sharedPref.getString("name", name.getHint().toString()));
        email.setText(sharedPref.getString("email", email.getHint().toString()));
        address.setText(sharedPref.getString("address", address.getHint().toString()));
        phoneNumber.setText(sharedPref.getString("phoneNumber", phoneNumber.getHint().toString()));
        bio.setText(sharedPref.getString("bio", bio.getHint().toString()));
    }
}
