package com.chienli.design_forum_all_lib.bean;

public class SettingConfig {
    // MAIN_SETTING
    private boolean messageNotification = true; // 消息通知， 默认为true
    private boolean mobileNetworkLoadsImage = true; // 移动网络加载数据， 默认为true， 下同

    public SettingConfig() {
    }

    public boolean isMessageNotification() {
        return messageNotification;
    }

    public void setMessageNotification(boolean messageNotification) {
        this.messageNotification = messageNotification;
    }

    public boolean isMobileNetworkLoadsImage() {
        return mobileNetworkLoadsImage;
    }

    public void setMobileNetworkLoadsImage(boolean mobileNetworkLoadsImage) {
        this.mobileNetworkLoadsImage = mobileNetworkLoadsImage;
    }
}
