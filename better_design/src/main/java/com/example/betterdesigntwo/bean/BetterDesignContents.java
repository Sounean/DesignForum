package com.example.betterdesigntwo.bean;

import java.util.List;

public class BetterDesignContents {
    Integer ID;
    Integer userId;
    String workType;
    Integer workLabels;
    String workLabelText;
    String workText;
    String workImage;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Integer getWorkLabels() {
        return workLabels;
    }

    public void setWorkLabels(Integer workLabels) {
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

    public List<String> getWorkImageList() {
        return workImageList;
    }

    public void setWorkImageList(List<String> workImageList) {
        this.workImageList = workImageList;
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

    public String getWorkLikeSize() {
        return workLikeSize;
    }

    public void setWorkLikeSize(String workLikeSize) {
        this.workLikeSize = workLikeSize;
    }

    public Integer getWorkDiscussSize() {
        return workDiscussSize;
    }

    public void setWorkDiscussSize(Integer workDiscussSize) {
        this.workDiscussSize = workDiscussSize;
    }

    public String getWorkCollect() {
        return workCollect;
    }

    public void setWorkCollect(String workCollect) {
        this.workCollect = workCollect;
    }

    public Integer getShow() {
        return show;
    }

    public void setShow(Integer show) {
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

    List<String> workImageList;//图片集合
    String path;

    public BetterDesignContents(Integer ID, Integer userId, String workType, Integer workLabels, String workLabelText, String workText, String workImage, List<String> workImageList, String path, String workTime, String workLikeSize, Integer workDiscussSize, String workCollect, Integer show, String userName, String userHead) {
        this.ID = ID;
        this.userId = userId;
        this.workType = workType;
        this.workLabels = workLabels;
        this.workLabelText = workLabelText;
        this.workText = workText;
        this.workImage = workImage;
        this.workImageList = workImageList;
        this.path = path;
        this.workTime = workTime;
        this.workLikeSize = workLikeSize;
        this.workDiscussSize = workDiscussSize;
        this.workCollect = workCollect;
        this.show = show;
        this.userName = userName;
        this.userHead = userHead;
    }

    String workTime;
    String workLikeSize;
    Integer workDiscussSize;
    String workCollect;
    Integer show;
    String userName;
    String userHead;
}
