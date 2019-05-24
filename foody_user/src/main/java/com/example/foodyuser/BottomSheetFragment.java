package com.example.foodyuser;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    MaterialButton accept;
    NumberPicker picker, day;
    private SharedPreferences sharedPreferences;
    TextView title;
    String restTime;
    ArrayList<String> stringsHours, stringsDays;
    Order fatherClass;



    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public void setFatherClass(Order fatherClass){
        this.fatherClass = fatherClass;
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
        accept = view.findViewById(R.id.accept_button);
        title = view.findViewById(R.id.pickerTitle);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picker.getValue() != 0)
                    sharedPreferences.edit().putString("selectedTime",stringsHours.get(picker.getValue())).apply();
                else{
                    String soonAs = sharedPreferences.getString("minTime","");
                    Log.d("MAD",soonAs);
                    sharedPreferences.edit().putString("selectedTime",soonAs).apply();
                }
                fatherClass.updateTime();
                dismiss();
            }
        });
        stringsHours = new ArrayList<>();
        stringsDays = new ArrayList<>();

        restTime = sharedPreferences.getString("restTime", "");
        Log.d("MAD", "orario del ristorante -> " + restTime);

        picker = view.findViewById(R.id.time_picker);
        day = view.findViewById(R.id.day_picker);

        stringsHours.add(getResources().getString(R.string.soon_as_possible));
        generateTimes();

        stringsDays.add(getResources().getString(R.string.today));

        picker.setMinValue(0);
        picker.setMaxValue(stringsHours.size() - 1);

        day.setMinValue(0);
        day.setMaxValue(stringsDays.size()-1);

        //TODO: check if restaurant opens tomorrow
        //for now just today

        picker.setWrapSelectorWheel(false);

        String[] stringHoursArray = new String[stringsHours.size()];
        stringHoursArray = stringsHours.toArray(stringHoursArray);

        picker.setDisplayedValues(stringHoursArray);

        String[] stringDaysArray = new String[stringsDays.size()];
        stringDaysArray = stringsDays.toArray(stringDaysArray);

        day.setDisplayedValues(stringDaysArray);

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

    private void generateTimes(){
        String[] time = restTime.split( " - ");
        Log.d("MAD",time[1]);
        ArrayList<String> times = new ArrayList<>();
        String curTime = sharedPreferences.getString("minTime","");
        String[] hourMin = curTime.split(":");
        String[] hourMinClosed = time[1].split(":");

        int curHour = Integer.valueOf(hourMin[0]);
        int curMin = Integer.valueOf(hourMin[1]);

        int maxHour = Integer.valueOf(hourMinClosed[0]);
        int maxMin = Integer.valueOf(hourMinClosed[1]);

        Log.d("MAD", " before "+curHour+":"+curMin+" "+maxHour+":"+maxMin);

        if(maxMin >= 30){
            maxMin+=30;
        }else{
            maxMin = maxMin + 30;
            maxHour--;
        }

        curMin+=15;
        curMin = curMin/15*15;
        if(curMin >= 60){
            curHour++;
            curMin=curMin%60;
        }

        Log.d("MAD", " after "+curHour+":"+curMin+" "+maxHour+":"+maxMin);

        while(true){
            Log.d("MAD", curHour+":"+curMin+" "+maxHour+":"+maxMin);
            Log.d("MAD", curHour*60+curMin+" "+maxHour*60+maxMin);
            if(curHour*60+curMin > maxHour*60+maxMin){
                Log.d("MAD", "break");
                break;
            }
            stringsHours.add(String.format("%02d:%02d",curHour, curMin));
            curMin+=15;
            if(curMin>=60){
                curHour++;
                curMin = curMin%60;
            }
        }

    }

}
