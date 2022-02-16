package com.chienli.micro_class.view_model;

import android.view.View;

import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.util.ActivityManagerUtil;

public class ItemVideoListRclViewModel {

    public final MicroClassActivityViewModel parentViewModel = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM(); // 父父容器的信息

    // 点击了某个视频，发送学习数据，并更新
    public static void onItemClick(View v, int position, final MicroClassActivityViewModel parentViewModel) {
        // 在这里修改 父父容器的信息
        parentViewModel.focusVideoPosition.set(position);
    }
}
