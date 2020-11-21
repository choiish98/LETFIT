package com.example.letfit;

public class WriteInfo {
    private String title;       // 닉네임
    private String contents;    // 내용
    private String publisher;   // 작성자

    public WriteInfo(String title, String contents, String publisher){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
    }


    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){ this.title = title; }

    public String getContents(){
        return this.contents;
    }
    public void setContents(String contents){ this.contents = contents; }

    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){ this.publisher = publisher; }
}
