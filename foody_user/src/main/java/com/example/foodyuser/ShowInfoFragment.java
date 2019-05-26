package com.example.foodyuser;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowInfoFragment extends Fragment {

    Restaurant showingRestaurant;

    SpinKitView loading;
    LinearLayout scroll;
    TextView phoneNumber, email, address, monTime, tueTime, wedTime, thuTime, friTime, satTime, sunTime;

    public ShowInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_info, container, false);
        // Inflate the layout for this fragment

        loading = view.findViewById(R.id.spin_kit_info);
        scroll = view.findViewById(R.id.scroll_info);
        phoneNumber = view.findViewById(R.id.phoneNumber_frag);
        email = view.findViewById(R.id.emailAddress_frag);
        address = view.findViewById(R.id.address_frag);
        monTime = view.findViewById(R.id.monTime_frag);
        tueTime = view.findViewById(R.id.tueTime_frag);
        wedTime = view.findViewById(R.id.wedTime_frag);
        thuTime = view.findViewById(R.id.thuTime_frag);
        friTime = view.findViewById(R.id.friTime_frag);
        satTime = view.findViewById(R.id.satTime_frag);
        sunTime = view.findViewById(R.id.sunTime_frag);


        if(showingRestaurant == null){
            loading.setVisibility(View.VISIBLE);
            scroll.setVisibility(View.GONE);
        }else {
            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
            setInterface();
        }

        return view;
    }

    public void init(Restaurant showingRestaurant){
        this.showingRestaurant = showingRestaurant;

        if(loading != null && scroll != null){
            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
            setInterface();
        }
    }

    private void setInterface(){

        if(showingRestaurant.getNumberPhone() != null){
            if(showingRestaurant.getNumberPhone().length() > 0){
                phoneNumber.setText(showingRestaurant.getNumberPhone());
            }else{
                phoneNumber.setText("");
                phoneNumber.setHint(getResources().getString(R.string.phone_hint));
            }
        }else{
            phoneNumber.setText("");
            phoneNumber.setHint(getResources().getString(R.string.phone_hint));
        }

        if(showingRestaurant.getEmail() != null){
            if(showingRestaurant.getEmail().length() > 0){
                email.setText(showingRestaurant.getEmail());
            }else{
                email.setText("");
                email.setHint(getResources().getString(R.string.email_hint));
            }
        }else{
            email.setText("");
            email.setHint(getResources().getString(R.string.email_hint));
        }


        if(showingRestaurant.getAddress() != null){
            if(showingRestaurant.getAddress().length() > 0){
                address.setText(showingRestaurant.getAddress());
            }else{
                address.setText("");
                address.setHint(getResources().getString(R.string.address_hint));
            }
        }else{
            address.setText("");
            address.setHint(getResources().getString(R.string.address_hint));
        }

        ArrayList<String> days = showingRestaurant.getDaysTime();

        monTime.setText(days.get(0));
        tueTime.setText(days.get(1));
        wedTime.setText(days.get(2));
        thuTime.setText(days.get(3));
        friTime.setText(days.get(4));
        satTime.setText(days.get(5));
        sunTime.setText(days.get(6));

    }
}
