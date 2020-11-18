package com.example.letfit;

import android.widget.EditText;

public class MemberInfo {
    private String name; // 이름
    private String phone;   // 전화번호
    private String birth;   // 생일
    private String address;   // 주소

    public MemberInfo(String name, String phone, String birth, String address){
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.address = address;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getBirth(){
        return this.birth;
    }
    public void setBirth(String birth){
        this.birth = birth;
    }

    public String getAddress(){
        return this.name;
    }
    public void setAddress(String address){
        this.address = address;
    }
}
