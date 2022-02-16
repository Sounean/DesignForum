package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.fragment.SettingFragment;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("TYPE");
    }

    private FrameLayout mSettingRoot;
    private String type;

    @Override
    public void findViews() {
        mSettingRoot = findViewById(R.id.setting_fragment_root);
    }

    @Override
    public void getData() {

    }

    @Override
    public void initViews() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启主设置的界面
        Fragment fragment = SettingFragment.getInstance(type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.setting_fragment_root, fragment);
        transaction.commit();
    }

    public static void startActivity(Context context,String type) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra("TYPE",type);
        context.startActivity(intent);
    }
}
