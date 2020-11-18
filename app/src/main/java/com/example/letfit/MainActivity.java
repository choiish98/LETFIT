package com.example.letfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.graphics.Color.rgb;
// 빨리 다운 로드 해라 이것들아;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            // 자동 로그인
            gotoActivity(LogInActivity.class);
        } else {
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
                    gotoActivity(LogInActivity.class);
                    break;
                case R.id.gotoSNS:
                    gotoActivity(SnsActivity.class);
            }
        }
    };

    // intent Acitivity 정의
    private void gotoActivity(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
        startActivity(intent);
    }
}

