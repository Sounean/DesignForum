package com.chienli.design_forum_all_lib.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用户表的实体对象, 现在的使用范围 :
 * Main - Personal Center (我的)
 */
public class UserInfo implements Cloneable {
    private String account;// 账户，一般为手机号
    private String password;// 密码不是必须的选项，可以说就是空的

    private int id;// 用于自动登陆的ID号码，id + history_time = account + password，就是没有返回详细信息就是了
    private String user_name;// 昵称
    private String user_gender;// 性别 1=man 0=woman
    private String history_time;// 验证身份的时间戳
    private String user_status;// 用户身份
    private String user_email;
    private String user_head;// 头像
    private String user_birthday;
    private String user_city;
    private String user_career; // 专业
    private int course_size; // 参与的课堂的数量
    private List<Integer> production_like; // 作品点赞的IDs
    private List<Integer> production_collect; // 作品的收藏IDs
    private List<Integer> design_like;
    private List<Integer> design_collect;// 设计收藏
    private List<Integer> discuss_like;// 评论点赞的IDs
    private List<Integer> course_like;// 课程点赞的IDs
    private List<Integer> course_collect;// 课程点赞的IDs
    private List<Integer> attention;// 该用户关注的用户IDs

    private String JSESSIONID;


    public UserInfo() {
        production_like = new ArrayList<>();
        production_collect = new ArrayList<>();
        design_like = new ArrayList<>();
        design_collect = new ArrayList<>();
        course_like = new ArrayList<>();
        course_collect = new ArrayList<>();
        attention = new ArrayList<>();
    }


    @Override
    public UserInfo clone() throws CloneNotSupportedException {
        return (UserInfo) super.clone();
    }

    public String getJSESSIONID() {
        return JSESSIONID;
    }

    public void setJSESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getHistory_time() {
        return history_time;
    }

    public void setHistory_time(String history_time) {
        this.history_time = history_time;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_head() {
        return user_head;
    }

    public void setUser_head(String user_head) {
        this.user_head = user_head;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_career() {
        return user_career;
    }

    public void setUser_career(String user_career) {
        this.user_career = user_career;
    }

    public int getCourse_size() {
        return course_size;
    }

    public void setCourse_size(int course_size) {
        this.course_size = course_size;
    }

    public List<Integer> getProduction_like() {
        return production_like;
    }

    public void setProduction_like(List<Integer> production_like) {
        this.production_like = production_like;
    }

    public List<Integer> getProduction_collect() {
        return production_collect;
    }

    public void setProduction_collect(List<Integer> production_collect) {
        this.production_collect = production_collect;
    }

    public List<Integer> getDesign_like() {
        return design_like;
    }

    public void setDesign_like(List<Integer> designLike) {
        this.design_like = designLike;
    }

    public List<Integer> getDesign_collect() {
        return design_collect;
    }

    public void setDesign_collect(List<Integer> design_collect) {
        this.design_collect = design_collect;
    }

    public List<Integer> getDiscuss_like() {
        return discuss_like;
    }

    public void setDiscuss_like(List<Integer> discuss_like) {
        this.discuss_like = discuss_like;
    }

    public List<Integer> getCourse_like() {
        return course_like;
    }

    public void setCourse_like(List<Integer> course_like) {
        this.course_like = course_like;
    }

    public List<Integer> getCourse_collect() {
        return course_collect;
    }

    public void setCourse_collect(List<Integer> course_collect) {
        this.course_collect = course_collect;
    }

    public List<Integer> getAttention() {
        return attention;
    }

    public void setAttention(List<Integer> attention) {
        this.attention = attention;
    }
}
