package com.example.letfit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInitActivity extends AppCompatActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView; // 프로필 사진
    private String profilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener); // 프로필 사진 설정
        findViewById(R.id.checkBtn).setOnClickListener(onClickListener); // 로그인 버튼 클릭 함수
    }

    // click listener
    View.OnClickListener onClickListener = new View.OnClickListener() {  //클릭 했을 때
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkBtn: // 확인 버튼을 클릭 했을 때
                    profileUpdate();
                    break;
                case R.id.profileImageView: // 프로필 사진 클릭 시
                    gotoActivity(CameraActivity.class);
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

    // 찍은 사진 파일 경로 받아오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0 : {
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ", "profilePath:"+profilePath);

                    // 전송 받은 file string을 img로 생성하여 profileImage로 업데이트
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    ImageView img;
                    profileImageView.setImageBitmap(bmp);
                }
                break;
            }
        }
    }

    // 프로필 업데이트
    private void profileUpdate() {
        final String nickName = ((EditText)findViewById(R.id.nickNameEditText)).getText().toString();   // 닉네임
        final String weight = ((EditText)findViewById(R.id.weightEditText)).getText().toString();   // 몸무게
        final String height = ((EditText)findViewById(R.id.heightEditText)).getText().toString();   // 키

        if(nickName.length() > 0 && weight.length() > 0 && height.length() > 2) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Create a reference to 'images/mountains.jpg'
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final  StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");


            try{
                InputStream stream = new FileInputStream(new File(profilePath));
                UploadTask uploadTask = mountainImagesRef.putStream(stream);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.e("실패1", "실패");
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.e("성공", "성공"+downloadUri);// cloud 초기화
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // DB set
                            MemberInfo memberInfo = new MemberInfo(nickName, weight, height, downloadUri.toString()); // 회원 정보 객체 (MemberInfo.java)
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
                        } else {
                            // Handle failures
                            // ...
                            Log.e("실패2", "실패");
                        }
                    }
                });
            }catch (FileNotFoundException e){
                Log.e("로그", "에러: "+e.toString());
            }
        } else {
            startToast("회원 정보를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // intent Acitivity 정의
    private void gotoActivity(Class c) {
        Intent intent = new Intent(MemberInitActivity.this, c);
        startActivityForResult(intent, 0);
    }
}
