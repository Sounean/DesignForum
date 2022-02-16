package com.inet.designforum.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.activity.LoginActivity;
import com.inet.designforum.activity.MainActivity;
import com.inet.designforum.activity.PersonalCenterToolsActivity;
import com.inet.designforum.activity.SettingActivity;
import com.inet.designforum.adapter.MainPersonalCenterToolsRecyclerAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;
import com.inet.designforum.dialog.MainPersonalCenterEditDialog;
import com.inet.designforum.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPersonalCenterFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {

    // 界面中左上角的 "编辑"
    private TextView mTvEdit;
    private ImageView mIvUserIcon;
    private TextView mTvUserName;
    private TextView mTvMyCourse;
    private TextView mTvMyWorks;
    private TextView mTvMyComment;
    private RecyclerView mRecyclerTools;

    private UserInfo userInfo;

    private static final String[] TOOL_NAMES = {"我的收藏", "我的点赞", "我的关注", "设置", "关于我们", "注销", "退出"};
    private static final int[] TOOL_ICONS = {
            R.drawable.ic_collect,
            R.drawable.ic_like,
            R.drawable.ic_follow,
            R.drawable.ic_setting,
            R.drawable.ic_about,
            R.drawable.ic_logout,
            R.drawable.ic_exit};

    private static final Tool.ToolType[] TYPE = {
            Tool.ToolType.COLLECT,
            Tool.ToolType.LIKE,
            Tool.ToolType.FOLLOW,
            Tool.ToolType.SETTING,
            Tool.ToolType.ABOUT,
            Tool.ToolType.LOGOUT,
            Tool.ToolType.EXIT};


    private List<Tool> tools;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            throw new NullPointerException("Fragment is a container, but the container is null");
        }
        View view = View.inflate(container.getContext(), R.layout.fragment_main_personal_center, null);
        init(view);
        return view;
    }

    private static final String TAG = "MainPersonalCenterFragm";

    /**
     * 该方法在fragment由不可见到可见的时候会被执行，所以从后台出来的时候或者刷新出来的时候就可以调用到
     * 在这里初始化该界面的用户头像信息和昵称
     */
    @Override
    public void onStart() {
        super.onStart();
        updateFragment();
    }

    private void updateFragment() {
        if (DesignForumApplication.mainBinder != null) {
            if (DesignForumApplication.mainBinder.isLogin()) {
                userInfo = DesignForumApplication.mainBinder.getUserInfo(); // clone的随便用
            } else {
                userInfo = null;
            }
        } else {
            userInfo = null;
        }
        Log.e(TAG, "onStart: userInfo == null ? " + (userInfo == null));
        // Glide的圆形变换
        RequestOptions requestOptions = RequestOptions.circleCropTransform().error(R.drawable.ic_error_cloud);
        if (userInfo != null) {
            mTvUserName.setText(userInfo.getUser_name());
            // "http://www.cyhfwq.top/designForum/file/head/" + userInfo.getUser_head()
            Glide.with(this)
                    .load(DesignForumApplication.HOST + "images/head/" + userInfo.getUser_head())
                    .apply(requestOptions)
                    .into(mIvUserIcon);
        } else {
            // 无用户登录
            mTvUserName.setText("用户未登录");
            Glide.with(this).load(R.drawable.ic_user_default).apply(requestOptions).into(mIvUserIcon);
        }
    }

    @Override
    public void findViews(View mView) {
        mTvEdit = mView.findViewById(R.id.tv_main_personal_center_edit);
        mIvUserIcon = mView.findViewById(R.id.iv_main_personal_center_user_icon);
        mTvUserName = mView.findViewById(R.id.iv_main_personal_center_user_name);
        mTvMyCourse = mView.findViewById(R.id.tv_main_personal_center_my_course);
        mTvMyWorks = mView.findViewById(R.id.tv_main_personal_center_my_works);
        mTvMyComment = mView.findViewById(R.id.tv_main_personal_center_my_comment);
        mRecyclerTools = mView.findViewById(R.id.recycler_main_personal_center_tools);
    }

    @Override
    public void getData(View mView) {

        tools = new ArrayList<>();
        for (int i = 0; i < TOOL_ICONS.length; i++) {
            tools.add(new Tool(TOOL_NAMES[i], TOOL_ICONS[i], TYPE[i]));
        }
    }

    @Override
    public void initViews(View mView) {
        // onClick
        mTvEdit.setOnClickListener(this);
        mTvMyCourse.setOnClickListener(this);
        mTvMyWorks.setOnClickListener(this);
        mTvMyComment.setOnClickListener(this);
        mIvUserIcon.setOnClickListener(this);
        mTvUserName.setOnClickListener(this);

        // recycler
        MainPersonalCenterToolsRecyclerAdapter adapter = new MainPersonalCenterToolsRecyclerAdapter(getContext(), tools, this);
        // 这里将recyclerView子项目的点击事件<del>放到Adapter中去了</del>又拿回来了！
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerTools.setAdapter(adapter);
        mRecyclerTools.setLayoutManager(manager);

        // "编辑"按钮的点击事件
        mTvEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_main_personal_center_edit) {
            if (userInfo != null) {
                MainPersonalCenterEditDialog.getInstance().show(getChildFragmentManager(), "MainPersonalCenterEditDialog");
            } else {
                Toast.makeText(getContext(), "您还未登陆", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tv_main_personal_center_my_course) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            PersonalCenterToolsActivity.startActivity(getContext(), PersonalCenterToolsActivity.TOOL_TYPE_COURSE);
        } else if (id == R.id.tv_main_personal_center_my_works) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            PersonalCenterToolsActivity.startActivity(getContext(), PersonalCenterToolsActivity.TOOL_TYPE_WORKS);
        } else if (id == R.id.tv_main_personal_center_my_comment) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            PersonalCenterToolsActivity.startActivity(getContext(), PersonalCenterToolsActivity.TOOL_TYPE_COMMENT);
        } else if (id == R.id.iv_main_personal_center_user_icon || id == R.id.iv_main_personal_center_user_name) {
            if (userInfo == null) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            } else {
                MainPersonalCenterEditDialog.getInstance().show(getChildFragmentManager(), "MainPersonalCenterEditDialog");
            }
        }
    }

