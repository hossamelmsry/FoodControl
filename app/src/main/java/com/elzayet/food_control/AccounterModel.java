package com.elzayet.food_control;

public class AccounterModel {
    private String orderId ,date,time,phoneNumber,payment,orderPrice;

    public AccounterModel() { }

    public AccounterModel(String phoneNumber, String orderId, String orderPrice, String date,String time, String payment) {
        this.phoneNumber = phoneNumber;
        this.orderId     = orderId;
        this.payment     = payment;
        this.date        = date;
        this.time        = time;
        this.orderPrice = orderPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public String getPayment() {
        return payment;
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getOrderPrice() { return orderPrice; }
}
