package com.example.betterdesigntwo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;


import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.FollowUserInfo;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.example.betterdesigntwo.R;
import com.example.betterdesigntwo.adapter.BetterDesignSetsAdapter;
import com.example.betterdesigntwo.bean.BetterDesignSets;
import com.example.betterdesigntwo.util.NoSlideViewpager;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.chienli.design_forum_all_lib.application.DesignForumApplication.handler;


public class BetterDesignActivity extends AppCompatActivity implements View.OnClickListener {

    String mImgBaseuri = "http://www.cyhfwq.top/designForum/images/work/";//要组成图片的前面的字符串
    List<String> mImgPaths = new ArrayList<>();//获取的图片地址值
    List<String> mImgTexts = new ArrayList<>();//获取的帖子标题集合
    private TabLayout tbTopBetterDesign;
    private RecyclerView recycBetterDesign;
    private BetterDesignSetsAdapter mBetterDesignSetsAdapter;
    List<BetterDesignSets> mBetterDesignSetsList = null;
    private EditText edFind;//edittext搜索框
    public String EDIT_TEXT = null;//用来记载edittext的常量（如果第n次和第n-1次是一样的，说明没有进行过改变，则不进行更新ui行为）
    private List<Integer> ids;////遍历jsonArray，将每个元素的id都获取出来组成一个集合
    private JSONArray jsonArraySearch;//表示的是“搜索”中的第二次获取的里面的集合
    private String responseDataSearch;//"搜索"中获取的网上的值
    private JSONObject jsontwo;//“搜索”里第二层了兄弟
    private String contentSearchOne;//这才是“搜索”里获取的第一层
    private String deepContentSearch;//"搜索"里最里面的一层

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betterdesign);
        initSteep();//加入沉浸式

        findViews();
        initView();
        tablayoutClick();//顶上的TABLAYOUT点击后的效果
        initTabLayoutOne();//刚打开时，显示的就是第一个item的内容
    }

    private void initSteep() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
    }

    private void tablayoutClick() {
        tbTopBetterDesign.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "最新", Toast.LENGTH_LONG).show();
                       // Log.d("优设计部分", "最新 ");
                        initTabLayoutNew();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "平面", Toast.LENGTH_LONG).show();
                        //Log.d("优设计部分", "平面 ");
                        initTabLayoutOne();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "编排", Toast.LENGTH_LONG).show();
                        //Log.d("优设计部分", "编排 ");
                        initTabLayoutTwo();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "标志", Toast.LENGTH_LONG).show();
                        Log.d("优设计部分", "标志 ");
                        initTabLayoutThree();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "包装", Toast.LENGTH_LONG).show();
                        Log.d("优设计部分", "包装 ");
                        initTabLayoutFour();
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(), "网页美工", Toast.LENGTH_LONG).show();
                        Log.d("优设计部分", "网页美工 ");
                        initTabLayoutFive();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(), "其他", Toast.LENGTH_LONG).show();
                        Log.d("优设计部分", "其他 ");
                        initTabLayoutSix();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /*
     * 通用，另起Ui线程来设置适配器用
     * */
    private void showTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
            }
        });
    }

    /*
     * 编辑“平面”部分
     * */
    private void initTabLayoutOne() {
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "1")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）大的List，String类型
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();//UI改变，另起ui线程实现
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 编辑"最新"部分
     * */
    private void initTabLayoutNew() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "1")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     *"平面部分"
     * */
    public void initTabLayoutTwo() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "2")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 编辑“标志”部分
     * */
    public void initTabLayoutThree() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "3")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 编辑“包装”部分
     * */
    public void initTabLayoutFour() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "4")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 编辑“网页美工”部分
     * */
    public void initTabLayoutFive() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "5")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        //Log.d("我我哦我我哦我", "获取到的第一层content");
                        //Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        // Log.d("我我哦我我哦我", "最里面需要的json数据");
                        // Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        //Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 编辑“其他”部分
     * */
    public void initTabLayoutSix() {
        mBetterDesignSetsList.clear();
        mBetterDesignSetsList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "work/getWorkList;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                FormBody body = new FormBody.Builder()
                        .add("work_type", "2")
                        .add("label_type", "6")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseJSONWithGSON(responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", content);

                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（图片list还在里面）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");

                        initBetterDesignSets(jsonArray, mBetterDesignSetsList);//获取图片的path值和workText值，并分别拼凑成List
                        mBetterDesignSetsAdapter = new BetterDesignSetsAdapter(mBetterDesignSetsList, listValue);
                        //recycBetterDesign.setAdapter(mBetterDesignSetsAdapter);
                        showTime();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 此方法用来将获取到的json数组遍历，并从中获取path值和workText值
     * */
    public void initBetterDesignSets(JSONArray jsonArray, List<BetterDesignSets> mBetterDesignSetsList) {
        BetterDesignSets set = null;
        mBetterDesignSetsList.clear();
        mImgPaths.clear();//先清空掉之前的path数组，以免把之前获取到的值又重复使用一次
        mImgTexts.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {//遍历一次json数组中的每个json对象，从中获取path值加入mImgPaths中去。
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String path = jsonObject.getString("path");
                mImgPaths.add(path);
                String imgPath = mImgBaseuri + path + "/1.jpg";//就path变，其他都固定。
                String textWork = jsonObject.getString("workText");
                Log.d("我我哦我我哦我", imgPath);
                // mImgTexts.add(textWork);
                set = new BetterDesignSets(imgPath, textWork);
                mBetterDesignSetsList.add(set);

                Log.d("我我哦我我哦我", set.getPic());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void findViews() {
        tbTopBetterDesign = findViewById(R.id.tb_topbetterdesign);
        // vpMainBetterDesign = findViewById(R.id.vp_mainbetterdesign);
        recycBetterDesign = findViewById(R.id.recyc_betterdesign);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycBetterDesign.setLayoutManager(linearLayoutManager);
        //这里获取edittext
        edFind = findViewById(R.id.tv_all_toolbar_search);
        edFind.setOnClickListener(this);
    }

    private void initView() {
        //  给标题栏部分里面每个格子设置对应文字。
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("最新"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("平面"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("编排"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("标志"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("包装"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("网页美工"));
        tbTopBetterDesign.addTab(tbTopBetterDesign.newTab().setText("其他"));
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_all_toolbar_search) {
            Toast.makeText(getApplicationContext(), "有效了", Toast.LENGTH_SHORT).show();
            String EDIT_TEXT_Next = edFind.getText().toString();//获取本次搜索框中的内容。
            if (EDIT_TEXT_Next != null && EDIT_TEXT_Next != "") {//如果搜索框确实是有内容，则进行搜索
                mBetterDesignSetsList.clear();
                mBetterDesignSetsList = new ArrayList<>();
                //先模糊搜索获得id，再将数据拼接。 将所需要的数据传递给下一个活动，下一个活动只需要set适配器就好了。
                //需要传递：1.获取最终的详情List（图片list还在里面）（string类型）；2.initBetterDesignSets(jsonArray, mBetterDesignSetsList)加工过的mBetterDesignSetsList
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        String uri1 = DesignForumApplication.HOST + "work/getSeekWork;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                        //String uri1 = "https://www.cyhfwq.top/designForum/work/getWorkList";
                        FormBody body = new FormBody.Builder()
                                .add("work_type", "2")
                                .add("work_text", "精致的")//将搜索框中的内容进行搜查。
                                .build();
                        Request request = new Request.Builder()
                                .url(uri1)
                                .post(body)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            responseDataSearch = response.body().string();
                            //parseJSONWithGSON(responseData);
                            try {
                                if (responseDataSearch != null) {

                                    JSONObject json = new JSONObject(responseDataSearch);
                                    //成功，可以获取第一层content内的数据
                                    contentSearchOne = json.getString("content");
                                    Log.d("我我哦我我哦我", "获取到的第一层content");

                                    jsontwo = new JSONObject(contentSearchOne);//将第一层的数据转换成jsonobj类型
                                    //最里面的了。
                                    deepContentSearch = jsontwo.getString("content");
                                    Log.d("我我哦我我哦我", "最里面需要的json数据");
                                    Log.d("我我哦我我哦我", deepContentSearch);
                                    //里面的集合，每个对象，带有一个id，即使我们所需要作为跳板获取完成信息的id。
                                    jsonArraySearch = new JSONArray(deepContentSearch);
                                    ids = new ArrayList<>();
                                    for (int i = 0; i < jsonArraySearch.length(); i++) {//遍历jsonArray，将每个元素的id都获取出来组成一个集合
                                        JSONObject object = jsonArraySearch.getJSONObject(i);
                                        int id = object.getInt("id");
                                        ids.add(id);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "没有搜索到相关信息哦~", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent intent = new Intent(BetterDesignActivity.this, BetterDesignSearchActivity.class);
                //intent.putExtra("ids" , ids.toString());
                //startActivity(intent);//把所有的id集合发给下一个活动
                if (ids != null) {
                    Toast.makeText(getApplicationContext(), ids.toString(), Toast.LENGTH_SHORT).show();
                    //遍历ids中每个id，从网上获取数据

                    JSONArray idsSearchArray = new JSONArray();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < ids.size(); j++) {
                                OkHttpClient client = new OkHttpClient();
                                String uri1 = DesignForumApplication.HOST + "work/getWorkInId;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                                //http://www.cyhfwq.top/designForum/work/getWorkInId?id_str=90用id获取jsonobject。
                                FormBody body = new FormBody.Builder()
                                        .add("id_str", String.valueOf(ids.get(j)))//将搜索框中的内容进行搜查。
                                        .build();
                                Request request = new Request.Builder()
                                        .url(uri1)
                                        .post(body)
                                        .build();
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                    responseDataSearch = response.body().string();
                                    JSONObject json = new JSONObject(responseDataSearch);//每个获取到的大对象，两层剥开后才是完整作品的jsonobj
                                    String idsOne = json.getString("content");//获取的第一层里面的内容
                                    JSONObject jsonObject = new JSONObject();
                                    //JSONObject jsonObject;
                                    //idsSearchArray.add(Json);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }).start();
                }
                //Toast.makeText(getApplicationContext(),ids.toString() , Toast.LENGTH_SHORT).show();

            }


        }
    }
}
