package com.example.letfit;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;



public class graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        RadarChart radarChart = findViewById(R.id.radarChart);

        ArrayList<RadarEntry> visitorsForFirstWebsite = new ArrayList<>();
        visitorsForFirstWebsite.add(new RadarEntry(420));
        visitorsForFirstWebsite.add(new RadarEntry(654));
        visitorsForFirstWebsite.add(new RadarEntry(540));
        visitorsForFirstWebsite.add(new RadarEntry(480));
        visitorsForFirstWebsite.add(new RadarEntry(040));
        visitorsForFirstWebsite.add(new RadarEntry(620));

        RadarDataSet radarDataSetForFirstWebsite = new RadarDataSet(visitorsForFirstWebsite, "website 1");
        radarDataSetForFirstWebsite.setColor(Color.RED);
        radarDataSetForFirstWebsite.setLineWidth(2f);
        radarDataSetForFirstWebsite.setValueTextColor(Color.RED);
        radarDataSetForFirstWebsite.setValueTextSize(14f);
        radarDataSetForFirstWebsite.setDrawFilled(true);
        radarDataSetForFirstWebsite.setFillColor(Color.RED);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSetForFirstWebsite);

        String[] labels = {"등", "어깨", "가슴", "팔", "복근", "하체"};

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        radarChart.getDescription().setText("Radar Chart Example");
        radarChart.setData(radarData);
    }
}