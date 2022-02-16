package com.inet.designforum.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.FollowUserInfo;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.adapter.MainFollowRecyclerAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;
import com.inet.designforum.fragment.base.BaseFragment;
import com.inet.designforum.util.DFFragmentHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainFollowFragment extends BaseFragment implements OnItemClickListener, OnRefreshLoadMoreListener {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_follow, container, false);
        super.init(view);
        return view;
    }

    private RecyclerView mRecycler;
    private TextView title;
    private LinearLayout back;
    private SmartRefreshLayout mRefresh;

    private List<FollowUserInfo> userInfos = new ArrayList<>();
    private Handler handler = new DFFragmentHandler<>(this);

    private MainFollowRecyclerAdapter adapter;

    @Override
    public void findViews(View view) {
        mRecycler = view.findViewById(R.id.rcl_main_follow);
        title = view.findViewById(R.id.tv_toolbar_all_title);
        back = view.findViewById(R.id.ll_toolbar_all_back);
        mRefresh = view.findViewById(R.id.ref_main_follow);
    }

    @Override
    public void getData(View view) {

    }

    @Override
    public void initViews(View view) {
        adapter = new MainFollowRecyclerAdapter(getActivity(), userInfos, this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setAdapter(adapter);
        mRecycler.setLayoutManager(manager);

        back.setVisibility(View.GONE);
        title.setText("我的关注");
        title.setGravity(Gravity.CENTER);

        mRefresh.setOnRefreshLoadMoreListener(this);
        mRefresh.autoRefresh();
    }

    @Override
    public void onClick(View v, int position) {
        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(500);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateData();
    }

    private static final String TAG = "MainFollowFragment";

    @Override
    public void onStart() {
        super.onStart();
        updateData();// 开始更新数据
    }

    private void updateData() {
        if (DesignForumApplication.mainBinder == null) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateData();
            }).start();
            return;
        }
        String url = DesignForumApplication.HOST + "InformationController/getUserInId";
        UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
        if (null == info) {
            mRefresh.finishRefresh(500);
            FollowUserInfo i = new FollowUserInfo();
            i.setUserStatus("-2");
            userInfos.clear();
            userInfos.add(i);
            handler.post(() -> adapter.notifyDataSetChanged());
            return;
        }
        if (null == info.getAttention() || info.getAttention().size() == 0){
            mRefresh.finishRefresh(500);
            FollowUserInfo i = new FollowUserInfo();
            i.setUserStatus("-1");
            userInfos.clear();
            userInfos.add(i);
            handler.post(() -> adapter.notifyDataSetChanged());
            return;
        }
        FormBody body = new FormBody.Builder()
                .add("attention", JSON.toJSONString(info.getAttention()).replace("[", "").replace("]", ""))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        DesignForumApplication.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "onResponse");
                if (response.isSuccessful() && response.body() != null) {
                    JSONObject json = JSONObject.parseObject(response.body().string());
                    int code = json.getInteger("code");
                    if (code == 0) {
                        userInfos.clear();
                        userInfos.addAll(json.getJSONObject("content").getJSONArray("users").toJavaList(FollowUserInfo.class));
                        if (userInfos.size() == 0) {
                            FollowUserInfo i = new FollowUserInfo();
                            i.setUserStatus("-1");
                            userInfos.clear();
                            userInfos.add(i);
                        }
                        handler.post(() -> {
                            adapter.notifyDataSetChanged();
                            mRefresh.finishRefresh(500);
                        });
                    } else {
                        handler.post(() -> {
                            Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_SHORT).show();
                            mRefresh.finishRefresh();
                            userInfos.clear();
                            FollowUserInfo i = new FollowUserInfo();
                            i.setUserStatus("-1");
                            userInfos.clear();
                            userInfos.add(i);
                            adapter.notifyDataSetChanged();
                            mRefresh.finishRefresh(500);
                        });
                    }
                }else {
                    handler.post(() -> {
                        Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_SHORT).show();
                        mRefresh.finishRefresh();
                        userInfos.clear();
                        FollowUserInfo i = new FollowUserInfo();
                        i.setUserStatus("-1");
                        userInfos.clear();
                        userInfos.add(i);
                        adapter.notifyDataSetChanged();
                        mRefresh.finishRefresh(500);
                    });
                }
            }
        });

    }
}
