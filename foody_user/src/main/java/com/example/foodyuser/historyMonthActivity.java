package com.example.foodyuser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class historyMonthActivity extends Activity {

    private ImageButton back;
    private LinearLayout monthsLayout;
    private SharedPreferences shared;

    Map<String, ArrayList<String>> calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_month);

        back = findViewById(R.id.calendar_back);
        monthsLayout = findViewById(R.id.layout_calendar);
        shared = getSharedPreferences("myPreference", MODE_PRIVATE);
        calendar = new HashMap<>();

        init();
    }

    private void init(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchMonths();

    }

    private void fetchMonths(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("archive").child("user").child(shared.getString("id",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d("DATAFETCH",""+ds.getKey());
                    Pattern pattern = Pattern.compile("[0-9]+-[0-9]+");
                    Matcher matcher = pattern.matcher(ds.getKey());
                    if(matcher.matches()){
                        String[] parts = ds.getKey().split("-");
                        String month = parts[0];
                        String year = parts[1];
                        if(!calendar.containsKey(year)){
                            ArrayList<String> newMonth = new ArrayList<>();
                            newMonth.add(month);
                            calendar.put(year, newMonth);
                        }else {
                            calendar.get(year).add(month);
                        }
                    }
                }

                if(calendar.keySet().size() == 0){
                    Log.d("PROVA","NO History");
                    //NO History
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View v = inflater.inflate(R.layout.calendar_layout, monthsLayout, false);
                    TextView year = v.findViewById(R.id.calendar_year);
                    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                    year.setText(String.format("%d",thisYear));
                    MaterialButton jan = v.findViewById(R.id.calendar_jan);
                    MaterialButton feb = v.findViewById(R.id.calendar_feb);
                    MaterialButton mar = v.findViewById(R.id.calendar_mar);
                    MaterialButton apr = v.findViewById(R.id.calendar_apr);
                    MaterialButton may = v.findViewById(R.id.calendar_may);
                    MaterialButton jun = v.findViewById(R.id.calendar_jun);
                    MaterialButton jul = v.findViewById(R.id.calendar_jul);
                    MaterialButton aug = v.findViewById(R.id.calendar_aug);
                    MaterialButton sep = v.findViewById(R.id.calendar_sep);
                    MaterialButton oct = v.findViewById(R.id.calendar_oct);
                    MaterialButton nov = v.findViewById(R.id.calendar_nov);
                    MaterialButton dec = v.findViewById(R.id.calendar_dec);

                    jan.setClickable(false);
                    jan.setBackgroundResource(R.drawable.calendar_disabled_background);
                    jan.setElevation(0);

                    feb.setClickable(false);
                    feb.setBackgroundResource(R.drawable.calendar_disabled_background);
                    feb.setElevation(0);

                    mar.setClickable(false);
                    mar.setBackgroundResource(R.drawable.calendar_disabled_background);
                    mar.setElevation(0);

                    apr.setClickable(false);
                    apr.setBackgroundResource(R.drawable.calendar_disabled_background);
                    apr.setElevation(0);

                    may.setClickable(false);
                    may.setBackgroundResource(R.drawable.calendar_disabled_background);
                    may.setElevation(0);

                    jun.setClickable(false);
                    jun.setBackgroundResource(R.drawable.calendar_disabled_background);
                    jun.setElevation(0);

                    jul.setClickable(false);
                    jul.setBackgroundResource(R.drawable.calendar_disabled_background);
                    jul.setElevation(0);

                    aug.setClickable(false);
                    aug.setBackgroundResource(R.drawable.calendar_disabled_background);
                    aug.setElevation(0);

                    sep.setClickable(false);
                    sep.setBackgroundResource(R.drawable.calendar_disabled_background);
                    sep.setElevation(0);

                    oct.setClickable(false);
                    oct.setBackgroundResource(R.drawable.calendar_disabled_background);
                    oct.setElevation(0);

                    nov.setClickable(false);
                    nov.setBackgroundResource(R.drawable.calendar_disabled_background);
                    nov.setElevation(0);

                    dec.setClickable(false);
                    dec.setBackgroundResource(R.drawable.calendar_disabled_background);
                    dec.setElevation(0);

                    monthsLayout.addView(v);
                }else{

                }

                for(String s : calendar.keySet()){
                    Log.d("PROVA",""+s);
                    for(String b: calendar.get(s))
                        Log.d("PROVA","    "+b);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
