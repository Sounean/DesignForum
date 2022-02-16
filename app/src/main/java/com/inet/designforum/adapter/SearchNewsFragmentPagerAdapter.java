package com.inet.designforum.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chienli.design_forum_all_lib.bean.NewsGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.inet.designforum.fragment.SearchNewsItemFragment.NEWS_TYPE_KEY;

public class SearchNewsFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public SearchNewsFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = new ArrayList<>(fragments);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        NewsGroup.NewsType type = null;
        Bundle bundle = fragments.get(position).getArguments();
        if (bundle == null){
            type = NewsGroup.NewsType.COMPETITION;
            return type.getName();
        }else {
            type = (NewsGroup.NewsType) bundle.getSerializable(NEWS_TYPE_KEY);
            // 这里的type不会是空的啦
            return Objects.requireNonNull(type).getName();
        }
//        return ((SearchNewsItemFragment)fragments.get(position))
    }
}
