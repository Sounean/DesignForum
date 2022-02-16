package com.inet.designforum.fragment.base;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {

    private View mView;
    /**
     * 初始化的相关界面, 分为寻找View的ID, 获取相关数据, 初始化视图
     *
     * 在Fragment中, 该初始化方法由程序员自己调用, 并不强制, 下面的三个寻找的方法也是在该方法调用以后才可以继续调用
     *
     * @param mView 这个就是我们自己实例化的View, 会在findViews(),getData(),initViews()三个方法中作为参数变量传递
     */
    public void init(View mView){
        this.mView = mView;
        findViews(mView);
        getData(mView);
        initViews(mView);
    }

    /**
     * 根据mView来findViewById, 把值赋给成员变量吧
     *
     * @param view 当前Fragment实例的根布局
     */
    public abstract void findViews(View view);

    /**
     * 该方法的作用目标一般是成员变量,成员变量可以直接在匿名内部类中进行修改, 局部变量则是需要final修饰符
     * 在子线程中获取数据, 组成合适的数据, 最终赋值给xxxList等等
     */
    public abstract void getData(View view);

    /**
     * 初始化视图, 根据{@see getData()}获取的方法来修改View, 这里获取和修改的动作是初始化的时候使用的, 在对视
     * 图进行更新的时候应该在相关的适配器中完成动作
     */
    public abstract void initViews(View view);
}
