package com.chienli.micro_class.data_model;

import android.support.annotation.NonNull;

/**
 * 简单的课堂信息，只有图片地址和课堂id
 */
public class MicroClassBaseInfo {

    /**
     * id : 1
     * path : 1 文件夹路径
     * image : 1.jpg
     * 完整的图片地址是 : 域名/images/Course/'path'/'image'
     */

    private int id;
    private String path;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return "MicroClassBaseInfo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
