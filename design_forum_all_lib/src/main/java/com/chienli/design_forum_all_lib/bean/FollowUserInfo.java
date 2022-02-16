package com.chienli.design_forum_all_lib.bean;

public class FollowUserInfo {
    /**
     * id : 1
     * userPhone : 1
     * userPassword : 6512bd43d9caa6e02c990b0a82652dca
     * userName : 667888
     * userStatus : 1
     * userEmail : 576030221@qq.com
     * userGender : 1
     * userBirthday : 1999-02-22
     * userCity : 浙江 台州
     * userCareer : 艺术爱好者
     * userHead : 1.png
     * historyTime : 1554034300599
     */

    private int id;
    private String userPhone;
    private String userPassword;
    private String userName;
    private String userStatus;
    private String userEmail;
    private String userGender;
    private String userBirthday;
    private String userCity;
    private String userCareer;
    private String userHead;

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    private transient boolean isFollow = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCareer() {
        return userCareer;
    }

    public void setUserCareer(String userCareer) {
        this.userCareer = userCareer;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

}
