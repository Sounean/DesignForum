package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.CommentInfo;
import com.chienli.design_forum_all_lib.bean.CourseInfo;
import com.chienli.design_forum_all_lib.bean.FollowUserInfo;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.chienli.design_forum_all_lib.bean.WorkWallInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.activity.MicroClassMainActivity;
import com.example.betterdesigntwo.activity.BetterDesignDeepActivity;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.adapter.PersonalCenterToolsRecyclerAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;
import com.inet.designforum.util.DFContextHandler;
import com.inet.designforum.workwall.activity.WorkWallContentActivity;
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

/**
 * 由于PersonalCenter的好几个模块的内容是类似或者相同的，所以直接将其整合
 * <p>
 * 分类的界限比较模糊，所以就直接不进行分类，一般使用“全部”，后续有需要在单独做
 */
public class PersonalCenterToolsActivity extends BaseActivity implements OnRefreshLoadMoreListener, View.OnClickListener, OnItemClickListener {

    private TextView mTitle;
    private LinearLayout mBack;
    private SmartRefreshLayout mRefresh;
    private RecyclerView mRecycler;

    private String title;
    private int type;

    private List infos = new ArrayList<>();

    private Handler handler = new DFContextHandler<>(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_center_tools);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void findViews() {
        mTitle = findViewById(R.id.tv_toolbar_all_title);
        mBack = findViewById(R.id.ll_toolbar_all_back);
        mRecycler = findViewById(R.id.rcl_personal_center_tools);
        mRefresh = findViewById(R.id.ref_personal_center_tools);
    }

    @Override
    public void getData() {
        Intent intent = getIntent();
        title = intent.getStringExtra(TOOL_TYPE_TITLE);
        type = intent.getIntExtra(TOOL_TYPE_TYPE, TOOL_TYPE_COLLECT);
    }

    private PersonalCenterToolsRecyclerAdapter adapter;

    @Override
    public void initViews() {
        mTitle.setText(title);
        mRefresh.setOnRefreshLoadMoreListener(this);
        adapter = new PersonalCenterToolsRecyclerAdapter(infos, type, this);
        mRecycler.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(manager);
        mBack.setOnClickListener(this);

        // 最开始没有数据所以直接刷新
        mRefresh.autoRefresh();
    }

