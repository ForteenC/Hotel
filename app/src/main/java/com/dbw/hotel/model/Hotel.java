package com.dbw.hotel.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DBW on 2016/12/28.
 * 酒店类，可序列化
 */
public class Hotel   implements Parcelable{
    private String name;                        //酒店名
    private String location;                    //酒店位置
    private int level;                          //酒店星级
    private String introduce;                   //酒店简介
    private String content;                     //酒店介绍
    private String imageUrl;                    //图片ID


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //存数据
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeInt(level);
        parcel.writeString(introduce);
        parcel.writeString(content);
        parcel.writeString(imageUrl);
    }

    //创建一个Creator用于对象的反序列化
    public static final Parcelable.Creator<Hotel> CREATOR = new Creator<Hotel>() {
        @Override
        public Hotel createFromParcel(Parcel parcel) {
            return new Hotel(parcel);
        }

        @Override
        public Hotel[] newArray(int i) {
            return new Hotel[i];
        }
    };

    //从parcel中取数据
    private Hotel(Parcel parcel){
        name = parcel.readString();
        location = parcel.readString();
        level = parcel.readInt();
        introduce = parcel.readString();
        content = parcel.readString();
        imageUrl = parcel.readString();
    }
}
