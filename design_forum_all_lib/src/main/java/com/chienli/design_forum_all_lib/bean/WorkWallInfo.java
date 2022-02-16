package com.chienli.design_forum_all_lib.bean;

import java.util.List;

public class WorkWallInfo {

    /**
     * id : 1
     * userId : 5
     * workType : 2
     * workLabels : 4
     * workLabelText : 包装
     * workText : 标题，5号老师发布的优设计
     * workImage : 1.jpg&2.jpg
     * workImageList : ["1.jpg","2.jpg"]
     * path : 1
     * workTime : 2018-12-14 16:59:36.0
     * workLikeSize : 45
     * workDiscussSize : 8
     * workCollect : 4
     * show : 1
     * userName : 5号
     * userHead : head.png
     */

    private int id;
    private int userId;
    private String workType;
    private int workLabels;
    private String workLabelText;
    private String workText;
    private String workImage;
    private String path;
    private String workTime;
    private int workLikeSize;
    private int workDiscussSize;
    private int workCollect;
    private int show;
    private String userName;
    private String userHead;
    private List<String> workImageList;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public int getWorkLabels() {
        return workLabels;
    }

    public void setWorkLabels(int workLabels) {
        this.workLabels = workLabels;
    }

    public String getWorkLabelText() {
        return workLabelText;
    }

    public void setWorkLabelText(String workLabelText) {
        this.workLabelText = workLabelText;
    }

    public String getWorkText() {
        return workText;
    }

    public void setWorkText(String workText) {
        this.workText = workText;
    }

    public String getWorkImage() {
        return workImage;
    }

    public void setWorkImage(String workImage) {
        this.workImage = workImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public int getWorkLikeSize() {
        return workLikeSize;
    }

    public void setWorkLikeSize(int workLikeSize) {
        this.workLikeSize = workLikeSize;
    }

    public int getWorkDiscussSize() {
        return workDiscussSize;
    }

    public void setWorkDiscussSize(int workDiscussSize) {
        this.workDiscussSize = workDiscussSize;
    }

    public int getWorkCollect() {
        return workCollect;
    }

    public void setWorkCollect(int workCollect) {
        this.workCollect = workCollect;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
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

    public List<String> getWorkImageList() {
        return workImageList;
    }

    public void setWorkImageList(List<String> workImageList) {
        this.workImageList = workImageList;
    }
}
