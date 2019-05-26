package com.example.foodyuser;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowInfoFragment extends Fragment {

    Restaurant showingRestaurant;

    SpinKitView loading;
    ScrollView scroll;
    TextView phoneNumber, email;

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
        phoneNumber = view.findViewById(R.id.phoneNumber);
        email = view.findViewById(R.id.emailAddress);

        if(showingRestaurant == null){
            loading.setVisibility(View.VISIBLE);
            scroll.setVisibility(View.GONE);
        }else {
            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void init(Restaurant showingRestaurant){
        this.showingRestaurant = showingRestaurant;

        if(loading != null && scroll != null){


            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
        }
    }

    private void setInterface(){
        //TODO: add phone number to restaurant class

        phoneNumber.setText("");
        phoneNumber.setHint(getResources().getString(R.string.phone_hint));

        //TODO: add email to restaurant class

        email.setText("");
        email.setHint(getResources().getString(R.string.email_hint));



    }
}
