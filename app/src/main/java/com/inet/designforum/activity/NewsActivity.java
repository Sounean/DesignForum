package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.Content;
import com.chienli.design_forum_all_lib.bean.News;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.adapter.NewsRecyclerAdapter;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 新闻的具体展示页面，使用startNewsActivity这个静态方法，接收News对象来启动界面
 */
public class NewsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewsActivity";
    private News news;
    //    private RecyclerView mRecycler;
    // 这个是Toolbar中的title
    private TextView title;
    //    private TextView contentTitle;
//    private TextView otherInfo;
    private LinearLayout back;
    private WebView web;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_news);
        super.onCreate(savedInstanceState);


        Observable
                .create(new ObservableOnSubscribe<JSONObject>() {
                    @Override
                    public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                        String url = DesignForumApplication.HOST + "NewsController/getNewsById";

                        FormBody body = new FormBody.Builder()
                                .add("id", String.valueOf(news.getId()))
                                .build();

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Response response = DesignForumApplication.client.newCall(request).execute();

                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = JSON.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            if (code == 0) {
                                emitter.onNext(json);
                            } else {
                                emitter.onError(new Exception("数据访问错误 json code = " + code));
                            }
                        } else {
                            emitter.onError(new Exception("数据访问错误 resp = " + response));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, String>() {
                    @Override
                    public String apply(JSONObject json) throws Exception {
                        // 将对应的JSON数据转换成html文本
                        String title = "<h2 style=\"text-align: center;\">" + json.getJSONObject("content").getJSONObject("news").getString("newsTitle") + "</h2>";
                        String htmlBody = json.getJSONObject("content").getJSONObject("news").getString("newsText");
                        String newsTime = "<p style=\"color:#666666;text-align: center;\" >发布时间: " + json.getJSONObject("content").getJSONObject("news").getString("newsTime") + "</p>";
                        String teacher = "<p style=\"color:#666666;text-align: center;\" >发布人: " + json.getJSONObject("content").getJSONObject("news").getString("userName") + "</p>";
                        return "<html><head></head><body>" + title + newsTime + teacher + htmlBody + "</body></html>";
                    }
                })
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        web.loadData(s, "text/html", "utf-8");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        Toast.makeText(NewsActivity.this, "网络访问出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void findViews() {
//        mRecycler = findViewById(R.id.news_recycler);
        title = findViewById(R.id.tv_toolbar_all_title);
//        contentTitle = findViewById(R.id.tv_news_title_content);
//        otherInfo = findViewById(R.id.tv_news_other_info);
        back = findViewById(R.id.ll_toolbar_all_back);
        web = findViewById(R.id.web);
    }

    @Override
    public void getData() {
        Intent intent = getIntent();
        news = (News) intent.getSerializableExtra(NEWS_KEY);
    }

    @Override
    public void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_all);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(news.getTitle());
//        } else {
//            Log.e(getClass().getSimpleName(), "ActionBar is null");
//        }
        back.setOnClickListener(this);

        title.setText(news.getNewsTitle());
//        contentTitle.setText(news.getNewsTitle());
//        String sb = "作者:" +
//                news.getUserName() +
//                "\t" +
//                "时间:" +
//                (news.getNewTime() == null ? "NULL" : news.getNewTime().substring(0, 10));
//        otherInfo.setText(sb);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        List<Content> contents = new ArrayList<>();
//        contents.add(new Content(news.getNewsText(), Content.ContentType.TEXT));
////        if (news.getNewsImageList() != null) {
////            for (String i : news.getNewsImageList()) {
////                contents.add(new Content(i, Content.ContentType.IMG));
////            }
////        }
//        mRecycler.setAdapter(new NewsRecyclerAdapter(contents, news));
//        mRecycler.setLayoutManager(manager);
//        web.loadUrl(DesignForumApplication.HOST + "NewsController/getNewsById?id=" + news.getId());
    }

    private static final String NEWS_KEY = "News";

    /**
     * 使用这个方法来开启该界面
     *
     * @param context 上下文对象
     * @param news    News对象，包含新闻的信息
     */
    public static void startNewsActivity(Context context, News news) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(NEWS_KEY, news);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_toolbar_all_back) {
            finish();
        }
    }
}
