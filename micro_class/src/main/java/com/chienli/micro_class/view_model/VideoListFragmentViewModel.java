package com.chienli.micro_class.view_model;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chienli.micro_class.data_model.MicroClassInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.adapter.VideoListRecyclerViewAdapter;
import com.chienli.micro_class.util.ActivityManagerUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VideoListFragmentViewModel {
    private static final String TAG = "VideoListFragmentViewMo";

    public final Context context;
    public final ObservableField<MicroClassInfo> info;// 这个里面包含了videoBean的信息，应该从Activity的ViewModel中直接获取

    public final MicroClassActivityViewModel parentViewModel = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM(); // 父容器的信息

    public VideoListFragmentViewModel(Context context) {
        this.context = context;
        this.info = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info;
    }

    @BindingAdapter({"vlRclAdapterData"})
    public static void vlRclAdapter(RecyclerView rcl, ObservableField<MicroClassInfo> info) {
        LinearLayoutManager manager = new LinearLayoutManager(rcl.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rcl.setLayoutManager(manager);
        rcl.setAdapter(new VideoListRecyclerViewAdapter(rcl.getContext(), info));
    }

}
