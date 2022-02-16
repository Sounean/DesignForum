package com.example.betterdesigntwo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.example.betterdesigntwo.R;
import com.example.betterdesigntwo.adapter.BetterDesignContentCommentAdapter;
import com.example.betterdesigntwo.adapter.BetterDesignContentImageAdapter;
import com.example.betterdesigntwo.bean.BetterDesignContentCommentItems;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BetterDesignDeepActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rvContentComment;
    RecyclerView rvContentImg;

    BetterDesignContentImageAdapter imgAdapter;
    Toolbar toptoolbar;

    private Handler handler = new Handler(Looper.getMainLooper());

    private List<String> mImg = new ArrayList<>();//上方图片的图片地址集合。
    private String mListValues;
    private int mPositon;
    private JSONArray jsonArray = null;
    private int mId;//作品id（通过这个获取评论信息）
    private List<BetterDesignContentCommentItems> commentItems;//评论区的集合
    private TextView tvChartNum;
    private Button btnSend;
    private EditText etSend;
    private BetterDesignContentCommentAdapter commentAdapter;//评论区的适配器
    private int likenum;//点赞个数
    private int chartNum;//评论条数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betterdesigndeep);

        getValues();//获取上一个活动获取的数据（小list以及作品id）
        initfind();//获取对象
        initBase();//基础的初始化（软键盘和透明状态栏）
        initBetterDesignSets();//初始化优设计的作品墙
        initBetterDesignComment();//初始化评论区(在其中有新建方法从作品id获取评论区对象BetterDesignContentCommentItems)
    }


    /*
     * 初始化优设计的评论区
     * */
    private void initBetterDesignComment() {
        commentItems = new ArrayList<>();//评论区的集合
        getCommentList();//通过作品id获取评论区的资源
    }


    /*
     * 通过作品id获取评论区资源（初始化掉commentItems评论区集合）
     * */
    private void getCommentList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String uri1 = DesignForumApplication.HOST + "comment/getComment;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                FormBody body = new FormBody.Builder()
                        .add("discuss_type", "2")
                        .add("id", mId + "")
                        .build();
                Request request = new Request.Builder()
                        .url(uri1)
                        .post(body)
                        .build();

                Response response = null;
                try {
                    Log.d("我我哦我我哦我", "10");
                    response = client.newCall(request).execute();
                    Log.d("我我哦我我哦我", "11");
                    Log.d("我我哦我我哦我", "1");
                    String responseData = response.body().string();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(responseData);
                        Log.d("我我哦我我哦我", "9");
                        String content = json.getString("content");//成功，可以获取第一层content内的数据
                        Log.d("我我哦我我哦我", "获取到的第一层content");
                        Log.d("我我哦我我哦我", "8");
                        JSONObject jsontwo = new JSONObject(content);
                        String deepContent = jsontwo.getString("content");//获取第二层content内的数据
                        Log.d("我我哦我我哦我", "最里面需要的json数据");
                        Log.d("我我哦我我哦我", deepContent);

                        JSONObject jsonLast = new JSONObject(deepContent);
                        String listValue = jsonLast.getString("list");//获取最终的详情List（评论区集合们）
                        JSONArray jsonArray = new JSONArray(listValue);
                        Log.d("我我哦我我哦我", jsonArray.length() + "");
                        Log.d("评论区楼层数（就是评论区集合们的对象数）", jsonArray.length() + "");
                        handler.post(()->initCommentItems(jsonArray, commentItems));//初始化评论区对象的集合
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
     * 初始化评论区对象的集合
     * */
    private void initCommentItems(JSONArray jsonArray, List<BetterDesignContentCommentItems> commentItems) {
        BetterDesignContentCommentItems items = null;//每次遍历jsonarry评论区对象数组时，都新增一个items用来添加进总的评论区对象集合里去。
        commentItems.clear();//每次打开，都清楚一次评论区集合，这样就不会遗留上一次的东西了。
        for (int i = 0; i < jsonArray.length(); i++) {//遍历一次jsonArray。把每个完整的网页反馈的评论区给获取出discussText。
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String commit = jsonObject.getString("discussText");//commit即为评论区里每层楼的留言。
                // int userhead = jsonObject.getInt("userHead");//获取userHead（因为网页那边还没有给头像设置值，索性我们这里就放默认的）
                //String userName = jsonObject.getString("userName");//获取userName(网页未给，我们自己写一个默认的)
                //设置默认点赞数为0
                likenum = 0;
                items = new BetterDesignContentCommentItems(R.drawable.better_design_comment_user_head_two, "露爷", likenum, commit);//新建一个评论对象。
                commentItems.add(items);//把此对象添加到commentItems评论区集合中去。
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("我我哦我我哦我", "commentItems长度为：" + commentItems.size() + "");
        Log.d("我我哦我我哦我", "commentItems内容为：" + commentItems + "");
        Log.d("我我哦我我哦我", "设置适配器前的commentItems数量：" + commentItems.size());
        //貌似把获取评论区数据放到子线程中的话，有可能主线程先执行完了（先给评论区给设置了适配器了），会造成传给适配器的其实是空的
        chartNum = commentItems.size();

        tvChartNum.setText(chartNum + "条评论");//设置“评论区楼层数量”
        /*+++++++++++++++++++这里进行一次尝试，将获取的list集合顺序反过来（不需要反过来！！！！！）+++++++++++++++++++++*/
       /* if (commentItems!= null && commentItems.size()!=0){
            Collections.reverse(commentItems);
        }
*/
        /*++++++++++++++++++++++++++++++++++++++++*/
        commentAdapter = new BetterDesignContentCommentAdapter(commentItems);//评论区的适配器（传递进去评论区的资源）
        rvContentComment.setAdapter(commentAdapter);
    }


    /*
     * 上方图片的初始化以及加载已经完成
     *返回栏也已经加载完成
     *
     *
     *
     *
     *
     *
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//显示“返回”按钮
        getMenuInflater().inflate(R.menu.betterdesigndeeptoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//设置“返回”按钮的点击事件
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BetterDesignDeepActivity.this, BetterDesignActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    private void initBetterDesignSets() {
        imgAdapter = new BetterDesignContentImageAdapter(mImg);
        rvContentImg.setAdapter(imgAdapter);
    }

    private void initBase() {
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // 透明式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initfind() {
        toptoolbar = findViewById(R.id.betterdesigntoolbar);
        setSupportActionBar(toptoolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.betterdesignback);
        }

        rvContentImg = findViewById(R.id.rv_better_design_content_image);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvContentImg.setLayoutManager(manager);
        rvContentComment = findViewById(R.id.rv_better_design_content_comment);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        rvContentComment.setLayoutManager(manager2);

        tvChartNum = findViewById(R.id.tv_better_design_content_comment_num_bottom); //获取评论数量的tv

        btnSend = findViewById(R.id.btn_work_wall_content_send_msg);//获取点击“发送”按钮的btn
        btnSend.setOnClickListener(this);

        etSend = findViewById(R.id.et_work_wall_content_type_word);//获取edittext中的内容
    }

    /*
     * 获取上一个活动得到的数据
     * 初始化完上方图片的list数组
     * */
    public void getValues() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 1);
        mListValues = intent.getStringExtra("values");//从上一个activity中获取小的list jsonarray
        try {
            JSONObject jsontwo = new JSONObject(mListValues);
            String listValue = jsontwo.getString("workImageList");//获取最终的详情List（图片list还在里面）
            //最里面的jsonArray
            jsonArray = new JSONArray(listValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPositon = intent.getIntExtra("position", 0);
        String path = intent.getStringExtra("path");
        // Toast.makeText(this , mId+"" , Toast.LENGTH_LONG).show();

        /*
         * 初始化上方图片的list组。
         * */
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String aUri = jsonArray.getString(i);
                mImg.add("http://www.cyhfwq.top/designForum/images/work/" + path + "/" + aUri);

                Log.d("aaaaaaaaa", mImg.get(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    /*
     * 发送按钮的点击事件
     * */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_work_wall_content_send_msg) {
            String sendMsg = etSend.getText().toString();//获取edittext中的内容
            if (sendMsg != null && sendMsg != "" && sendMsg.length() < 50) {//点击发送按钮，先判断是否为空后，产生子线程进行发送操作。
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient postClient = new OkHttpClient();
                        String uri = DesignForumApplication.HOST + "comment/insComment;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                        RequestBody formBody = new FormBody.Builder()   //创建表单请求体，具体的参数设置也要去看源码才更清楚
                                .add("discussType", "2")      //表示类型是优设计
                                .add("parentId", mId + "")
                                .add("discussText", sendMsg)
                                .build();
                        Request request = new Request.Builder()//创建Request 对象。
                                .url(uri)
                                .post(formBody)//传递请求体   //与get的区别在这里
                                .build();
                        postClient.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(BetterDesignDeepActivity.this, "上传评论失败！网络信号差", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        });
                    }
                }).start();
                BetterDesignContentCommentItems postItem = new BetterDesignContentCommentItems(R.drawable.better_design_comment_user_head_two, "露爷", 0, sendMsg);//新建一个评论对象。
                commentItems.add(postItem);//将新增加的评论分装成一个评论区对象加入进去
                //Toast.makeText(this , postItem.getCommentwords() , Toast.LENGTH_LONG).show();
                if (commentAdapter != null) {
                    commentAdapter.notifyItemInserted(commentItems.size() - 1);
                    chartNum++;//评论条数加1
                    tvChartNum.setText(chartNum + "条评论");//设置“评论区楼层数量”
                } else {
                    commentAdapter = new BetterDesignContentCommentAdapter(commentItems);
                    rvContentComment.setAdapter(commentAdapter);
                    tvChartNum.setText(commentItems.size() + "条评论");//设置“评论区楼层数量”
                }
                //commentAdapter = new BetterDesignContentCommentAdapter(commentItems);
                //initBetterDesignComment();
                rvContentComment.scrollToPosition(commentItems.size() - 1);
                //当评论增加时，刷新Recycle中断的显示
                etSend.setText("");//每次提交完就对edittext进行一次清空操作,保证不对下一次输入操作进行干扰。
            } else {
                if (sendMsg == null && sendMsg == "") {
                    Toast.makeText(BetterDesignDeepActivity.this, "请输入要发送的内容！", Toast.LENGTH_LONG).show();
                } else if (sendMsg.length() >= 50) {
                    Toast.makeText(BetterDesignDeepActivity.this, "发送的内容长度不能超过50！", Toast.LENGTH_LONG).show();
                }

            }
        }
        //将输入法隐藏，mPasswordEditText 代表密码输入框
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSend.getWindowToken(), 0);
    }
}
