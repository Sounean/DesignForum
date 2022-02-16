package com.chienli.micro_class.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.chienli.micro_class.util.FragmentManagerUtil;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManagerUtil.add(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManagerUtil.remove(this);
    }
}
