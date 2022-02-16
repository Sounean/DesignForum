package com.chienli.micro_class.data_model;

public class SearchItemInfo {

    /**
     * id : 1
     * courseId : 1
     * videoLink : 1.avi
     * videoText : 这是视频标题1
     */

    private int id;
    private int courseId;
    private String videoLink;
    private String videoText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoText() {
        return videoText;
    }

    public void setVideoText(String videoText) {
        this.videoText = videoText;
    }
}
