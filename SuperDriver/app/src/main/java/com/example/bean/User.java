package com.example.bean;

import java.io.Serializable;

public class User implements Serializable {
    private String phoneNumber;
    private String userName;
    private String passWord;
    private String userEmail;
    private String userSex = "女";
    private int userAge;
    private String userIntroduction;
    private String vehicleType;
    private String userHeadImage;
    private String userProvince = "吉林";


    public User(String phoneNumber, String userName, String passWord, String userEmail, String userSex, int userAge, String userIntroduction, String vehicleType, String userHeadImage) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.passWord = passWord;
        this.userEmail = userEmail;
        this.userSex = userSex;
        this.userAge = userAge;
        this.userIntroduction = userIntroduction;
        this.vehicleType = vehicleType;
        this.userHeadImage = userHeadImage;
    }

    public User() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserIntroduction() {
        return userIntroduction;
    }

    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getUserHeadImage() {
        return userHeadImage;
    }

    public void setUserHeadImage(String userHeadImage) {
        this.userHeadImage = userHeadImage;
    }

    public String getUserProvince() {
        return userProvince;
    }

    public void setUserProvince(String userProvince) {
        this.userProvince = userProvince;
    }
}
