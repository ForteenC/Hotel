package com.dbw.hotel.model;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by DBW on 2016/12/28.
 * 酒店表
 */
public class Hotel  extends DataSupport {
    private String name;                        //酒店名
    private int level;                          //酒店星级
    private String location;                    //酒店位置
    private int imageId;                        //图片ID
    private String content;                     //酒店介绍
    private List<HotelOrder> orders;            //订单

    public List<HotelOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<HotelOrder> orders) {
        this.orders = orders;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

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
}
