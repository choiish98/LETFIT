package com.example.letfit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostAcitivty";
    private FirebaseUser user;                                      // 유저
    private ArrayList<String> pathList = new ArrayList<>();   // 사진 경로 변수
    private LinearLayout parent;                                            // image 받는 layout
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);

    }

    // 찍은 사진 파일 경로 받아오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0 : {
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath); // 사진 추가할 때마다 arraylist에 추가

                    // create edittext, imageView in ViewGroup
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this)
                            .load(profilePath)
                            .override(1000)
                            .into(imageView);
                    parent.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    parent.addView(editText);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    gotoActivity(GalleryActivity.class);
                    break;
            }
        }
    };

    // 게시글 등록
    private void storageUpload() {
        final String title = ((EditText)findViewById(R.id.titleEditText)).getText().toString();   //

        if(title.length() > 0) {
            final ArrayList<String> contentlist = new ArrayList<>();    // view 객체의 EditText를 담을 공간
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for(int i = 0; i < parent.getChildCount(); i++){
                View view = parent.getChildAt(i);
                if(view instanceof EditText) {
                    String text = ((EditText)view).getText().toString();
                    if(text.length() > 0) {
                        contentlist.add(text);
                    }
                } else {
                    contentlist.add(pathList.get(pathCount));
                    // db 등록 로직
                    final  StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/"+ pathCount +".jpg");
                    try{
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", ""+(contentlist.size()-1)).build();
                        UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentlist.set(index, uri.toString());
                                        successCount++;
                                        if(pathList.size() == successCount){
                                            //finish

                                            WriteInfo writeInfo = new WriteInfo(title, contentlist, user.getUid(), new Date());    // 회원 정보 객체 (MemberInfo.java)
                                            dbUpLoader(writeInfo);                                                  // db upload

                                            for(int a = 0; a < contentlist.size(); a++){
                                                Log.e("로그", "콘텐츠"+contentlist.get(a));
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }catch (FileNotFoundException e){
                        Log.e("로그", "에러: "+e.toString());
                    }
                    pathCount++;
                }
            }
        } else {
            startToast("게시글 내용을 입력해주세요.");
        }
    }

    // DB 등록
    private void dbUpLoader(WriteInfo writeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // DB set
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        startToast("Document written with ID: "+ documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    // toast
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    // intent Acitivity 정의
    private void gotoActivity(Class c) {
        Intent intent = new Intent(WritePostActivity.this, c);
        startActivityForResult(intent, 0);
    }
}
