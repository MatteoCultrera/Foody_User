package com.example.foodyuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class DiscoverFragment extends Fragment {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 9;

    private ImageButton searchButton;
    private TextView address;
    private boolean setted = false;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view){
        sharedPref = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        searchButton = view.findViewById(R.id.discover_search_button);
        address = view.findViewById(R.id.discover_search_address);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setted)
                    Toast.makeText(address.getContext(), R.string.address_not_selected, Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), RestaurantsList.class);
                    startActivity(intent);
                }
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressActivity();
            }
        });
    }

    public void addressActivity() {
        if (!Places.isInitialized()) {
            Places.initialize(address.getContext(), BuildConfig.ApiKey);
        }

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(address.getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("PLACE", "Place: " + place.getAddress() + " LAT_LNG " + place.getLatLng());
                //TODO: here we can start the query to search restaurant
                if(place.getAddress().equals(""))
                    setted = false;
                address.setText(place.getAddress());
                address.setTextColor(getContext().getColor(R.color.primaryText));
                setted = true;
                searchButton.setEnabled(true);
                edit.putString("delivery_address", place.getAddress());
                edit.apply();
            }
        }
    }
}
