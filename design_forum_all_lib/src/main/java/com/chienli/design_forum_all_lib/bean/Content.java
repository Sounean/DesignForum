package com.chienli.design_forum_all_lib.bean;

import java.io.Serializable;

public class Content implements Serializable {
    public enum ContentType{
        TEXT,IMG
    }
    private String content;
    private ContentType type;

    public Content() {
    }

    public Content(String content, ContentType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }
}
