package com.inet.designforum.view.callback;

import android.content.Context;
import android.view.View;

/**
 * 该接口应该是被PagerAdapter实现
 * 在自定义的TabLayout{@link com.inet.designforum.view.MainHomeTabLayout}中使用
 */
public interface TabCallBack{
    View getTabView(Context context, int position);
}
