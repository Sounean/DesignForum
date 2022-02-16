package com.chienli.micro_class.data_model;

import java.io.Serializable;
import java.util.List;

/**
 * 具体某个MicroClass的基础信息
 */
public class MicroClassInfo implements Serializable {

    /**
     * id : 1
     * userId : null
     * courseName : null
     * courseText : 本课堂为板式理论课程,非常系统且详尽的分析了设计中各个抽象元素具体化,培养审美等方法,分析不同设计作品中主体与构图形式,对齐与符号化文字运用等等,是一套非常完美,培养设计师设计理念的优秀课程
     * likeSize : 0
     * collectSize : 0
     * study_video : []
     * courseImage : null
     * path : null
     * show : null
     * video : [{"id":1,"courseId":1,"videoLink":"1.avi","videoText":"这是视频标题1"},{"id":2,"courseId":1,"videoLink":"2.avi","videoText":"这是视频标题2"}]
     */

    private int id;
    private int userId;
    private String courseName;
    private String courseText;
    private int likeSize;
    private int collectSize;
    private String courseImage;
    private String path;
    private boolean show;
    private List<Integer> study_video; // 在该课堂中学习过的视频ID
    private List<VideoBean> video;

    public MicroClassInfo() {
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseText() {
        return courseText;
    }

    public void setCourseText(String courseText) {
        this.courseText = courseText;
    }

    public int getLikeSize() {
        return likeSize;
    }

    public void setLikeSize(int likeSize) {
        this.likeSize = likeSize;
    }

    public int getCollectSize() {
        return collectSize;
    }

    public void setCollectSize(int collectSize) {
        this.collectSize = collectSize;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public List<Integer> getStudy_video() {
        return study_video;
    }

    public void setStudy_video(List<Integer> study_video) {
        this.study_video = study_video;
    }

    public List<VideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoBean> video) {
        this.video = video;
    }

    public static class VideoBean {
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
}
