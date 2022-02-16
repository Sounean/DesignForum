package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.News;
import com.chienli.design_forum_all_lib.bean.NewsGroup;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.adapter.SearchNewsFragmentPagerAdapter;
import com.inet.designforum.fragment.SearchNewsItemFragment;
import com.inet.designforum.util.DFContextHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SearchNewsActivity extends BaseActivity implements View.OnClickListener {
    private static final String SEARCH_NEWS_TYPE_KET = "news type";

    private static final String NEWS_GROUP_KEY = "news group";
    // 该界面的toolbar上的标题
    private static final String TITILE = "搜索";

    private TabLayout mTab;
    private ViewPager mViewPager;

    private List<Fragment> fragments;

    private int selectIndex;

    private TextView title;
    private LinearLayout back;

    private List<NewsGroup> news = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_news);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String s = intent.getStringExtra(SEARCH_NEWS_TYPE_KET);
        if(s == null){
            s = NewsGroup.NewsType.COMPETITION.getName();
            // 在主界面搜索按钮按下的时候
        }
        if (s.equals(NewsGroup.NewsType.COMPETITION.getName())) {
            selectIndex = 0;
        } else if (s.equals(NewsGroup.NewsType.INFORMATION.getName())) {
            selectIndex = 1;
        } else if (s.equals(NewsGroup.NewsType.HOT_TOPIC.getName())) {
            selectIndex = 2;
        }
        Log.e(getClass().getSimpleName(), "selectIndex:" + selectIndex);
    }

    @Override
    public void findViews() {
        mTab = findViewById(R.id.tab_search_news);
        mViewPager = findViewById(R.id.vp_search_news);
        title = findViewById(R.id.tv_toolbar_all_title);
        back = findViewById(R.id.ll_toolbar_all_back);
    }

    private static final String TAG = "SearchNewsActivity";
    private Handler handler = new DFContextHandler(this);

    @Override
    public void getData() {
        String url = DesignForumApplication.HOST + "NewsController/getNews";
        DesignForumApplication.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> Toast.makeText(SearchNewsActivity.this, "网络访问错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    JSONObject json = JSONObject.parseObject(response.body().string());
                    int code = json.getInteger("code");
                    Log.e(TAG, "onResponse: code = " + code);
                    if (code == 0) {

                        List<News> competition = new ArrayList<>(json.getJSONObject("content").getJSONArray("Competition").toJavaList(News.class));
                        List<News> information = new ArrayList<>(json.getJSONObject("content").getJSONArray("viewpoint").toJavaList(News.class));
                        List<News> hotTopic = new ArrayList<>(json.getJSONObject("content").getJSONArray("art").toJavaList(News.class));

                        List<NewsGroup> newsGroups = new ArrayList<>();

                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.COMPETITION, competition));
                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.INFORMATION, information));
                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.HOT_TOPIC, hotTopic));

                        news = newsGroups;
                        handler.post(() -> update());

                    } else {
                        Log.e(TAG, "onResponse: Response code = " + response.code());
                        handler.post(() -> Toast.makeText(SearchNewsActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void update() {
        fragments = new ArrayList<>();
        // 在这里联网获取数据啦，但是这个界面并没有什么好获取的
        fragments.add(SearchNewsItemFragment.getInstance(NewsGroup.NewsType.COMPETITION, news));
        fragments.add(SearchNewsItemFragment.getInstance(NewsGroup.NewsType.INFORMATION, news));
        fragments.add(SearchNewsItemFragment.getInstance(NewsGroup.NewsType.HOT_TOPIC, news));

        SearchNewsFragmentPagerAdapter adapter = new SearchNewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mTab.setupWithViewPager(mViewPager);
        title.setText(TITILE);
        back.setOnClickListener(this);

        Objects.requireNonNull(mTab.getTabAt(selectIndex)).select();
    }

    @Override
    public void initViews() {

    }

    public static void startActivity(Context context, String type) {
        Intent intent = new Intent(context, SearchNewsActivity.class);
        intent.putExtra(SEARCH_NEWS_TYPE_KET, type);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_toolbar_all_back) {
            finish();
        }
    }
}
