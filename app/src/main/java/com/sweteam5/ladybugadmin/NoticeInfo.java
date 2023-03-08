package com.sweteam5.ladybugadmin;

import android.os.Parcel;
import android.os.Parcelable;

public class NoticeInfo{
    String title;   // Title string data
    String date;    // Datetime string data
    String content; // Content string data

    public NoticeInfo(String title,String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }
    public NoticeInfo() { }

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    public String getContent(){return this.content;}
    public void setContent(String content){this.content = content;}
    public String getDate(){return this.date;}
    public void setDate(String date){this.date = date;}


}
