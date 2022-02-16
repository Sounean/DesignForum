package com.chienli.design_forum_all_lib.bean;

public class CommentInfo {

    /**
     * id : 26
     * userId : 1
     * discussType : 3
     * parentId : 2
     * discussText : 我的用户id为1,在id为2的视频中，发布的评论
     * discussTime : 2019-01-21 19:17:20.0
     * discussLikeSize : 1
     * answer : 0
     * hasNextPage : false
     * replies : null
     * userHead : null
     * userName : null
     */

    private int id;
    private int userId;
    private String discussType;
    private int parentId;
    private String discussText;
    private String discussTime;
    private int discussLikeSize;
    private String answer;
    private boolean hasNextPage;
    private Object replies;
    private Object userHead;
    private Object userName;

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

    public String getDiscussType() {
        return discussType;
    }

    public void setDiscussType(String discussType) {
        this.discussType = discussType;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getDiscussText() {
        return discussText;
    }

    public void setDiscussText(String discussText) {
        this.discussText = discussText;
    }

    public String getDiscussTime() {
        return discussTime;
    }

    public void setDiscussTime(String discussTime) {
        this.discussTime = discussTime;
    }

    public int getDiscussLikeSize() {
        return discussLikeSize;
    }

    public void setDiscussLikeSize(int discussLikeSize) {
        this.discussLikeSize = discussLikeSize;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Object getReplies() {
        return replies;
    }

    public void setReplies(Object replies) {
        this.replies = replies;
    }

    public Object getUserHead() {
        return userHead;
    }

    public void setUserHead(Object userHead) {
        this.userHead = userHead;
    }

    public Object getUserName() {
        return userName;
    }

    public void setUserName(Object userName) {
        this.userName = userName;
    }
}
