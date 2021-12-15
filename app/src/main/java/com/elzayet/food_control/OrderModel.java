package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class OrderModel {
    private String phoneNumber,orderId ,date,time,orderStatus,orderPrice;

    public OrderModel() { }

    public OrderModel(String phoneNumber, String orderPrice, String orderId, String date, String time, String orderStatus) {
        this.phoneNumber = phoneNumber;
        this.orderPrice = orderPrice;
        this.orderId = orderId;
        this.date = date;
        this.time = time;
        this.orderStatus = orderStatus;
    }


    public String getOrderId() { return orderId; }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getOrderStatus() {  return orderStatus;    }

    public String getOrderPrice() {
        return orderPrice;
    }
}