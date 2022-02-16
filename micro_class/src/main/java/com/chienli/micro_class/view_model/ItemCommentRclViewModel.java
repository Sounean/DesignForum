package com.chienli.micro_class.view_model;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.chienli.micro_class.data_model.CommentInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.fragment.CommentFragment;
import com.chienli.micro_class.ui.fragment.RepliesFragment;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.chienli.micro_class.util.FragmentManagerUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemCommentRclViewModel {
    private static final String TAG = "ItemCommentRclViewModel";

    public static void onItemClick(View view, int position) {
    }

    @BindingAdapter({"setRepliesData"})
    public static void repliesLayout(FrameLayout layout, List<CommentInfo.CommentBean.RepliesBean> repliesBeans) {
//        if (repliesBeans == null || repliesBeans.size() == 0) {
//            return;
//        }
//        Fragment parent = FragmentManagerUtil.findFragmentByClass(CommentFragment.class);
//        FragmentManager childFragmentManager = parent.getChildFragmentManager();
//        layout.setTag(String.valueOf(Math.random()*99+1));
//        FragmentTransaction transaction = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getSupportFragmentManager().beginTransaction();
//        transaction.replace(layout.getId(), RepliesFragment.getInstance(new ArrayList<>(repliesBeans)),String.valueOf(Math.random()*99+1));
//        transaction.commit();
//        Log.e(TAG, "repliesLayout: " + layout.getParent().getParent().toString() );
    }
}
