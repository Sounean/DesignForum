package com.inet.designforum.view;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.AttributeSet;
import android.view.View;

import com.inet.designforum.R;
import com.inet.designforum.adapter.MainFragmentPagerAdapter;
import com.inet.designforum.view.callback.TabCallBack;

import java.util.Objects;

public class MainHomeTabLayout extends TabLayout {

    public MainHomeTabLayout(Context context) {
        super(context);
    }

    public MainHomeTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainHomeTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 再调用该方法之前, 应当确定对应的ViewPager已经setAdapter(),
     * 并且该ViewPager的PagerAdapter已经实现了{@link TabCallBack}
     * 接口, 最后将该Adapter对象最为TabCallBack对象传入
     *
     * @param callBack 该TabLayout对应ViewPager的PagerAdapter对象, 该PagerAdpter已经实现了TabCallBack接口
     */
    public void setTabView(TabCallBack callBack){
        for (int i = 0; i<((FragmentPagerAdapter)callBack).getCount(); i++){
            TabLayout.Tab tab = getTabAt(i);

            // Objects.requireNonNull()方法将有可能抛出空指针异常
            //Objects.requireNonNull(tab).setCustomView(callBack.getTabView(getContext(),i));
            if (tab == null) {
                throw new NullPointerException("在你调用该方法以前, 请检查对应ViewPager是否在此之前绑定了PagerAdapter, 或者传到此处的Adapter对象是否和对应的ViewPager是一个对象");
            }
            tab.setCustomView(callBack.getTabView(getContext(),i));
        }
    }


}