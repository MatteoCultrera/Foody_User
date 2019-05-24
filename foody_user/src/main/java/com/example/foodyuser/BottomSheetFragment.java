package com.example.foodyuser;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    MaterialButton delete;
    TimePicker picker;
    private SharedPreferences sharedPreferences;
    TextView title;


    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        delete = view.findViewById(R.id.cancel_button);
        title = view.findViewById(R.id.pickerTitle);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String restTime = sharedPreferences.getString("restTime", "");
        Log.d("MAD", "orario del ristorante -> " + restTime);

        picker = view.findViewById(R.id.time_picker);

        picker.setIs24HourView(true);

        /* per matte, se cambi vista tutto da eliminare
        String[] time = restTime.split( " - ");
        String restClose = "";
        if(!restTime.equals("")) {
            restClose = time[1];
            title.setText(String.format(Locale.getDefault(), "%s %s %s", getString(R.string.select_time),
                    getString(R.string.select_time_until), restClose));
        }


        int currMin = picker.getMinute();
        int currHour = picker.getHour();

        if(currMin >= 30) {
            currHour++;
            currMin-=30;
        } else {
            currMin+=30;
        }

        picker.setMinute(currMin);
        picker.setHour(currHour);

        final int finCurrHour = currHour;
        final int finCurrMin = currMin;

        Log.d("MAD", ""+restTime);
        String[] dividedTime = null;
        Integer closeHour = 0;
        Integer closeMinute = 0;
        if(!restTime.equals("")) {
            dividedTime = restClose.split(":");
            closeHour = Integer.valueOf(dividedTime[0]);
            closeMinute = Integer.valueOf(dividedTime[1]);
        }

        final int finCloseHour = closeHour;
        final int finCloseMinute = closeMinute;

        Log.d("MAD", "close " + finCloseHour + ":" + finCloseMinute);

        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("MAD", ""+hourOfDay+" " + minute);
                if(hourOfDay ==  finCurrHour && minute < finCurrMin) {
                    Toast.makeText(picker.getContext(), "Not possible to place an order in the past!", Toast.LENGTH_SHORT).show();
                    picker.setHour(finCurrHour);
                    picker.setMinute(finCurrMin);
                } else if (hourOfDay >= finCloseHour) {
                    Toast.makeText(picker.getContext(), "The restaurant cannot prepare your order in time", Toast.LENGTH_SHORT).show();
                    picker.setHour(finCloseHour);
                    picker.setMinute(finCloseMinute);
                }
            }
        });
        */

        return view;
    }


}
