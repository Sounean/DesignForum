package com.inet.designforum.workwall.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.chienli.design_forum_all_lib.service.MainServices;
import com.google.gson.Gson;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.activity.base.BaseActivity;
import com.inet.designforum.workwall.adapter.WorkWallContentCommentAdapter;
import com.inet.designforum.workwall.adapter.WorkWallContentImageAdapter;
import com.inet.designforum.workwall.bean.WorkComment;
import com.inet.designforum.workwall.bean.WorkInfo;
import com.inet.designforum.workwall.util.HttpUtil;
import com.inet.designforum.workwall.util.MyContentUtil;
import com.inet.designforum.workwall.util.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WorkWallContentActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "【WorkWallContent】";

    private ImageView ivLikeIcon;
    private Button btnClickConcern;

    private boolean bInfo;
    private LinearLayout llWorkInfo;
    private RelativeLayout rlShowInfo;
    private ImageView ivChangeArrow;
    private LinearLayout llClickLikeIconArea;
    private RecyclerView rvContentImg;
    private RecyclerView rvContentComment;


    // 定义一个子线程与主线程交互的 Handler
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case -3:    // 表示发送评论失败
                    Toast.makeText(WorkWallContentActivity.this, "发送评论失败", Toast.LENGTH_SHORT).show();
                    break;
                case -2:    // 显示网络异常
                    pageNum--;
                    tvShowLoadWord.setText("加载更多");
                    Toast.makeText(WorkWallContentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case -1: // 用于显示服务器正忙
                    pageNum--;
                    tvShowLoadWord.setText("加载更多");
                    Toast.makeText(WorkWallContentActivity.this, "服务器正忙", Toast.LENGTH_SHORT).show();
                    break;
                case 0: // 0表示数据正常获取
                    int total = msg.arg1;   // 获取评论总数
                    tvCommentNumTop.setText("" + total);
                    tvCommentNumBottom.setText(total + " 条评论");
                    adapter1.setmWorkCommentList(workCommentList);
                    adapter1.notifyDataSetChanged();
                    break;
                case 1:
                    etInputComment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etInputComment.getWindowToken(), 0);
                    Toast.makeText(WorkWallContentActivity.this, "发送评论成功", Toast.LENGTH_SHORT).show();
                    break;
                case 10:    // 表示加载更多评论
                    tvShowLoadWord.setText("加载更多");
                    llLoadMore.setEnabled(true);
                    break;
                case 11:    // 表示没有更多评论了
                    tvShowLoadWord.setText("没有更多内容了");
                    llLoadMore.setEnabled(false);
                    break;
                case 21: //评论点赞成功
                    WorkComment workComment = workCommentList.get(pos);
                    int commentId = workComment.getId();
                    int commentLikeNum = workComment.getDiscussLikeSize();
                    userInfo.getDiscuss_like().add(commentId);
                    commentLikeNum++;
                    holder.tvCommentLikeNum.setText("" + commentLikeNum);
                    holder.ivCommentLike.setImageResource(R.drawable.ic_work_wall_content_like_icon_selected);
                    Toast.makeText(WorkWallContentActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                    break;
                case 22: //作品点赞成功
                    hasWorkLike = true;
                    int workId = mWorkInfo.getId();
                    int workLikeNum = mWorkInfo.getWorkLikeSize();
                    userInfo.getProduction_like().add(workId);
                    workLikeNum++;
                    tvLikeNum.setText("" + workLikeNum);
                    ivLikeIcon.setImageResource(R.drawable.ic_work_wall_content_like_icon_selected);
                    Toast.makeText(WorkWallContentActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                    break;
                case 23: //作品收藏成功
                    hasWorkCollection = true;
                    int workId2 = mWorkInfo.getId();
                    int workCollect = mWorkInfo.getWorkCollect();
                    userInfo.getProduction_collect().add(workId2);
                    workCollect++;
                    tvCollectionNum.setText("" + workCollect);
                    ivCollectionIcon.setImageResource(R.drawable.ic_work_wall_content_collection_icon_selected);
                    Toast.makeText(WorkWallContentActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    break;
                case 24:
                    hasUserConcerned = true;
                    int concernedUserId = mWorkInfo.getUserId();
                    userInfo.getAttention().add(concernedUserId);   //加入被关注者的id
                    btnClickConcern.setText("已关注");
                    Toast.makeText(WorkWallContentActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    break;
                case 25:
                    hasUserConcerned = false;
                    int userId1 = mWorkInfo.getUserId();
                    userInfo.getAttention().remove(Integer.valueOf(userId1));    //移除被关注者的id
                    btnClickConcern.setText("关注");
                    Toast.makeText(WorkWallContentActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                    break;

            }
            return false;
        }
    });

    // 存储从json转换后的WorkComment对象信息
    private List<WorkComment> workCommentList = new ArrayList<>();

    private String mJsonData;
    private WorkInfo mWorkInfo;
    private ImageView ivWorkPic;
    private ImageView ivPublisherHead;
    private TextView tvPublisherName;
    private TextView tvPublisherTypeText;
    private TextView tvWorkName;
    private LinearLayout llClickCollectionIconArea;
    private ImageView ivCollectionIcon;
    private TextView tvCopyrightInfo;
    private TextView tvWorkTypeInfo;
    private TextView tvLikeNum;
    private TextView tvCollectionNum;
    private TextView tvWorkUpTime;
    private TextView tvCommentNumTop;
    private TextView tvCommentNumBottom;
    private Button btnSendMsg;
    private EditText etInputComment;
    private LinearLayout llLoadMore;
    private TextView tvShowLoadWord;
    private int pageNum = 1;    //初始化pageNum为1
    private WorkWallContentCommentAdapter adapter1;

    private static final int COMMENT_LIKE = 1;  //评论点赞
    private static final int WORK_LIKE = 2; // 作品点赞
    private static final int WORK_COLLECTION = 3;   // 作品收藏
    private static final int USER_CONCERNED = 4;    // 发布者关注
    private static final int USER_NOT_CONCERNED = 5;    // 发布者取消关注


    // 获得DesignForumApplication中的常量
    public static MainServices.MainBinder mainBinder;
    public static String HOST; // 请求域名开头
    public static UserInfo userInfo;    // 公开UserInfo信息
    private WorkWallContentCommentAdapter.MyViewHolder holder;
    private int pos;
    private boolean hasUserConcerned;
    private boolean hasWorkLike;
    private boolean hasWorkCollection;

    private void getApplicationData() {
        try {
            Thread.sleep(200);  //确保获取到数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainBinder = DesignForumApplication.mainBinder;
        userInfo = mainBinder.getUserInfo();
        HOST = DesignForumApplication.HOST;
        List<Integer> discuss_like = userInfo.getDiscuss_like();
        Log.e(TAG, discuss_like.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_work_wall_content);
        // 获得本应用的数据
        getApplicationData();
        super.onCreate(savedInstanceState);
        initListener(); // 初始化监听器

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // 启用透明式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            changeStatusBar();
        }

        initContent();  // 初始化内容

        // 设置显示作品图片内容的RecyclerView的Adapter
        setRvContentImgAdapter();

        // 设置评论信息内容的RecyclerView的Adapter
        setRvContentCommentAdapter();
    }


    // 初始化活动界面
    private void initContent() {
        // 1.第一张图片
        List<String> workImageList = mWorkInfo.getWorkImageList();
        String imgPath = mWorkInfo.getPath();
        String workImgUrl = HOST + "/images/work/" + imgPath + "/" + workImageList.get(0);
        Glide.with(this).load(workImgUrl).into(ivWorkPic); // 把第一张图片加载到imageView中

        // 2.发布者头像
        String userHead = mWorkInfo.getUserHead();
        String headImgUrl = HOST + "/images/head/" + userHead;
        Glide.with(this).load(headImgUrl).into(ivPublisherHead);

        // 3.发布者名称
        String userName = mWorkInfo.getUserName();
        tvPublisherName.setText(userName);

        // 4.发布作品的类别
        String workLabelText = mWorkInfo.getWorkLabelText();
        tvPublisherTypeText.setText(workLabelText);

        // 5.发布作品名称
        String workText = mWorkInfo.getWorkText();
        tvWorkName.setText(workText);

        // 6.关注按钮的文字初始化
        List<Integer> attention = userInfo.getAttention();
        if (attention.contains(mWorkInfo.getUserId())) {
            btnClickConcern.setText("已关注");
            hasUserConcerned = true;
        } else {
            btnClickConcern.setText("关注");
        }

        // 7.喜欢图标的设置
        int workId = mWorkInfo.getId(); //作品id
        if (userInfo.getProduction_like().contains(workId)) {
            hasWorkLike = true;
            ivLikeIcon.setImageResource(R.drawable.ic_work_wall_content_like_icon_selected);
        }
        // 8.喜欢数量
        int likeNum = mWorkInfo.getWorkLikeSize();
        tvLikeNum.setText("" + likeNum);

        // 9.收藏图标
        if (userInfo.getProduction_collect().contains(workId)) {
            hasWorkCollection = true;
            ivCollectionIcon.setImageResource(R.drawable.ic_work_wall_content_collection_icon_selected);
        }

        // 10.收藏数量
        int collectionNum = mWorkInfo.getWorkCollect();
        tvCollectionNum.setText("" + collectionNum);

        // 11.下拉显示信息，作品分类标签信息
        tvWorkTypeInfo.setText(workLabelText);

        // 12.下拉显示信息，作品上传时间
        String workTime = mWorkInfo.getWorkTime();
        String time = MyUtil.getUpdateTime(workTime);   //得到需要的时间字符串
        tvWorkUpTime.setText(time);

        // 13.下拉显示信息，版权信息
        String copyrightInfo = "本作品最终版权解释归 " + userName + " 所有，本作品禁止匿名转载，禁止商业使用";
        tvCopyrightInfo.setText(copyrightInfo);

        // 14.上面、底部评论数目
        /* 可以不写，因为每次获取评论都会刷新获得评论数目 */
        int workDiscussSize = mWorkInfo.getWorkDiscussSize();
        tvCommentNumTop.setText("" + workDiscussSize);
        tvCommentNumBottom.setText(workDiscussSize + " 条评论");
    }


    // 启动活动，设置所需数据
    public static void actionStart(Context context, String json) {
        Intent intent = new Intent(context, WorkWallContentActivity.class);
        intent.putExtra("jsonData", json);
        context.startActivity(intent);
    }


    private void changeStatusBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }


    private void setRvContentImgAdapter() {
        // 进行WorkWallContent作品的导入
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvContentImg.setLayoutManager(manager);

        WorkWallContentImageAdapter adapter = new WorkWallContentImageAdapter(this, mWorkInfo);

        // 接口回调【进行图片内容的放大】
        adapter.setOnItemClickListener(new WorkWallContentImageAdapter.OnItemImgClickListener() {
            @Override
            public void onItemImgClick(String imgUrl) {
                // 进行大图预览活动的启动
                WorkWallContentImageShowActivity.actionStart(WorkWallContentActivity.this, imgUrl);
            }
        });
        rvContentImg.setAdapter(adapter);
    }


    private void setRvContentCommentAdapter() {
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        rvContentComment.setLayoutManager(manager1);

        adapter1 = new WorkWallContentCommentAdapter(this, workCommentList);
        rvContentComment.setAdapter(adapter1);

        // 设置接口回调
        adapter1.setOnCommentLikeClickedListener(new WorkWallContentCommentAdapter.onCommentLikeClickedListener() {
            @Override
            public void onCommentLikeClicked(WorkWallContentCommentAdapter.MyViewHolder myViewHolder, int position) {
                // 得到评论信息，注：里面和外面的workCommentList是相同的
                WorkComment workComment = workCommentList.get(position);
                int userId = userInfo.getId();
                int commentId = workComment.getId();   //得到该评论的id
                Log.e(TAG, "评论者的id:" + userId);
                Log.e(TAG, "评论的id:" + commentId);
                holder = myViewHolder;
                pos = position;

                String address = HOST + "/userHistory/worksWallCommectLike"
                        + ";jsessionid=" + mainBinder.getLocalJSessionId();
                Map<String, String> map = new HashMap<>();
                map.put("userId", "" + userId);
                map.put("targetId", "" + commentId);

                // 发送评论点赞信息
                MyContentUtil.sendOptionInfo(address, map, handler, COMMENT_LIKE);
            }
        });
    }


    @Override
    public void findViews() {
    }


    @Override
    public void getData() {
        // 获取作品的json数据
        Intent intent = getIntent();
        mJsonData = intent.getStringExtra("jsonData");

        // 解析 json，获取传入的作品内容的数据，得到 WorkInfo 保存到 mWorkInfo 中
        Gson gson = new Gson();
        mWorkInfo = gson.fromJson(mJsonData, WorkInfo.class);

        // 获取作品评论的数据
        jsonToWorkCommentList();
    }

    private void jsonToWorkCommentList() {
        // 获取json数据，不传其他参数时获取的是第1页内容，10条信息
        /**
         *  可传入的参数
         *  1.page_num 获取当前页码的信息
         *  2.page_size 获取该页显示的条数
         *  3.show_page_num 连续显示的页码
         *  4.label_type 获取分类
         */
        int id = mWorkInfo.getId(); //得到作品的id，作为获取评论的参数
        String address = HOST + "comment/getComment;jsessionid="
                + mainBinder.getLocalJSessionId() + "?discuss_type=1&id=" + id + "&page_num=" + pageNum;
        MyContentUtil.dealWorkCommentJson(address, handler, workCommentList);
    }


    @Override
    public void initViews() {
        // 第一张图
        ivWorkPic = findViewById(R.id.iv_work_wall_content_work_first_pic);

        // 发布者头像
        ivPublisherHead = findViewById(R.id.iv_work_wall_content_work_publisher_icon);

        // 发布者名称
        tvPublisherName = findViewById(R.id.tv_work_wall_content_work_publisher_name);

        // 发布者类别
        tvPublisherTypeText = findViewById(R.id.tv_work_wall_content_type);

        // 发布作品名称
        tvWorkName = findViewById(R.id.tv_work_wall_content_work_name);

        // 关注按钮的点击
        btnClickConcern = findViewById(R.id.btn_work_wall_content_click_concern);

        // 设置喜欢按钮点击 src 图片切换
        llClickLikeIconArea = findViewById(R.id.ll_work_wall_content_click_like_area);
        ivLikeIcon = findViewById(R.id.iv_work_wall_content_like_icon);
        tvLikeNum = findViewById(R.id.tv_work_wall_content_like_num);

        // 设置收藏按钮点击 src 图片切换
        llClickCollectionIconArea = findViewById(R.id.ll_work_wall_content_click_collection_area);
        ivCollectionIcon = findViewById(R.id.iv_work_wall_content_collection_icon);
        tvCollectionNum = findViewById(R.id.tv_work_wall_content_collect_num);

        // 上面的评论数
        tvCommentNumTop = findViewById(R.id.tv_work_wall_content_comment_num_top);

        // RecyclerView【作品图片内容】
        rvContentImg = findViewById(R.id.rv_work_wall_content_image);

        // 作品信息的下拉按钮
        rlShowInfo = findViewById(R.id.rl_work_wall_content_show_work_info);

        ivChangeArrow = findViewById(R.id.iv_work_wall_content_change_arrow);   // 箭头显示图片
        llWorkInfo = findViewById(R.id.ll_work_wall_content_work_info); // 作品信息的布局
        tvWorkTypeInfo = findViewById(R.id.tv_work_wall_content_work_type_info);    // 作品类型
        tvWorkUpTime = findViewById(R.id.tv_work_wall_content_upload_time); // 作品上传时间
        tvCopyrightInfo = findViewById(R.id.tv_work_wall_content_copyright_info);   // 作品产权

        // 底部的评论数
        tvCommentNumBottom = findViewById(R.id.tv_work_wall_content_comment_num_bottom);

        // RecyclerView【评论信息内容】
        rvContentComment = findViewById(R.id.rv_work_wall_content_comment);

        // 评论加载更多内容
        llLoadMore = findViewById(R.id.ll_work_wall_content_comment_click_load_more_area);
        tvShowLoadWord = findViewById(R.id.tv_work_wall_content_show_load_word);

        // 12.发送评论按钮
        btnSendMsg = findViewById(R.id.btn_work_wall_content_send_msg);

        // 13.输入评论内容的EditText
        etInputComment = findViewById(R.id.et_work_wall_content_type_word);
        MyContentUtil.setTextWatcher(this, etInputComment);  // 设置字数限定
    }

    private void initListener() {
        btnClickConcern.setOnClickListener(this);
        llClickLikeIconArea.setOnClickListener(this);
        llClickCollectionIconArea.setOnClickListener(this);
        rlShowInfo.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
        llLoadMore.setOnClickListener(this);
    }


    // 【活动中的点击事件】
    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 作品点赞
        if (id == R.id.ll_work_wall_content_click_like_area) {
            if (hasWorkLike) {
                Toast.makeText(this, "你已经点过赞了", Toast.LENGTH_SHORT).show();
                return;
            }
            String address = HOST + "userHistory/worksWallWorksLike" + ";jsessionid=" + mainBinder.getLocalJSessionId();

            int userId = userInfo.getId();  // 用户id
            int targetId = mWorkInfo.getId();   //作品id

            Map<String, String> map = new HashMap<>();
            map.put("userId", "" + userId);
            map.put("targetId", "" + targetId);

            Log.e(TAG, "WorkLike -- userId:" + userId);
            Log.e(TAG, "WorkLike -- targetId:" + targetId);
            MyContentUtil.sendOptionInfo(address, map, handler, WORK_LIKE);
        }

        // 作品收藏
        if (id == R.id.ll_work_wall_content_click_collection_area) {
            if (hasWorkCollection) {
                Toast.makeText(this, "你已经点收藏过了", Toast.LENGTH_SHORT).show();
                return;
            }

            String address = HOST + "userHistory/worksWallCollect" + ";jsessionid=" + mainBinder.getLocalJSessionId();
            int userId = userInfo.getId();  // 用户id
            int targetId = mWorkInfo.getId();   //作品id

            Map<String, String> map = new HashMap<>();
            map.put("userId", "" + userId);
            map.put("targetId", "" + targetId);

            Log.e(TAG, "WorkCollection -- userId:" + userId);
            Log.e(TAG, "WorkCollection -- targetId:" + targetId);
            MyContentUtil.sendOptionInfo(address, map, handler, WORK_COLLECTION);
        }

        // 发布者关注
        if (id == R.id.btn_work_wall_content_click_concern) {
            int userId = userInfo.getId();
            int targetId = mWorkInfo.getUserId();

            if (hasUserConcerned) {  //如果已经关注，则进行取消关注操作
                // InformationController/delAttention?userId=1&targetId=6
                String address = HOST + "InformationController/delAttention" + ";jsessionid=" + mainBinder.getLocalJSessionId();
                Map<String, String> map = new HashMap<>();
                map.put("userId", "" + userId);
                map.put("targetId", "" + targetId);

                MyContentUtil.sendOptionInfo(address, map, handler, USER_NOT_CONCERNED);
            } else {    // 进行关注操作
                // InformationController/setAttention?userId=1&targetId=6
                String address = HOST + "InformationController/setAttention" + ";jsessionid=" + mainBinder.getLocalJSessionId();
                Map<String, String> map = new HashMap<>();
                map.put("userId", "" + userId);
                map.put("targetId", "" + targetId);

                MyContentUtil.sendOptionInfo(address, map, handler, USER_CONCERNED);
            }
        }

        if (id == R.id.rl_work_wall_content_show_work_info) {
            if (bInfo) {
                // 修改显示的箭头
                ivChangeArrow.setBackgroundResource(R.drawable.bg_work_wall_content_info_down);
                llWorkInfo.setVisibility(View.GONE);
            } else {
                ivChangeArrow.setBackgroundResource(R.drawable.bg_work_wall_content_info_up);
                llWorkInfo.setVisibility(View.VISIBLE);
            }
            bInfo = !bInfo;
        }

        // 加载更多评论
        if (id == R.id.ll_work_wall_content_comment_click_load_more_area) {
            pageNum++;
            tvShowLoadWord.setText("加载中...");
            jsonToWorkCommentList();
        }

        // 发送评论的按钮处理事件
        if (id == R.id.btn_work_wall_content_send_msg) {
            sendComment();
        }
    }

    // 【发送评论】
    private void sendComment() {
        // 1.EditText   2.UserInfo  3.Handler   4.Map
        String discussText = etInputComment.getText().toString();
        if (TextUtils.isEmpty(discussText)) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            userInfo = mainBinder.getUserInfo();
            int parentId = mWorkInfo.getId();   // 作品id
            int userId = userInfo.getId();  // 评论者id
            int discussType = 1;    // 1表示该评论在作品墙中

            Log.e(TAG, "SendCommentUserId" + userId);

            String address = HOST + "comment/insComment" + ";jsessionid=" + mainBinder.getLocalJSessionId();
            Map<String, String> map = new HashMap<>();
            map.put("userId", "" + userId);
            map.put("discussType", "" + discussType);
            map.put("parentId", "" + parentId);
            map.put("discussText", discussText);

            HttpUtil.sendOkHttpPost(address, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-2);   // 网络异常
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int statusCode = response.code();
                    if (statusCode != 200) { // 如果返回不是200，则显示Toast(发送评论失败）
                        handler.sendEmptyMessage(-3);
                        return;
                    }
                    if (response.body() == null) {  // 如果response.body 为空，也显示Toast(发送评论失败)
                        handler.sendEmptyMessage(-3);
                        return;
                    }
                    String responseData = response.body().string();   // 返回的数据
                    Log.e(TAG, "Post请求返回的内容" + responseData); // 打印出返回的内容
                    try {
                        // 第一层解析获得状态码
                        JSONObject jsonObject1 = new JSONObject(responseData);
                        int code = jsonObject1.optInt("code");
                        Log.e(TAG, "获取的Code" + code);
                        if (code == 0) {
                            handler.sendEmptyMessage(1);    // 表示发送评论成功

                            workCommentList.clear();    //调用该方法进行评论的刷新
                            jsonToWorkCommentList();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
