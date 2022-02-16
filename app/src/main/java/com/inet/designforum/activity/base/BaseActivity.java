package com.inet.designforum.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.inet.designforum.activity.ChangeUserIconActivity;

/**
 * Activity的基础类，规定了视图初始化的方法与流程
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 视图初始化的流程在这里调用
     * @param savedInstanceState 保存信息用的包
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化的相关界面, 分为寻找View的ID, 获取相关数据, 初始化视图
     */
    public void init() {
        findViews();
        getData();
        initViews();
    }

    /**
     * 在这个方法中调用findViewById来找到控件
     */
    public abstract void findViews();

    /**
     * 该方法的作用目标一般是成员变量,成员变量可以直接在匿名内部类中进行修改, 局部变量则是需要final修饰符
     * 在子线程中获取数据, 组成合适的数据, 最终赋值给xxxList等等
     */
    public abstract void getData();

    /**
     * 初始化视图, 根据{@see getData()}获取的方法来修改View, 这里获取和修改的动作是初始化的时候使用的, 在对视
     * 图进行更新的时候应该在相关的适配器中完成动作
     */
    public abstract void initViews();

    /**
     * 在Fragment中开启别的界面并获取数据, 在Activity中接收
     *
     * @param resultCode 返回码
     * @param data 返回的数据，可能为Null
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMG) {
            if (data != null) {
                Uri imageUri = data.getData();
                ChangeUserIconActivity.startActivity(this, imageUri);
            }
        }
    }

    // 规定的请求图片的方法
    public static final int GET_IMG = 111;

}
