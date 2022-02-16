package com.chienli.design_forum_all_lib.bean;

public class CourseInfo {

    /**
     * id : 1
     * userId : 5
     * courseName : 字体设计
     * courseText : 本课堂为板式理论课程,非常系统且详尽的分析了设计中各个抽象元素具体化,培养审美等方法,分析不同设计作品中主体与构图形式,对齐与符号化文字运用等等,是一套非常完美,培养设计师设计理念的优秀课程
     * likeSize : 0
     * collectSize : 0
     * study_video : null
     * courseImage : 1.jpg
     * path : 1
     * show : 1
     * video : null
     */

    private int id;
    private int userId;
    private String courseName;
    private String courseText;
    private int likeSize;
    private int collectSize;
    private Object study_video;
    private String courseImage;
    private String path;
    private String show;
    private String video;

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

    public Object getStudy_video() {
        return study_video;
    }

    public void setStudy_video(Object study_video) {
        this.study_video = study_video;
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

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
