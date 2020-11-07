package com.example.letfit;

import android.content.Intent;
import android.os.Bundle;
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

public class signUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpAcitivty";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.gotosignup).setOnClickListener(onClickListener); //회원가입 버튼 클릭 함수
        findViewById(R.id.gotologin).setOnClickListener(onClickListener); //로그인 버튼 클릭
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //ui
    }

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
            switch(view.getId()){
                case R.id.gotosignup: //회원가입버튼을 클릭 했을 때
                    sign();
                    break;
                case R.id.gotologin:
                    Intent intent = new Intent(signUpActivity.this, LogInActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void sign() {    //회원가입 함수
        String email = ((EditText)findViewById(R.id.user_id)).getText().toString();   //이메일
        String password = ((EditText)findViewById(R.id.user_pw)).getText().toString();  //비밀번호
        String passwordCheck = ((EditText)findViewById(R.id.user_pw_check)).getText().toString();  //비밀번호 확인

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) { // 비밀번호와 비밀번호 확인이 일치 할 때
                mAuth.createUserWithEmailAndPassword(email, password) //로그인 시도
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입에 성공하였습니다.");
                                    Intent intent = new Intent(signUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    if (task.getException() != null) { // 회원가입 실패 시 에러 코드 출력
                                        startToast(task.getException().toString());
                                    }
                                }
                            }
                        });
            } else { //비밀번호와 비밀번호 확인 불일치 시
                startToast("비밀번호가 일치하지 않습니다.");
            }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}