    /**
     * SmartRefreshLayout的加载回调
     *
     * @param refreshLayout RefreshLayout本身
     */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(2000);
    }

    /**
     * SmartRefreshLayout的刷新回调
     *
     * @param refreshLayout RefreshLayout本身
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        loadNetData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_toolbar_all_back) {
            finish();
        }
    }

    @Override
    public void onClick(View v, int position) {
        switch (type) {
            case TOOL_TYPE_COURSE: {
                CourseInfo info = (CourseInfo) infos.get(position);
                MicroClassActivity.startMicroClassActivity(this, DesignForumApplication.mainBinder.getUserInfo().getId(), info.getId());
                break;
            }
            case TOOL_TYPE_WORKS: {
                WorkWallContentActivity.actionStart(this, JSON.toJSONString(infos.get(position)));
                break;
            }
            case TOOL_TYPE_COMMENT: {
                break;
            }
            case TOOL_TYPE_COLLECT: {
                WorkWallInfo info = (WorkWallInfo) infos.get(position);
                if (info.getWorkType().equals("1")){
                    WorkWallContentActivity.actionStart(this, JSON.toJSONString(infos.get(position)));
                }else if (info.getWorkType().equals("2")){
                  //  BetterDesignDeepActivity.
                }

                break;
            }
            case TOOL_TYPE_LIKE: {
                // 这个界面就没有数据
                break;
            }
            case TOOL_TYPE_FOLLOW: {
                break;
            }
            default:
                break;
        }
    }

    private static final String TAG = "PersonalCenterToolsAct";

    private void loadNetData() {
        switch (type) {
            case TOOL_TYPE_COURSE: {
                // 我的课程
                if (DesignForumApplication.mainBinder.getUserInfo().getCourse_collect().size() == 0) {
                    handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "您还未参与课程", Toast.LENGTH_SHORT).show());
                    return;
                }
                String url = DesignForumApplication.HOST + "Course/getIds;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                FormBody body = new FormBody.Builder()
                        .add("ids", DesignForumApplication.mainBinder.getUserInfo().getCourse_collect().toString().replace("[", "").replace("]", ""))
                        .build();
                Request request = new Request.Builder()
                        .post(body)
                        .url(url)
                        .build();
                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            if (code == 0) {
                                infos.clear();
                                infos.addAll(json.getJSONObject("content").getJSONArray("content").toJavaList(CourseInfo.class));
                                handler.post(() -> {
                                    adapter.notifyDataSetChanged();
                                    mRefresh.finishRefresh(500);
                                    if (infos.size() == 0) {
                                        Toast.makeText(PersonalCenterToolsActivity.this, "您还未参与课程", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                            }
                            Log.e(TAG, "onResponse: " + response);
                        }
                    }
                });
                break;
            }
            case TOOL_TYPE_WORKS: {
                // 我的作品
                String url = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                Log.e(TAG, "loadNetData: url = " + url);
                FormBody body = new FormBody.Builder()
                        .add("work_type", "1")
                        .build();
                Request request = new Request.Builder()
                        .post(body)
                        .url(url)
                        .build();
                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            Log.e(TAG, "onResponse: TOOL_TYPE_WORKS code = " + code);
                            if (code == 0) {
                                Log.e(TAG, "onResponse: json = " + json.toJSONString());
                                infos.clear();
                                infos.addAll(json.getJSONObject("content").getJSONObject("content").getJSONArray("list").toJavaList(WorkWallInfo.class));
                                handler.post(() -> {
                                    adapter.notifyDataSetChanged();
                                    mRefresh.finishRefresh(500);
                                });
                            } else {
                                handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                });
                break;
            }
            case TOOL_TYPE_COMMENT: {
                // 我的评论
                String url = DesignForumApplication.HOST + "comment/getUserAllComment;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            Log.e(TAG, "onResponse: TOOL_TYPE_COMMENT code = " + code);
                            if (code == 0) {
                                Log.e(TAG, "onResponse: json = " + json.toJSONString());
                                infos.clear();
                                infos.addAll(json.getJSONObject("content").getJSONArray("content").toJavaList(CommentInfo.class));
                                handler.post(() -> {
                                    adapter.notifyDataSetChanged();
                                    mRefresh.finishRefresh(500);
                                });
                            } else {
                                Log.e(TAG, "onResponse: TOOL_TYPE_COMMENT Response code = " + response.code());
                                handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                });
                break;
            }
            case TOOL_TYPE_COLLECT: {
                // 没有做收藏的显示
                String url = DesignForumApplication.HOST + "work/getWorkInId;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                List<Integer> idList = new ArrayList<>();
                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                if (null != info.getDesign_collect()) {
                    idList.addAll(info.getDesign_collect());
                }
                if (null != info.getProduction_collect()) {
                    idList.addAll(info.getProduction_collect());
                }
                if (idList.size() == 0) {
                    Toast.makeText(this, "您还没有收藏", Toast.LENGTH_SHORT).show();
                    return;
                }
                FormBody body = new FormBody.Builder()
                        .add("id_str", JSON.toJSONString(idList))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e(TAG, "onResponse: TOOL_TYPE_COLLECT-1");
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            Log.e(TAG, "onResponse: TOOL_TYPE_COLLECT code = " + code);
                            if (code == 0) {
                                Log.e(TAG, "onResponse: json = " + json.toJSONString());
                                infos.clear();
                                infos.addAll(json.getJSONObject("content").getJSONArray("content").toJavaList(WorkWallInfo.class));
                                handler.post(() -> {
                                    adapter.notifyDataSetChanged();
                                    mRefresh.finishRefresh(500);
                                });
                            } else {
                                handler.post(() -> Toast.makeText(PersonalCenterToolsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                            }
                        }
                        Log.e(TAG, "onResponse: " + response);
                    }
                });
                break;
            }
            case TOOL_TYPE_LIKE: {
                break;
            }
            case TOOL_TYPE_FOLLOW: {
                break;
            }
            default:
                break;
        }
    }


    /**********************************************************************************************/

    /**
     * 开启该界面具体有以下几种
     * - 我的课堂 --> 0
     * - 我的作品 --> 1
     * - 我的评论 --> 2
     * - 我的收藏 --> 3
     * - 我的点赞 --> 4
     * - 我的关注 --> 5
     *
     * @param context  上下文的对象
     * @param toolType 该工具的具体类型
     */
    public static void startActivity(Context context, int toolType) {
        Intent intent = new Intent(context, PersonalCenterToolsActivity.class);
        switch (toolType) {
            case TOOL_TYPE_COURSE: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的课程");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_COURSE);
                break;
            }
            case TOOL_TYPE_WORKS: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的作品");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_WORKS);
                break;
            }
            case TOOL_TYPE_COMMENT: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的评论");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_COMMENT);
                break;
            }
            case TOOL_TYPE_COLLECT: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的收藏");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_COLLECT);
                break;
            }
            case TOOL_TYPE_LIKE: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的点赞");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_LIKE);
                break;
            }
            case TOOL_TYPE_FOLLOW: {
                intent.putExtra(TOOL_TYPE_TITLE, "我的关注");
                intent.putExtra(TOOL_TYPE_TYPE, TOOL_TYPE_FOLLOW);
                break;
            }
            default:
                intent.putExtra(TOOL_TYPE_TITLE, "ERROR_VALUE");
                break;
        }
        context.startActivity(intent);
    }

    public static final int TOOL_TYPE_COURSE = 0;
    public static final int TOOL_TYPE_WORKS = 1;
    public static final int TOOL_TYPE_COMMENT = 2;
    public static final int TOOL_TYPE_COLLECT = 3;
    public static final int TOOL_TYPE_LIKE = 4;
    public static final int TOOL_TYPE_FOLLOW = 5;

    private static final String TOOL_TYPE_TITLE = "TITLE";
    private static final String TOOL_TYPE_TYPE = "TYPE";


}
