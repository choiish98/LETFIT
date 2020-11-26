package com.example.letfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

/*
<intent-filter>
<action android:name="android.intent.action.MAIN" />

<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
*/

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainAcitivty";
    private LineChart mChart;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 가로 모드 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            // 자동 로그인
            gotoActivity(LogInActivity.class);
        } else {
            // 회원 정보 유무 확인
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                gotoActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        // 육각형 기능
        RadarChart radarChart = findViewById(R.id.radarChart);
        radarChart.getDescription().setEnabled(false);

        radarChart.setWebColor(Color.WHITE);        // 세로줄 없애기
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

        // 곡선 그래프
        mChart = (LineChart) findViewById(R.id.lineChart);

        //mChart.setOnChartGestureListener(MainActivity.this);
        //mChart.setOnChartValueSelectedListener(MainActivity.this);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setGridBackgroundColor(Color.WHITE);
        mChart.setBorderColor(Color.WHITE);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 20f));
        yValues.add(new Entry(1, 35f));
        yValues.add(new Entry(2, 46f));
        yValues.add(new Entry(3, 50f));
        yValues.add(new Entry(4, 10f));
        yValues.add(new Entry(5, 60f));
        yValues.add(new Entry(6, 30f));

        LineDataSet set1 = new LineDataSet(yValues, "Data set 1");

        set1.setFillAlpha(110);
        set1.setColor(rgb(150, 31, 47));
        set1.setLineWidth(2f);
        set1.setValueTextColor(Color.WHITE);
        set1.setCircleColor(rgb(150, 31, 47));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);
        // 곡선 그래프

        //막대 그래프
        barChart = findViewById(R.id.barChart);

        barChart.setDrawBarShadow(false);
        barChart.setMaxVisibleValueCount(50);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();


        barEntries.add(new BarEntry(1,5f));
        barEntries.add(new BarEntry(2,10f));
        barEntries.add(new BarEntry(3,15f));
        barEntries.add(new BarEntry(4,10f));
        barEntries.add(new BarEntry(5,30f));
        barEntries.add(new BarEntry(6,25f));

        BarDataSet barDataSet = new BarDataSet(barEntries,"bar Data Set1");
        barDataSet.setColor(rgb(150, 31, 47));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        //막대 그래프

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
                    gotoActivity(LogInActivity.class);
                    break;
                case R.id.gotoSNS:
                    gotoActivity(SnsActivity.class);
                    break;
            }
        }
    };

    // intent Acitivity 정의
    private void gotoActivity(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
        startActivity(intent);
    }
}

