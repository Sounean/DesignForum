package com.chienli.micro_class.ui.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.chienli.micro_class.util.ActivityManagerUtil;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerUtil.remove(this);
    }
}
