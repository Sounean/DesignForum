package com.inet.designforum.workwall.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化的相关界面, 分为寻找View的ID, 获取相关数据, 初始化视图
     */
    public void init(){
        findViews();
        getData();
        initViews();
    }

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

}
