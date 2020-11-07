package com.example.letfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
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
        // 육각형

        // 로그아웃 버튼
        findViewById(R.id.logOutBtn).setOnClickListener(onClickListener);
        // 로그아웃 버튼
    }

    // onClickListener 정의
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.logOutBtn:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    // onClickListener 정의
}

