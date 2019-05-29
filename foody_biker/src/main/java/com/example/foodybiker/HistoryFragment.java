package com.example.foodybiker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private TextView distance;
    private BarChart barChart;
    private PieChart pieChart;
    private HashMap<Integer, Integer> frequency = new HashMap<>();
    private Integer count;
    private int delivered;
    private int orders;

    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        distance = view.findViewById(R.id.distance_biker);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
        for(int i = 0; i < 24; i++){
            frequency.put(i, 0);
        }
        delivered = 0;
        orders = 0;
        return view;
    }
/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("archive")
                .child("Bikers").child(firebaseUser.getUid());
        Log.d("BIKERLOG", "" + firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot ds2 : ds.getChildren()) {
                        Log.d("SRSRSR", "time: " + ds2.child("orderTime"));
                        Integer time = Integer.parseInt(ds2.child("orderTime").getValue(String.class).split(":")[0]);
                        count = frequency.get(time) + 1;
                        frequency.put(time, count);

                        if(ds2.child("status").getValue().equals("delivered")) {
                            delivered++;
                        }
                        orders++;
                    }
                }
                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(delivered));
                entries.add(new PieEntry(orders-delivered));
                PieDataSet dataSet = new PieDataSet(entries, "Orders Results");
                ArrayList<Integer> colors = new ArrayList<>();

                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                pieChart.setData(data);
                Log.d("SRSRSR", "orders: "+ orders + " delivered: " + delivered);

                drawChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void drawChart() {
        barChart.setDrawBarShadow(false);
        barChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinimum(0f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setEnabled(false);
        leftAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        List<BarEntry> yVals1 = new ArrayList<>();

        Iterator it = frequency.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Integer> pair = (Map.Entry) it.next();
            yVals1.add(new BarEntry(pair.getKey(), pair.getValue()));
        }

        BarDataSet set = new BarDataSet(yVals1, null);
        BarData data = new BarData(set);
        data.setDrawValues(false);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();
    }
*/
}
