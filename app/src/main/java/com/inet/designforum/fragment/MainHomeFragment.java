package com.inet.designforum.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.News;
import com.chienli.design_forum_all_lib.bean.NewsGroup;
import com.chienli.micro_class.ui.activity.MicroClassMainActivity;
import com.example.betterdesigntwo.activity.BetterDesignActivity;
import com.inet.designforum.R;
import com.inet.designforum.activity.SearchNewsActivity;
import com.inet.designforum.adapter.MainHomeNewsGroupRecyclerAdapter;
import com.inet.designforum.fragment.base.BaseFragment;
import com.inet.designforum.util.DFFragmentHandler;
import com.inet.designforum.workwall.activity.WorkWallOverviewActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainHomeFragment extends BaseFragment implements View.OnClickListener {

    private Banner mBanner;
    private LinearLayout mBatterDesignLL;
    private LinearLayout mSmallCourseLL;
    private LinearLayout mWorksWallLL;
    private RecyclerView mRecyclerNewsGroup;
    private NestedScrollView mScroll;

    private List<Integer> bannerImgUrls;
    // TODO : News
    private List<News> competition;
    private List<News> information;
    private List<News> hotTopic;

    private List<NewsGroup> newsGroups;

    // toolbar上的搜索视图
    private TextView mSearch;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            throw new NullPointerException("Fragment is a container, but the container is null");
        }
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);
        // 在此调用init方法来初始化界面, 虽然最终的效果是自己实现的
        super.init(view);
        return view;
    }

    @Override
    public void findViews(View mView) {
        mBanner = mView.findViewById(R.id.banner_main_home);
        mBatterDesignLL = mView.findViewById(R.id.ll_main_home_batter_design);
        mSmallCourseLL = mView.findViewById(R.id.ll_main_home_small_course);
        mWorksWallLL = mView.findViewById(R.id.ll_main_home_works_wall);
        mRecyclerNewsGroup = mView.findViewById(R.id.recycler_main_home_news_group);
        mScroll = mView.findViewById(R.id.scroll_main_home);

        mSearch = mView.findViewById(R.id.tv_all_toolbar_search);
    }

    @Override
    public void getData(View mView) {
        bannerImgUrls = new ArrayList<>();
        bannerImgUrls.add(R.drawable.better_design);
        bannerImgUrls.add(R.drawable.small_coures);
        bannerImgUrls.add(R.drawable.work_wall);

        // TODO : News
        competition = new ArrayList<>();
        information = new ArrayList<>();
        hotTopic = new ArrayList<>();

        newsGroups = new ArrayList<>();
    }


    private MainHomeNewsGroupRecyclerAdapter adapter;

    @Override
    public void initViews(View mView) {
        // init Banner
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setImages(bannerImgUrls);
        BannerOnClickListenerAndImageLoader bannerOnClickListenerAndImageLoader = new BannerOnClickListenerAndImageLoader();
        mBanner.setOnBannerListener(bannerOnClickListenerAndImageLoader);
        mBanner.setImageLoader(bannerOnClickListenerAndImageLoader);
        mBanner.setDelayTime(4000);
        mBanner.start();
        mBanner.startAutoPlay();

        // init mRecyclerNewsGroup
        adapter = new MainHomeNewsGroupRecyclerAdapter(newsGroups);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerNewsGroup.setLayoutManager(manager);
        mRecyclerNewsGroup.setAdapter(adapter);

        // init BatterDesign, SmallCourse, WorksWall
        mBatterDesignLL.setOnClickListener(this);
        mSmallCourseLL.setOnClickListener(this);
        mWorksWallLL.setOnClickListener(this);

        mSearch.setOnClickListener(this);

        updateData();
    }

    private Handler handler = new DFFragmentHandler<>(this);
    private static final String TAG = "MainHomeFragment";

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateData() {
        String url = DesignForumApplication.HOST + "NewsController/getNews";
        DesignForumApplication.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> Toast.makeText(getActivity(), "网络访问错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    JSONObject json = JSONObject.parseObject(response.body().string());
                    int code = json.getInteger("code");
                    Log.e(TAG, "onResponse: code = " + code);
                    if (code == 0) {
                        competition.addAll(json.getJSONObject("content").getJSONArray("Competition").toJavaList(News.class));
                        information.addAll(json.getJSONObject("content").getJSONArray("viewpoint").toJavaList(News.class));
                        hotTopic.addAll(json.getJSONObject("content").getJSONArray("art").toJavaList(News.class));


                        newsGroups.clear();
                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.COMPETITION, competition));
                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.INFORMATION, information));
                        newsGroups.add(new NewsGroup(NewsGroup.NewsType.HOT_TOPIC, hotTopic));
                        adapter.update(newsGroups);

                        handler.post(() -> adapter.notifyDataSetChanged());

                    } else {
                        handler.post(() -> Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_main_home_batter_design) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getContext(), "您还未登录", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(getActivity(), BetterDesignActivity.class));
        } else if (id == R.id.ll_main_home_small_course) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getContext(), "您还未登录", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(getActivity(), MicroClassMainActivity.class));
        } else if (id == R.id.ll_main_home_works_wall) {
            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getContext(), "您还未登录", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(getActivity(), WorkWallOverviewActivity.class));
        } else if (id == R.id.tv_all_toolbar_search) {
            // TODO : 主页开启的应该是一个搜索的界面 SearchActivity 现在临时导向 SearchNewsActivity
            startActivity(new Intent(getActivity(), SearchNewsActivity.class));
        }
    }

    private class BannerOnClickListenerAndImageLoader extends ImageLoader implements OnBannerListener {

        private BannerOnClickListenerAndImageLoader instance;

        public BannerOnClickListenerAndImageLoader getInstance() {
            if (instance == null) {
                instance = new BannerOnClickListenerAndImageLoader();
            }
            return instance;
        }

        public BannerOnClickListenerAndImageLoader() {
        }

        @Override
        public void OnBannerClick(int position) {
            // 在这里处理Banner的点击事件
            // 第一张图片是自己的开启优设计的路径

            // 第二张图片是自己开启微课堂的路径

            // 第三张是开启作品墙

            if (!DesignForumApplication.mainBinder.isLogin()) {
                Toast.makeText(getContext(), "您还未登录", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (position) {
                case 0: {

                    break;
                }
                case 1: {
                    startActivity(new Intent(getActivity(), MicroClassMainActivity.class));
                    break;
                }
                case 2: {
                    startActivity(new Intent(getActivity(), WorkWallOverviewActivity.class));
                    break;
                }
            }
        }

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            // 在这里的使用自定义的图片加载框架
            // 主要的功能还是加载图片
            Glide.with(context).load((int) path).into(imageView);
        }
    }
}
