package com.chienli.micro_class.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chienli.micro_class.BR;
import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.MicroClassInfo;
import com.chienli.micro_class.databinding.FragmentVideoListBinding;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.fragment.base.BaseFragment;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.chienli.micro_class.view_model.VideoListFragmentViewModel;

/**
 * 获取视频列表
 * 域名/Course/getVideo?userId=5&courseId=2
 */
public class VideoListFragment extends BaseFragment {

    private static final String TAG = "VideoListFragment";

    private static final String USER_ID = "USER_ID";
    private static final String COURSE_ID = "COURSE_ID";

    private int userId;
    private int courseId;

    private FragmentVideoListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt(USER_ID);
            courseId = args.getInt(COURSE_ID);
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_list, container, false);
        VideoListFragmentViewModel viewModel = new VideoListFragmentViewModel(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class));
        binding.setVideoListVM(viewModel);
        return binding.getRoot();
    }

    public static Fragment getInstance(int userId, int courseId) {
        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, userId);
        bundle.putInt(COURSE_ID, courseId);

        Fragment fragment = new VideoListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
