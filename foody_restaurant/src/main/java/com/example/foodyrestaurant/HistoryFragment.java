package com.example.foodyrestaurant;

import android.graphics.Color;
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
import com.github.mikephil.charting.components.Legend;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private TextView income;
    private BarChart barChart;
    private PieChart pieChart;
    private HashMap<Integer, Integer> frequency = new HashMap<>();
    private Float amount;
    private Integer accepted, rejected;

    public HistoryFragment() {}

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        income = view.findViewById(R.id.tot_income);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
        for(int i = 0; i < 24; i++){
            frequency.put(i, 0);
        }
        accepted = 0;
        rejected = 0;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("archive")
                .child("restaurant").child(firebaseUser.getUid());

        DatabaseReference databaseFrequency = databaseReference.child("frequency");
        databaseFrequency.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        frequency.put(Integer.parseInt(ds.getKey()), ds.getValue(Integer.class));
                    }
                } else {
                    for (int i = 0; i < 24; i ++){
                        frequency.put(i, 0);
                    }
                }

                drawChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().compareTo("amount") == 0) {
                        amount = ds.getValue(Float.class);
                    }
                    if(ds.getKey().compareTo("accepted") == 0) {
                        accepted = ds.getValue(Integer.class);
                    }
                    if(ds.getKey().compareTo("rejected") == 0) {
                        rejected = ds.getValue(Integer.class);
                    }
                }
                income.setText(String.format(Locale.getDefault(), "%.2f \u20ac", amount));
                drawPieCharts();
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

    public void drawPieCharts() {
        //pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        //pieChart.setCenterTextTypeface(tfLight);

        //chart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(accepted));
        entries.add(new PieEntry(rejected));
        PieDataSet dataSet = new PieDataSet(entries, "Orders Results");
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(5f);

        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        Log.d("SRSRSR", "rejected: "+ rejected + " accepted: " + accepted);
    }
}