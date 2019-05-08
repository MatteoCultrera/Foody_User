package com.example.foodyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private final String PROFILE_IMAGE = "ProfileImage.jpg";
    private File storageDir;
    private SharedPreferences sharedPref;
    private TextView monTime, tueTime, wedTime, thuTime, friTime, satTime, sunTime;
    private TextView name, email, address, phoneNumber, delivPrice, foodType;
    private FirebaseAuth firebaseAuth;
    private ImageView profilePicture;
    private SharedPreferences.Editor edit;

    public UserFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        sharedPref = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        profilePicture = view.findViewById(R.id.profilePicture);
        final ImageView profileShadow = view.findViewById(R.id.shadow);
        FloatingActionButton editMode = view.findViewById(R.id.edit_mode);
        name = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.emailAddress);
        address = view.findViewById(R.id.address);
        phoneNumber = view.findViewById(R.id.phoneNumber);

        monTime = view.findViewById(R.id.monTime);
        tueTime = view.findViewById(R.id.tueTime);
        wedTime = view.findViewById(R.id.wedTime);
        thuTime = view.findViewById(R.id.thuTime);
        friTime = view.findViewById(R.id.friTime);
        satTime = view.findViewById(R.id.satTime);
        sunTime = view.findViewById(R.id.sunTime);
        delivPrice = view.findViewById(R.id.delivery);
        foodType = view.findViewById(R.id.food_type);

        //setup of the Shared Preferences to save value in (key, value) format
        if (!email.getText().toString().equals("email")) {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("restaurantsInfo");
            Query query = database.child(firebaseAuth.getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        RestaurantInfo info = ds.getValue(RestaurantInfo.class);
                        name.setText(info.getUsername());
                        email.setText(info.getEmail());
                        address.setText(info.getAddress());
                        phoneNumber.setText(info.getNumberPhone());
                        int deliveryPrice;
                        deliveryPrice = info.getDeliveryPrice();
                        double price = 0.5 *  deliveryPrice;
                        String text = String.format(Locale.UK,"%.2f", price) + getResources().getString(R.string.value);
                        delivPrice.setText(text);
                        String[] foodCategories = getResources().getStringArray(R.array.foodcategory_array);
                        ArrayList<Integer> cuisine = info.getCuisineTypes();
                        String cuisineText = "";
                        if (cuisine != null){
                            for(int i = 0; i < cuisine.size(); i++){
                                cuisineText += foodCategories[cuisine.get(i)];
                                if(i!= cuisine.size()-1)
                                    cuisineText+=", ";
                            }
                        }
                        foodType.setText(cuisineText);
                        ArrayList<String> days = info.getDaysTime();
                        monTime.setText(days.get(0));
                        tueTime.setText(days.get(1));
                        wedTime.setText(days.get(2));
                        thuTime.setText(days.get(3));
                        friTime.setText(days.get(4));
                        satTime.setText(days.get(5));
                        sunTime.setText(days.get(6));
                        edit.putString("name", info.getUsername());
                        edit.putString("email", info.getEmail());
                        edit.putString("foodType", cuisineText);
                        edit.putInt("delivPrice", info.getDeliveryPrice());
                        if (!address.getText().toString().equals(getResources().getString(R.string.address_hint)))
                            edit.putString("address", info.getAddress());
                        if (!phoneNumber.getText().toString().equals(getResources().getString(R.string.phone_hint)))
                            edit.putString("phoneNumber", info.getNumberPhone());
                        if (!info.getDaysTime().get(0).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("monState", true);
                        }
                        if (!info.getDaysTime().get(1).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("tueState", true);
                        }
                        if (!info.getDaysTime().get(2).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("wedState", true);
                        }
                        if (!info.getDaysTime().get(3).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("thuState", true);
                        }
                        if (!info.getDaysTime().get(4).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("friState", true);
                        }
                        if (!info.getDaysTime().get(5).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("satState", true);
                        }
                        if (!info.getDaysTime().get(6).equals(getResources().getString(R.string.Closed))){
                            edit.putBoolean("sunState", true);
                        }
                        edit.putString("monTime", info.getDaysTime().get(0));
                        edit.putString("tueTime", info.getDaysTime().get(1));
                        edit.putString("wedTime", info.getDaysTime().get(2));
                        edit.putString("thuTime", info.getDaysTime().get(3));
                        edit.putString("friTime", info.getDaysTime().get(4));
                        edit.putString("satTime", info.getDaysTime().get(5));
                        edit.putString("sunTime", info.getDaysTime().get(6));
                        edit.apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("SWSW", databaseError.getMessage());
                    name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
                    email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
                    address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
                    phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
                    delivPrice.setText(sharedPref.getInt("delivPrice", 5));
                    foodType.setText(sharedPref.getString("foodType", ""));
                    monTime.setText(sharedPref.getString("monTime", getResources().getString(R.string.Closed)));
                    tueTime.setText(sharedPref.getString("tueTime", getResources().getString(R.string.Closed)));
                    wedTime.setText(sharedPref.getString("wedTime", getResources().getString(R.string.Closed)));
                    thuTime.setText(sharedPref.getString("thuTime", getResources().getString(R.string.Closed)));
                    friTime.setText(sharedPref.getString("friTime", getResources().getString(R.string.Closed)));
                    satTime.setText(sharedPref.getString("satTime", getResources().getString(R.string.Closed)));
                    sunTime.setText(sharedPref.getString("sunTime", getResources().getString(R.string.Closed)));
                }
            });
        }

        File f = new File(storageDir, PROFILE_IMAGE);

        RequestOptions glideOptions = new RequestOptions()
                .signature(new ObjectKey(f.getPath()+f.lastModified()));

        if(f.exists()){
            Glide
                    .with(this)
                    .load(f)
                    .apply(glideOptions)
                    .into(profilePicture);
        } else {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child("images/restaurants/" + firebaseAuth.getCurrentUser().getUid() + "_profile.jpeg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide
                                    .with(profilePicture.getContext())
                                    .load(uri)
                                    .into(profilePicture);

                            Glide
                                    .with(profileShadow.getContext())
                                    .load(R.drawable.shadow)
                                    .into(profileShadow);
                            //TODO: salvare l'uri qua nel path dell'immagine profilo
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

                init(Objects.requireNonNull(getView()));
    }
}
