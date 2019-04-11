package com.example.foodyrestaurant;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.foodyrestaurant.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private FloatingActionButton editMode;
    private TextView name;
    private TextView email;
    private TextView address;
    private TextView phoneNumber;
    private TextView monTime, tueTime, wedTime, thuTime, friTime, satTime,sunTime;
    private TextView delivPrice, foodType;
    private ImageView profilePicture, profileShadow;
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private final String PROFILE_IMAGE = "ProfileImage.jpg";
    private File storageDir;
    private boolean allowRefresh = true;

    private SharedPreferences sharedPref;


    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void init(View view){

        this.profilePicture = view.findViewById(R.id.profilePicture);
        this.profileShadow = view.findViewById(R.id.shadow);
        this.editMode = view.findViewById(R.id.edit_mode);
        this.name = view.findViewById(R.id.userName);
        this.email = view.findViewById(R.id.emailAddress);
        this.address = view.findViewById(R.id.address);
        this.phoneNumber = view.findViewById(R.id.phoneNumber);

        this.monTime = view.findViewById(R.id.monTime);
        this.tueTime = view.findViewById(R.id.tueTime);
        this.wedTime = view.findViewById(R.id.wedTime);
        this.thuTime = view.findViewById(R.id.thuTime);
        this.friTime = view.findViewById(R.id.friTime);
        this.satTime = view.findViewById(R.id.satTime);
        this.sunTime = view.findViewById(R.id.sunTime);
        this.delivPrice = view.findViewById(R.id.delivery);
        this.foodType = view.findViewById(R.id.food_type);

        //setup of the Shared Preferences to save value in (key, value) format

        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));

        int deliveryPrice;

        deliveryPrice = sharedPref.getInt("delivPrice",5);
        foodType.setText(sharedPref.getString("foodType", getResources().getString(R.string.food_type_unselect)));

        monTime.setText(sharedPref.getString("monTime", getResources().getString(R.string.Closed)));
        tueTime.setText(sharedPref.getString("tueTime", getResources().getString(R.string.Closed)));
        wedTime.setText(sharedPref.getString("wedTime", getResources().getString(R.string.Closed)));
        thuTime.setText(sharedPref.getString("thuTime", getResources().getString(R.string.Closed)));
        friTime.setText(sharedPref.getString("friTime", getResources().getString(R.string.Closed)));
        satTime.setText(sharedPref.getString("satTime", getResources().getString(R.string.Closed)));
        sunTime.setText(sharedPref.getString("sunTime", getResources().getString(R.string.Closed)));

        double price = 0.5 * deliveryPrice;

        String text = String.format(Locale.getDefault(),"%.2f", price) + getResources().getString(R.string.value);
        delivPrice.setText(text);


        File f = new File(storageDir, PROFILE_IMAGE);

        RequestOptions glideOptions = new RequestOptions()
                .signature(new ObjectKey(f.getPath()+f.lastModified()));

        if(f.exists()){
            Glide
                    .with(this)
                    .load(f)
                    .apply(glideOptions)
                    .into(profilePicture);
        }

        Glide
                .with(this)
                .load(R.drawable.shadow)
                .into(profileShadow);

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
}
