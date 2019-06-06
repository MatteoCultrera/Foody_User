package com.example.foodyuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private TextView totalSpent;
    private Float amount;
    private Integer delivered, rejected;
    private PieChart pieChart;
    private MaterialButton button;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalSpent = view.findViewById(R.id.spent);
        pieChart = view.findViewById(R.id.pieChart);
        button = view.findViewById(R.id.enter_order_history);
        delivered = 0;
        rejected = 0;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryPickMonth.class);
                startActivity(intent);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("archive")
                .child("user").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().compareTo("delivered") == 0) {
                        delivered = ds.getValue(Integer.class);
                    }
                    if (ds.getKey().compareTo("rejected") == 0) {
                        rejected = ds.getValue(Integer.class);
                    }
                    if (ds.getKey().compareTo("amount") == 0) {
                        amount = ds.getValue(Float.class);
                    }
                }
                totalSpent.setText(String.format(Locale.getDefault(), "%.2f \u20ac", amount));
                drawPieCharts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void drawPieCharts() {
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getDescription().setEnabled(false);

        final ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(delivered, getResources().getString(R.string.text_delivered)));
        entries.add(new PieEntry(rejected, getResources().getString(R.string.text_rejected)));

        PieDataSet dataSet = new PieDataSet(entries, "Orders Results");
        int[] colors = {getResources().getColor(R.color.accept, getActivity().getTheme()),
                getResources().getColor(R.color.errorColor, getActivity().getTheme())};
        dataSet.setColors(colors);

        pieChart.setDrawEntryLabels(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(5f);

        dataSet.setDrawValues(false);
        pieChart.getLegend().setEnabled(false);

        int total = delivered+rejected;
        pieChart.setCenterText(total + "\n" + getResources().getString(R.string.text_orders));
        pieChart.setCenterTextSize(22f);
        pieChart.setNoDataText("NO ORDERS IN ARCHIVE RIGHT NOW");
        pieChart.animateXY(3000, 3000);

        final int totToText = total;
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(entries.get(0).equals(e)) {
                    pieChart.setCenterText(delivered + "\n" + getResources().getString(R.string.text_delivered));
                } else {
                    pieChart.setCenterText(rejected + "\n" + getResources().getString(R.string.text_rejected));
                }
            }

            @Override
            public void onNothingSelected() {
                pieChart.setCenterText(totToText + "\n" + getResources().getString(R.string.text_orders));
            }
        });
    }
}
