package com.example.letfit;

import android.content.Intent;
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

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.log_in_btn).setOnClickListener(onClickListener); //로그인 버튼 클릭 함수
        findViewById(R.id.gotosignup).setOnClickListener(onClickListener); //회원가입 버튼 클릭 함수
        findViewById(R.id.gotoPasswordResetBtn).setOnClickListener(onClickListener); //비밀번호 재설정 버튼 클릭 함수
    }


    // 뒤로가기 막음
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {  //클릭 했을 때
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.log_in_btn: //로그인 버튼을 클릭 했을 때
                    login();
                    break;
                case R.id.gotosignup:
                    startMyActivity(signUpActivity.class);
                    break;
                case R.id.gotoPasswordResetBtn:
                    startMyActivity(PasswordReset.class);
                    break;
            }
        }
    };

    // intent 함수 정의
    private void startMyActivity(Class c) {
        Intent intent = new Intent(LogInActivity.this, c);
        startActivity(intent);
    }

    private void login() {     //회원가입 함수
        String email = ((EditText)findViewById(R.id.user_id)).getText().toString();   //이메일
        String password = ((EditText)findViewById(R.id.user_pw)).getText().toString();  //비밀번호

        if(email.length() > 0 && password.length() > 0) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("로그인에 성공하였습니다.");
                                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 메인 뒤로가기 막음
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (task.getException() != null) { // 회원가입 실패 시 에러 코드 출력
                                        startToast(task.getException().toString());
                                    }
                                    // ...
                                }

                                // ...
                            }
                        });
            } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
