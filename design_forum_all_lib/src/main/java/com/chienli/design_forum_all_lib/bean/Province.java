package com.chienli.design_forum_all_lib.bean;

import java.util.List;

public class Province {
    private String province; // 省级
    private List<String> cities; // 市级

    public Province() {
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "Province{" +
                "province='" + province + '\'' +
                ", cities=" + cities +
                '}';
    }
}
