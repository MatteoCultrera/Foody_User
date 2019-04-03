package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


//The image ratio is 7:4

public class User extends AppCompatActivity {

    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private TextView monTime, tueTime, wedTime, thuTime, friTime, satTime,sunTime;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private final String PROFILE_IMAGE = "ProfileImage.jpg";
    private File storageDir;

    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        //Shared Preferences definition
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);


        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        firstStart();
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
            edit.putString("name",getString(R.string.namerosso));

        if(!sharedPref.contains("email"))
            edit.putString("email",getString(R.string.mail_rosso));

        if(!sharedPref.contains("address"))
            edit.putString("address",getString(R.string.address_rosso));

        if(!sharedPref.contains("phoneNumber"))
            edit.putString("phoneNumber",getString(R.string.phone_rosso));


        File f = new File(storageDir, PROFILE_IMAGE);

        if(!f.exists()){
            Bitmap pizza = BitmapFactory.decodeResource(this.getResources(), R.drawable.pizza);
            saveBitmap(pizza, f.getPath());
        }

        edit.apply();

    }

    private void saveBitmap(Bitmap bitmap,String path){
        if(bitmap!=null){
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){

        ImageView profilePicture = findViewById(R.id.profilePicture);
        this.editMode = findViewById(R.id.edit_mode);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);

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

        monTime.setText(sharedPref.getString("monTime", getResources().getString(R.string.Closed)));
        tueTime.setText(sharedPref.getString("tueTime", getResources().getString(R.string.Closed)));
        wedTime.setText(sharedPref.getString("wedTime", getResources().getString(R.string.Closed)));
        thuTime.setText(sharedPref.getString("thuTime", getResources().getString(R.string.Closed)));
        friTime.setText(sharedPref.getString("friTime", getResources().getString(R.string.Closed)));
        satTime.setText(sharedPref.getString("satTime", getResources().getString(R.string.Closed)));
        sunTime.setText(sharedPref.getString("sunTime", getResources().getString(R.string.Closed)));


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
        monTime.setText(savedInstanceState.getString("monTime", getResources().getString(R.string.Closed)));
        tueTime.setText(savedInstanceState.getString("tueTime", getResources().getString(R.string.Closed)));
        wedTime.setText(savedInstanceState.getString("wedTime", getResources().getString(R.string.Closed)));
        thuTime.setText(savedInstanceState.getString("thuTime", getResources().getString(R.string.Closed)));
        friTime.setText(savedInstanceState.getString("friTime", getResources().getString(R.string.Closed)));
        satTime.setText(savedInstanceState.getString("satTime", getResources().getString(R.string.Closed)));
        sunTime.setText(savedInstanceState.getString("sunTime", getResources().getString(R.string.Closed)));
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
