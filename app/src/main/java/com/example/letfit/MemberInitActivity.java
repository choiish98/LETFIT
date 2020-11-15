package com.example.letfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MemberInitActivity extends AppCompatActivity {

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

    private void profileUpdate() {     // 프로필 업데이트 함수
        String name = ((EditText)findViewById(R.id.nameeditText)).getText().toString();   // 이름

        if(name.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            if(user != null){
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startToast("회원 정보 등록을 성공하였습니다.");
                                    finish();
                                }
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
