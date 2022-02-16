package com.chienli.micro_class.view_model;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chienli.micro_class.BR;
import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.CommentInfo;
import com.chienli.micro_class.ui.adapter.base.BaseBindingRecyclerViewAdapter;
import com.chienli.micro_class.ui.fragment.CommentFragment;
import com.chienli.micro_class.util.FragmentManagerUtil;

import java.util.List;

public class RepliesFragmentViewModel {
    public final ObservableArrayList<CommentInfo.CommentBean.RepliesBean> repliesBeans = new ObservableArrayList<>();
    public final ObservableField<CommentInfo> info = FragmentManagerUtil.findFragmentByClass(CommentFragment.class).getBinding().getCommentVM().info;

    public RepliesFragmentViewModel(List<CommentInfo.CommentBean.RepliesBean> repliesBeans) {
        this.repliesBeans.addAll(repliesBeans);
    }

    @BindingAdapter({"repliesRclData"})
    public static void repliesRclAdapter(RecyclerView rcl, ObservableArrayList<CommentInfo.CommentBean.RepliesBean> repliesBeans) {
        LinearLayoutManager manager = new LinearLayoutManager(rcl.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rcl.setLayoutManager(manager);
        rcl.setAdapter(new BaseBindingRecyclerViewAdapter<>(R.layout.item_replies_rcl, repliesBeans, BR.data, BR.position));
    }

}
