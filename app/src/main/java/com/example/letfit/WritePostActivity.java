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
import android.widget.RelativeLayout;
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
    private FirebaseUser user;                                // 유저
    private ArrayList<String> pathList = new ArrayList<>();   // 사진 경로 변수
    private LinearLayout parent;                              // image 받는 layout
    private int pathCount, successCount;
    private RelativeLayout buttonsBackgroudLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        buttonsBackgroudLayout = findViewById(R.id.buttonsBackgroudLayout);
        parent = findViewById(R.id.contentsLayout);

        buttonsBackgroudLayout.setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    selectedEditText = null;
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    gotoActivity(GalleryActivity.class, 0);
                    break;
                case R.id.buttonsBackgroudLayout:
                    if(buttonsBackgroudLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroudLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    gotoActivity(GalleryActivity.class, 1);
                    buttonsBackgroudLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    parent.removeView((View)selectedImageView.getParent());
                    buttonsBackgroudLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    // 찍은 사진 파일 경로 받아오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0 :
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath); // 사진 추가할 때마다 arraylist에 추가

                    // create edittext, imageView in ViewGroup
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText == null){
                        parent.addView(linearLayout);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(linearLayout, i + 1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonsBackgroudLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });
                    Glide.with(this)
                            .load(profilePath)
                            .override(1000)
                            .into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    Glide.with(this)
                            .load(profilePath)
                            .override(1000)
                            .into(selectedImageView);
                }
                break;
        }
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                selectedEditText = (EditText) view;
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
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();

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
                    final  StorageReference mountainImagesRef = storageRef.child("posts/"+documentReference.getId()+"/"+ pathCount +".jpg");
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
                                            dbUpLoader(documentReference, writeInfo);                                                  // db upload

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
            if(pathList.size() == 0){
                WriteInfo writeInfo = new WriteInfo(title, contentlist, user.getUid(), new Date());    // 회원 정보 객체 (MemberInfo.java)
                dbUpLoader(documentReference, writeInfo);
            }
        } else {
            startToast("게시글 제목을 입력해주세요.");
        }
    }

    // DB 등록
    private void dbUpLoader(DocumentReference documentReference, WriteInfo writeInfo){
        documentReference.set(writeInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
    }

    // toast
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    // intent Acitivity 정의
    private void gotoActivity(Class c, int requestCode) {
        Intent intent = new Intent(WritePostActivity.this, c);
        startActivityForResult(intent, requestCode);
    }
}
