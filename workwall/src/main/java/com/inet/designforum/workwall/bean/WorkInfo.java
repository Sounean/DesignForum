package com.inet.designforum.workwall.bean;

import java.util.List;

public class WorkInfo {

    /**
     * id : 40
     * userId : 22
     * workType : 1
     * workLabels : 7
     * workLabelText : 标志/UI设计
     * workText : hzpt校标方案汇报。
     * workImage : 1.jpg&2.jpg&3.jpg&4.jpg&5.jpg
     * workImageList : ["1.jpg","2.jpg","3.jpg","4.jpg","5.jpg"]
     * path : 22_1_1555050547349
     * workTime : 2019-04-12 14:29:07.0
     * workLikeSize : 0
     * workDiscussSize : 4
     * workCollect : 0
     * show : 1
     * userName : 5号教师
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
