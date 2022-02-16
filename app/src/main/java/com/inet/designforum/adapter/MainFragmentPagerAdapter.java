package com.inet.designforum.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inet.designforum.R;
import com.inet.designforum.activity.MainActivity;
import com.inet.designforum.view.callback.TabCallBack;
import com.sendtion.xrichtext.GlideOptions;

import java.util.List;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter implements TabCallBack {

    private List<Fragment> fragments;

    public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return MainActivity.MAIN_PAGER_COUNT;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    /**
     * 这个方法的返回值应当为空, 不为空的话tab中会出现文字
     *
     * @param position ViewPager中的位置
     * @return 返回值应该是一个标题, 但是标题是由我们自己实现的图片+文字, 在这里不合适
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }


    /**
     * 自定义Tab中的View
     *
     * @param position tab中View的索引值啦
     * @return 自定义Tab中的View
     */
    @Override
    public View getTabView(Context context, int position) {
        View view = View.inflate(context, R.layout.layout_main_tab_item, null);
        TextView tv = view.findViewById(R.id.tv_main_tab);
        ImageView img = view.findViewById(R.id.img_main_tab);
        switch (position) {
            case 0: {
                tv.setText("首页");
                Glide.with(context).load(R.drawable.ic_home).into(img);
                return view;
            }
            case 1: {
                tv.setText("关注");
                Glide.with(context).load(R.drawable.ic_follow).into(img);
                return view;
            }
            case 2: {
//                View v = View.inflate(context, R.layout.layout_main_tab_center_item, null);
//                TextView tv1 = v.findViewById(R.id.tv_main_tab);
//                ImageView img1 = v.findViewById(R.id.img_main_tab);
//                tv1.setText("上传");
//                Glide.with(context).load(R.drawable.ic_add).into(img1);

                tv.setText("上传");
                Glide.with(context).load(R.drawable.ic_add).into(img);
                return view;
            }
            case 3: {
                tv.setText("消息");
                Glide.with(context).load(R.drawable.ic_message).into(img);
                return view;
            }
            case 4: {
                tv.setText("我的");
                Glide.with(context).load(R.drawable.ic_personal_center).into(img);
                return view;
            }
        }
        return View.inflate(context, R.layout.layout_main_tab_item, null);
    }
}
