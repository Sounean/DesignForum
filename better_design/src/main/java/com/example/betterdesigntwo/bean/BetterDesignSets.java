package com.example.betterdesigntwo.bean;

public class BetterDesignSets {
    String pic;
    String text;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BetterDesignSets(String pic, String text) {
        this.pic = pic;
        this.text = text;
    }
}
