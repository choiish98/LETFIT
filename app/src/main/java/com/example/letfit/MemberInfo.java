package com.example.letfit;

import android.widget.EditText;

public class MemberInfo {
    private String nickName; // 닉네임
    private String weight;   // 몸무게
    private String height;   // 현재 키
    private String photoUrl; // 프로필 사진

    public MemberInfo(String nickName, String weight, String height){
        this.nickName = nickName;
        this.weight = weight;
        this.height = height;
    }

    public MemberInfo(String nickName, String weight, String height, String photoUrl){
        this.nickName = nickName;
        this.weight = weight;
        this.height = height;
        this.photoUrl = photoUrl;
    }

    public String getNickName(){
        return this.nickName;
    }
    public void setNickName(String name){
        this.nickName = nickName;
    }

    public String getWeight(){
        return this.weight;
    }
    public void setWeight(String phone){
        this.weight = weight;
    }

    public String getHeight(){
        return this.height;
    }
    public void setHeight(String birth){
        this.height = height;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String address){ this.photoUrl = photoUrl; }
}
