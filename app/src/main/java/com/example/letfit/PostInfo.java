package com.example.letfit;

import java.util.ArrayList;
import java.util.Date;

public class PostInfo {
    private String title;       // 닉네임
    private ArrayList<String> contents;    // 내용
    private String publisher;   // 작성자
    private Date createDate; // 생성일
    private String likes; // 좋아요

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createDate, String likes){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createDate = createDate;
        this.likes = likes;
    }


    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){ this.title = title; }

    public ArrayList<String> getContents(){
        return this.contents;
    }
    public void setContents(ArrayList<String> contents){ this.contents = contents; }

    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){ this.publisher = publisher; }

    public Date getCreateDate(){
        return this.createDate;
    }
    public void setCreateDate(Date createDate){ this.createDate = createDate; }

    public String getLikes(){
        return this.likes;
    }
    public void setLikes(Date createDate){ this.likes = likes; }

}
