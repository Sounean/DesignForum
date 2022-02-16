package com.chienli.design_forum_all_lib.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsGroup implements Serializable {

    private NewsType type;
    private List<News> news;

    public enum NewsType {
        // 比赛, 资讯, 热点(热门话题)
        COMPETITION("艺术比赛"), INFORMATION("艺术资讯"), HOT_TOPIC("热点新闻");

        private String name;

        NewsType(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }

    }

    public NewsGroup() {
        news = new ArrayList<>();
    }

    public NewsGroup(NewsType type, List<News> news) {
        this.type = type;
        this.news = news;
    }

    public NewsType getType() {
        return type;
    }

    public void setType(NewsType type) {
        this.type = type;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
