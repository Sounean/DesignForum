package com.inet.designforum.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.FollowUserInfo;
import com.inet.designforum.R;
import com.inet.designforum.adapter.recycler_click_interface.DFRecyclerViewClickableAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainFollowRecyclerAdapter extends DFRecyclerViewClickableAdapter<MainFollowRecyclerAdapter.Holder> {
    private List<FollowUserInfo> userInfos;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;


    public MainFollowRecyclerAdapter(Context context, List<FollowUserInfo> userInfos, OnItemClickListener listener) {
        super(listener);
        this.userInfos = userInfos;
        this.context = context;
    }

    @NonNull
    @Override
    public MainFollowRecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_main_follow_rcl_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainFollowRecyclerAdapter.Holder holder, int i) {
        FollowUserInfo info = new FollowUserInfo();
        info.setUserStatus("-1");
        // 设置一个默认的状态
        try {
            info = userInfos.get(i);
        } catch (IndexOutOfBoundsException e) {
            // 老是爆数组上限，
            if (userInfos.size() == 0 && i == 0) {
                // 等待刷新完成就好
                holder.itemView.setVisibility(View.VISIBLE);
                // 不添加数据了，直接只显示第一组数据
                holder.followBtn.setVisibility(View.GONE);
                holder.userName.setVisibility(View.GONE);
                holder.userIntroduce.setText("您还没有关注其他用户");
                holder.userIntroduce.setGravity(Gravity.CENTER);
                holder.userIcon.setVisibility(View.GONE);
                return;
            } else if (userInfos.size() <= i) {
                // 什么也不显示
                holder.itemView.setVisibility(View.GONE);
                return;
            }
        }


        holder.itemView.setVisibility(View.VISIBLE);
        if (info.getUserStatus().equals("-1")) {
            holder.followBtn.setVisibility(View.GONE);
            holder.userName.setVisibility(View.GONE);
            holder.userIntroduce.setText("您还没有关注其他用户");
            holder.userIntroduce.setGravity(Gravity.CENTER);
            holder.userIcon.setVisibility(View.GONE);
            return;
        } else if (info.getUserStatus().equals("-2")) {
            holder.followBtn.setVisibility(View.GONE);
            holder.userName.setVisibility(View.GONE);
            holder.userIntroduce.setText("您还未登陆");
            holder.userIntroduce.setGravity(Gravity.CENTER);
            holder.userIcon.setVisibility(View.GONE);
            return;
        }
        holder.followBtn.setVisibility(View.VISIBLE);
        holder.userName.setVisibility(View.VISIBLE);
        holder.userIntroduce.setGravity(Gravity.START | Gravity.TOP);
        holder.userIcon.setVisibility(View.VISIBLE);

        holder.userName.setText(info.getUserName());
        RequestOptions options = RequestOptions.centerInsideTransform().error(R.drawable.ic_error_cloud);
        Glide.with(holder.userIcon).load(DesignForumApplication.HOST + "images/head/" + info.getUserHead()).apply(options).into(holder.userIcon);
        String sb = (info.getUserStatus().equals("1") ? "学生" : "老师") +
                "/" +
                info.getUserCareer() +
                "\n" +
                info.getUserCity();
        holder.userIntroduce.setText(sb);
        holder.followBtn.setText(info.isFollow() ?
                "已关注" :
                "未关注"
        );
    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    class Holder extends DFRecyclerViewClickableAdapter.DFViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView userIntroduce;
        Button followBtn;
        private OnItemClickListener btnListener = (v, position) -> {
            // 在这关注、取消关注
            if (!userInfos.get(position).isFollow()) {
                // 关注
                String url = DesignForumApplication.HOST + "InformationController/setAttention;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                FormBody body = new FormBody.Builder()
                        .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                        .add("targetId", String.valueOf(userInfos.get(position).getId()))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e(TAG, "onResponse");
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            Log.e(TAG, "onResponse: code = " + code);
                            if (code == 0) {
                                userInfos.get(position).setFollow(true);
                                handler.post(() -> {
                                    Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
                                    MainFollowRecyclerAdapter.this.notifyDataSetChanged();
                                });
                            } else {
                                handler.post(() -> Toast.makeText(context, "关注失败", Toast.LENGTH_SHORT).show());
                            }
                        }
                        Log.e(TAG, "onResponse: Response = " + response);
                    }
                });
            } else {
                // 取消关注
                String url = DesignForumApplication.HOST + "InformationController/delAttention;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                FormBody body = new FormBody.Builder()
                        .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                        .add("targetId", String.valueOf(userInfos.get(position).getId()))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e(TAG, "onResponse");
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            Log.e(TAG, "onResponse: code = " + code);
                            if (code == 0) {
                                userInfos.get(position).setFollow(false);
                                handler.post(() -> {
                                    Toast.makeText(context, "取消关注成功", Toast.LENGTH_SHORT).show();
                                    MainFollowRecyclerAdapter.this.notifyDataSetChanged();
                                });
                            } else {
                                handler.post(() -> Toast.makeText(context, "取消关注失败", Toast.LENGTH_SHORT).show());
                            }
                        }
                        Log.e(TAG, "onResponse: Response = " + response);
                    }
                });
            }
        };

        private static final String TAG = "Holder";

        Holder(@NonNull View itemView) {
            super(itemView);
            userIcon = itemView.findViewById(R.id.follow_user_icon);
            userName = itemView.findViewById(R.id.follow_user_name);
            userIntroduce = itemView.findViewById(R.id.follow_user_introduce);
            followBtn = itemView.findViewById(R.id.follow_user_btn);

            followBtn.setOnClickListener(v -> btnListener.onClick(v, getLayoutPosition()));
        }
    }
}
