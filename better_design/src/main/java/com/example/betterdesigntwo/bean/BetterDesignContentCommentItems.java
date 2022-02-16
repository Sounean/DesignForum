package com.example.betterdesigntwo.bean;

public class BetterDesignContentCommentItems {
    int userhead;//头像
    String username;//用户名
    int likenum;//点赞数

    public BetterDesignContentCommentItems() {
    }

    String commentwords;//评语

    public BetterDesignContentCommentItems(int userhead, String username, int likenum, String commentwords) {
        this.userhead = userhead;
        this.username = username;
        this.likenum = likenum;
        this.commentwords = commentwords;
    }

    public int getUserhead() {
        return userhead;
    }

    public void setUserhead(int userhead) {
        this.userhead = userhead;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public String getCommentwords() {
        return commentwords;
    }

    public void setCommentwords(String commentwords) {
        this.commentwords = commentwords;
    }
}
