package com.chienli.micro_class.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.CommentInfo;
import com.chienli.micro_class.databinding.FragmentRepliesBinding;
import com.chienli.micro_class.ui.fragment.base.BaseFragment;
import com.chienli.micro_class.view_model.RepliesFragmentViewModel;

import java.util.ArrayList;

public class RepliesFragment extends BaseFragment {
    private static final String REPLIES_BEANS = "REPLIES_BEANS";

    private ArrayList<CommentInfo.CommentBean.RepliesBean> repliesBeans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            repliesBeans = (ArrayList<CommentInfo.CommentBean.RepliesBean>) args.getSerializable(REPLIES_BEANS);
        }
        FragmentRepliesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_replies, container, false);
        RepliesFragmentViewModel viewModel = new RepliesFragmentViewModel(repliesBeans);
        binding.setRepliesData(viewModel);
        return binding.getRoot();
    }

    public static Fragment getInstance(ArrayList<CommentInfo.CommentBean.RepliesBean> repliesBeans) {
        Bundle args = new Bundle();
        args.putSerializable(REPLIES_BEANS, repliesBeans);
        Fragment fragment = new RepliesFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
