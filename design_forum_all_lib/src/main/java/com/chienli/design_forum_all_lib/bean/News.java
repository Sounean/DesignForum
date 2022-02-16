package com.chienli.design_forum_all_lib.bean;

import java.io.Serializable;

public class News implements Serializable {


    /**
     * cover : https://www.cyhfwq.top:443/designForum/images/news/1555934199630.png
     * id : 33
     * userId : 22
     * newsType : 3
     * newsTitle : 日本新生代顶级设计师佐藤大
     * newsText :
     * newsImg : null
     * path : null
     * newTime : 2019-04-22 19:56:39.0
     * show : 1
     * userName : 5号教师
     * userHead : head.png
     */

    private String cover;
    private int id;
    private int userId;
    private String newsType;
    private String newsTitle;
    private String newsText;
    private Object newsImg;
    private Object path;
    private String newTime;
    private String show;
    private String userName;
    private String userHead;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public Object getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(Object newsImg) {
        this.newsImg = newsImg;
    }

    public Object getPath() {
        return path;
    }

    public void setPath(Object path) {
        this.path = path;
    }

    public String getNewTime() {
        return newTime;
    }

    public void setNewTime(String newTime) {
        this.newTime = newTime;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }
}
