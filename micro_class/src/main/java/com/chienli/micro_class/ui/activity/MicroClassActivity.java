package com.chienli.micro_class.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.chienli.micro_class.R;
import com.chienli.micro_class.databinding.ActivityMicroClassBinding;
import com.chienli.micro_class.ui.activity.base.BaseActivity;
import com.chienli.micro_class.view_model.MicroClassActivityViewModel;

import cn.jzvd.Jzvd;

/**
 * 这个是单一的一个Activity, 传入UserId, CourseId
 * 域名/Course/getVideo?userId=5&courseId=2
 * <p>
 * 获取视频列表
 * 域名/Course/getVideo?userId=5&courseId=2
 * <p>
 * 获取评论列表
 * 请求接口地址：域名/comment/getComment
 * 发送:
 * discusstype:3 3代表要找的是课堂视频的评论（必填）
 * id: 点击的视频的id,是视频,不是课堂id（必填）
 * pagenum: 页码(默认为1) 比如第一次加载,page就为1,表示找第一页的10条评论,然后安卓端先存起来.上拉加载的时候,+1,变成2,把2传递给后端,后端就去找第11-20条的记录,前面10条就不再次发了
 * <p>
 * 发送评论
 * 请求接口地址：域名/comment/insComment
 * 请求方式必须是post请求
 * userId：用户id
 * discussType：3（必填）
 * parentId：作品id
 * discussText：评论内容 长度不能超过50
 * <p>
 * 请求接口地址：域名/comment/insReply
 * 请求方式必须是post请求
 * 发送:
 * parentId: 教师要回复的评论的id（不能为空）
 * sendName: 发送者昵称 即 这个老师的昵称（不能为空）
 * receiveName:接收者昵称,即那条评论发送者昵称（不能为空）
 * replyText:回复内容（不能超过30个字符）
 */
public class MicroClassActivity extends BaseActivity {

    private static final String USER_ID = "USER_ID";
    private static final String CLASS_ID = "CLASS_ID";
    private ActivityMicroClassBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_micro_class);

        int userId = getIntent().getIntExtra(USER_ID, 1);
        int classId = getIntent().getIntExtra(CLASS_ID, 1);

        MicroClassActivityViewModel viewModel = new MicroClassActivityViewModel(this,userId, classId);

        binding.setMicroClassVM(viewModel);
    }


    public ActivityMicroClassBinding getBinding() {
        return binding;
    }

    public static void startMicroClassActivity(Context context, int userId, int classId) {
        Intent intent = new Intent(context, MicroClassActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(CLASS_ID, classId);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
