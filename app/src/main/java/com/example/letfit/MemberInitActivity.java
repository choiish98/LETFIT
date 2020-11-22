package com.example.letfit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
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

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView; // 프로필 사진
    private String profilePath;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener); // 프로필 사진 설정
        findViewById(R.id.checkBtn).setOnClickListener(onClickListener); // 회원정보 입력 버튼 클릭 함수
        findViewById(R.id.picture).setOnClickListener(onClickListener); // 촬영 버튼 클릭 함수
        findViewById(R.id.delete).setOnClickListener(onClickListener); // 갤러리 버튼 클릭 함수

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
                    CardView cardView = findViewById(R.id.buttonsCardView); // 카드뷰가 보임
                    if(cardView.getVisibility() == View.VISIBLE){
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.picture: // 촬영 버튼 클릭 시
                    gotoActivity(CameraActivity.class);
                    break;
                case R.id.delete: // 갤러리 클릭 시
                    gotoActivity(GalleryActivity.class);
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
                    Glide.with(this)
                            .load(profilePath)
                            .centerCrop()
                            .override(500)
                            .into(profileImageView);
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
            user = FirebaseAuth.getInstance().getCurrentUser();
            final  StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");


            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(nickName, weight, height); // 회원 정보 객체 (MemberInfo.java)
                upLoader(memberInfo);
            } else {
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

                                MemberInfo memberInfo = new MemberInfo(nickName, weight, height, downloadUri.toString()); // 회원 정보 객체 (MemberInfo.java)
                                upLoader(memberInfo);
                            } else {
                                startToast("회원 정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    Log.e("로그", "에러: "+e.toString());
                }
            }
        } else {
            startToast("회원 정보를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // DB 등록
    private void upLoader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // DB set
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

    // intent Acitivity 정의
    private void gotoActivity(Class c) {
        Intent intent = new Intent(MemberInitActivity.this, c);
        startActivityForResult(intent, 0);
    }
}
