package com.inet.designforum.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;

import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.adapter.MainFragmentPagerAdapter;
import com.inet.designforum.fragment.MainFollowFragment;
import com.inet.designforum.fragment.MainHomeFragment;
import com.inet.designforum.fragment.MainMessageFragment;
import com.inet.designforum.fragment.MainPersonalCenterFragment;
import com.inet.designforum.fragment.MainUploadFragment;
import com.inet.designforum.view.MainHomeTabLayout;
import com.inet.designforum.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 在这里注意区分一个特点, 就是Main页面是最开始的, 可以直接见到的界面/页面
 * <p>
 * 在Main界面中, 还有Home页, Follow(关注)页, Upload页, Message页, PersonalCenter页
 *
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    public static final int MAIN_PAGER_COUNT = 5;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    private NoScrollViewPager mViewPager;
    private MainHomeTabLayout mTab;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        initPermission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void findViews() {
        mViewPager = findViewById(R.id.vp_main);
        mTab = findViewById(R.id.tab_main);
    }

    @Override
    public void getData() {
        // ViewPager中展示的内容, 这里由各个界面负责的同学来完成
        fragments = new ArrayList<>();
        fragments.add(new MainHomeFragment());
        fragments.add(new MainFollowFragment());
        fragments.add(new MainUploadFragment());
        fragments.add(new MainMessageFragment());
        fragments.add(new MainPersonalCenterFragment());
    }

    @Override
    public void initViews() {
        // 关于mTab对象的具体视图的设置方法请看MainFragmentPagerAdapter和MainHomeTabLayout中的部分方法
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), fragments);

        mViewPager.setAdapter(adapter);
        // mTab的使用应该先setupWithViewPager, 然后setTabView, 不然不能获得正确的数量 or 直接空指针
        mTab.setupWithViewPager(mViewPager);
        mTab.setTabView(adapter);

        mViewPager.setScroll(false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void toIndex(int index) {
        TabLayout.Tab tab = mTab.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }
}
