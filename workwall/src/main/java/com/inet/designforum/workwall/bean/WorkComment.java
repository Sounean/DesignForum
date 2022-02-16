package com.inet.designforum.workwall.bean;

public class WorkComment {

    /**
     * id : 33
     * userId : 18
     * discussType : 1
     * parentId : 40
     * discussText : 你好
     * discussTime : 2019-04-15 00:29:53.0
     * discussLikeSize : 0
     * answer : 0
     * hasNextPage : false
     * replies : “”
     * userHead : “”
     * userName : “”
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
    private String replies;
    private String userHead;
    private String userName;


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

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