//    /**
//     * RecyclerView中注册的点击回调
//     *
//     * @param v        itemView
//     * @param position itemView所在的实时位置
//     */
//    @Override
//    public void onItemClick(View v, int position) {
//        Log.e("RecyclerViewItem", "OnClick-" + position);
//        Toast.makeText(getContext(), "RecyclerViewItem" + "OnClick-" + position, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(getClass().getSimpleName(), data.toURI());
        if (requestCode == 222) {
            Uri imageUri = data.getData();
        }
    }

    @Override
    public void onClick(View v, int position) {
        MainPersonalCenterFragment.Tool tool = tools.get(position);
        MainPersonalCenterFragment.Tool.ToolType toolType = tool.getType();

        if (toolType == MainPersonalCenterFragment.Tool.ToolType.COLLECT) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            PersonalCenterToolsActivity.startActivity(getActivity(), PersonalCenterToolsActivity.TOOL_TYPE_COLLECT);
        } else if (toolType == MainPersonalCenterFragment.Tool.ToolType.LIKE) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            PersonalCenterToolsActivity.startActivity(getActivity(), PersonalCenterToolsActivity.TOOL_TYPE_LIKE);
        } else if (toolType == MainPersonalCenterFragment.Tool.ToolType.FOLLOW) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            MainActivity activity = (MainActivity) getActivity();
            activity.toIndex(1);
//            PersonalCenterToolsActivity.startActivity(getActivity(), PersonalCenterToolsActivity.TOOL_TYPE_FOLLOW);
        } else if (toolType == MainPersonalCenterFragment.Tool.ToolType.SETTING) {
            // 开启设置界面
            SettingActivity.startActivity(getActivity(), SettingFragment.MAIN_SETTING);
        } else if (toolType == MainPersonalCenterFragment.Tool.ToolType.ABOUT) {
            // 开启相关的介绍
            Toast.makeText(getActivity(),"即将上线",Toast.LENGTH_SHORT).show();
        } else if (toolType == MainPersonalCenterFragment.Tool.ToolType.EXIT) {
            // 弹出对话框，然后确认退出
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage("您确定退出吗？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentActivity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
        } else if (toolType == Tool.ToolType.LOGOUT) {
            // 注销
            DesignForumApplication.mainBinder.logout();
            updateFragment();
        }

    }

    public static class Tool {
        private String name;
        private int icon;
        private ToolType type;

        public enum ToolType {
            COLLECT, LIKE, FOLLOW, SETTING, ABOUT, LOGOUT, EXIT
        }

        public Tool() {
        }

        public Tool(String name, int icon, ToolType type) {
            this.name = name;
            this.icon = icon;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public ToolType getType() {
            return type;
        }

        public void setType(ToolType type) {
            this.type = type;
        }
    }
}
