package com.dbw.hotel.model;

/**
 * Created by DBW on 2017/1/3.
 * 订单表
 */
public class HotelOrder {

    private String hotel;                   //酒店
    private String orderID;                 //订单ID
    private String customer;                //顾客
    private String beginTime;
    private String endTime;                 //订房和退房时间
    private double orderPrice;              //价格




    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }
}
