package com.chienli.design_forum_all_lib.bean;

/**
 * 抽取出相关界面的基础信息，
 * - 我的课堂
 * - 我的作品
 * - 我的评论
 * - 我的收藏
 * - 我的点赞
 * - 我的关注
 */
public class ToolInfo {
    private String title;
    private String subTitle;
    private String imgUrl;
    private int toActivity;// 用于判断开启具体的某个界面

    public static final int SMALL_COURSE = 0;
    public static final int WORKS_WALL = 1;
    public static final int BATTER_DESIGN = 2;

    public ToolInfo() {
    }

    public ToolInfo(String title, String subTitle, String imgUrl, int toActivity) {
        this.title = title;
        this.subTitle = subTitle;
        this.imgUrl = imgUrl;
        this.toActivity = toActivity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getToActivity() {
        return toActivity;
    }

    public void setToActivity(int toActivity) {
        this.toActivity = toActivity;
    }
}
