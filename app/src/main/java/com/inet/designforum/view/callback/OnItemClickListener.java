package com.inet.designforum.view.callback;

import android.view.View;

/**
 * 说实话，RecyclerView的点击事件没有比较优雅的实现方式，我在这里使用一个约定俗成的方式
 * 该适配器应该使用在Adapter中，在onBindViewHolder这个方法中设置点击事件(OnClickListener)
 * 在点击事件中直接使用该接口对象的onItemClick方法
 */
public interface OnItemClickListener {
    void onItemClick(View view, int position);
}
