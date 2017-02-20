package com.dbw.hotel.model;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by DBW on 2017/1/3.
 * 顾客表
 */
public class Customer extends DataSupport {

    private String name,IDCard ;            //姓名和身份证号
    private String sex ;                    //性别
    private String phone;                   //电话号码
    private List<HotelOrder> orders ;       //订单


    public List<HotelOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<HotelOrder> orders) {
        this.orders = orders;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }
}
