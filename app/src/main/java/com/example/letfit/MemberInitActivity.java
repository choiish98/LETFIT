package com.example.letfit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberInitActivity extends AppCompatActivity {
    private static final String TAG = "MemberInitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        findViewById(R.id.checkbtn).setOnClickListener(onClickListener); // 로그인 버튼 클릭 함수
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {  //클릭 했을 때
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbtn: // 확인 버튼을 클릭 했을 때
                    profileUpdate();
                    break;
            }
        }
    };

    // 뒤로가기 막기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // 프로필 업데이트
    private void profileUpdate() {
        String name = ((EditText)findViewById(R.id.nameeditText)).getText().toString();   // 이름
        String phone = ((EditText)findViewById(R.id.phoneeditText)).getText().toString();   // 전화번호
        String birth = ((EditText)findViewById(R.id.birtheditText)).getText().toString();   // 생일
        String address = ((EditText)findViewById(R.id.addresseditText)).getText().toString();   // 주소

        if(name.length() > 0 && phone.length() > 9 && birth.length() > 5 && address.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // cloud 초기화
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // DB set
            MemberInfo memberInfo = new MemberInfo(name, phone, birth, address); // 회원 정보 객체 (MemberInfo.java)
            if(user != null) {
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                startToast("회원 정보 등록을 성공하였습니다.");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("회원 정보 등록에 실패하였습니다.");
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        } else {
            startToast("회원 정보를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
