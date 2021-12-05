package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class UserModel {
    private String userPassword, userPinCode, userEmail, userName, phoneNumber, userRefellar, signupDate;


    public UserModel() { }

    public UserModel(String userPassword, String userPinCode, String userEmail, String userName, String phoneNumber, String userRefellar, String signupDate) {
        this.userPassword = userPassword;
        this.userPinCode = userPinCode;
        this.userEmail = userEmail;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.userRefellar = userRefellar;
        this.signupDate = signupDate;
    }

    public String getUserPassword() { return userPassword; }

    public String getUserPinCode() { return userPinCode; }

    public String getUserEmail() { return userEmail; }

    public String getUserName() { return userName; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getUserRefellar() { return userRefellar; }

    public String getSignupDate() { return signupDate; }
}
