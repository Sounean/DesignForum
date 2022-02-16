package com.chienli.micro_class.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chienli.micro_class.R;
import com.chienli.micro_class.databinding.FragmentCommentBinding;
import com.chienli.micro_class.ui.fragment.base.BaseFragment;
import com.chienli.micro_class.view_model.CommentFragmentViewModel;

/**
 * 获取评论列表
 * 请求接口地址：域名/comment/getComment
 * 发送:
 * discusstype:3 3代表要找的是课堂视频的评论（必填）
 * id: 点击的视频的id,是视频,不是课堂id（必填
 * pagenum: 页码(默认为1) 比如第一次加载,page就为1,表示找第一页的10条评论,然后安卓端先存起来.上拉加载的时候,+1,变成2,把2传递给后端,后端就去找第11-20条的记录,前面10条就不再次发了
 */
public class CommentFragment extends BaseFragment {

    private static final String TAG = "CommentFragment";
    private static final String VIDEO_ID = "VIDEO_ID";

    private FragmentCommentBinding binding;

    private int videoId; // 事实上依赖于某个Activity的Fragment都不用传入对象

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            videoId = args.getInt(VIDEO_ID);
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        CommentFragmentViewModel viewModel = new CommentFragmentViewModel();
        binding.setCommentVM(viewModel);
        return binding.getRoot();
    }

    public FragmentCommentBinding getBinding(){
        return binding;
    }

    public static Fragment getInstance(int videoId) {
        Bundle args = new Bundle();
        args.putInt(VIDEO_ID, videoId);
        Fragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
