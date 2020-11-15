package com.example.letfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.graphics.Color.rgb;
// 빨리 다운 로드 해라 이것들아;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }

        // 육각형 기능
        RadarChart radarChart = findViewById(R.id.radarChart);
        radarChart.getDescription().setEnabled(false);

        radarChart.setWebColor(Color.WHITE);        // 세로줄 없애기
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColorInner(Color.WHITE);   // 가로줄 없애기
        radarChart.setWebAlpha(100);
        radarChart.getLegend().setTextColor(Color.WHITE);
        radarChart.getLegend().setTextColor(Color.WHITE);

        ArrayList<RadarEntry> visitorsForFirstWebsite = new ArrayList<>();
        visitorsForFirstWebsite.add(new RadarEntry(350));
        visitorsForFirstWebsite.add(new RadarEntry(250));
        visitorsForFirstWebsite.add(new RadarEntry(200));
        visitorsForFirstWebsite.add(new RadarEntry(400));
        visitorsForFirstWebsite.add(new RadarEntry(250));

        RadarDataSet radarDataSetForFirstWebsite = new RadarDataSet(visitorsForFirstWebsite, "website 1");
        radarDataSetForFirstWebsite.setColor(rgb(150, 31, 47));
        radarDataSetForFirstWebsite.setValueTextColor(rgb(150, 31, 47));
        radarDataSetForFirstWebsite.setFillColor(rgb(150, 31, 47));
        radarDataSetForFirstWebsite.setDrawFilled(true);
        radarDataSetForFirstWebsite.setFillAlpha(3000);
        radarDataSetForFirstWebsite.setDrawHighlightIndicators(false);
        radarDataSetForFirstWebsite.setDrawHighlightCircleEnabled(true);
        radarDataSetForFirstWebsite.setDrawValues(false);


        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSetForFirstWebsite);

        String[] labels = {"등", "가슴", "하체", "복부", "어깨"};

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(12f);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(100, false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMaximum(500);
        yAxis.setAxisMinimum(0);

        radarChart.getDescription().setText("Radar Chart Example");
        radarChart.setData(radarData);
        radarChart.invalidate();
        // 육각형

        // 버튼 정의
        findViewById(R.id.logOutBtn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSNS).setOnClickListener(onClickListener);
        // 버튼 정의
    }

    // onClickListener 정의
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.logOutBtn:
                    FirebaseAuth.getInstance().signOut();
                    gotoLoginActivity();
                    break;
                case R.id.gotoSNS:
                    gotoSnsActivity();
            }
        }
    };

    // intent Acitivity 정의
    private void gotoSnsActivity() {
        Intent intent = new Intent(MainActivity.this, SnsActivity.class);
        startActivity(intent);
    }
    private void gotoLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}

