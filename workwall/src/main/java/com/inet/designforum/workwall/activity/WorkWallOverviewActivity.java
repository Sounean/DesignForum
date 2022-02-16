package com.inet.designforum.workwall.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.service.MainServices;
import com.google.gson.Gson;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.activity.base.BaseActivity;
import com.inet.designforum.workwall.adapter.WorkWallOverviewItemAdapter;
import com.inet.designforum.workwall.bean.WorkInfo;
import com.inet.designforum.workwall.util.MyOverviewUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkWallOverviewActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "【WorkWallOverview】";

    // 存储从json转换后的WorkInfo对象信息
    private List<WorkInfo> workInfoList = new ArrayList<>();
    private List<Integer> idList = new ArrayList<>();
    private WorkWallOverviewItemAdapter adapter;

    // 存储 DesignForumApplication 中的常量
    private static MainServices.MainBinder mainBinder;
    public static String HOST; // 请求域名开头

    // RecyclerView 中存放的预览项
    private RecyclerView rvPreview;
    private EditText etInput;

    // 得到Json信息，对其进行操作
    private Handler handlerShowWork = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (srlRefresh.isRefreshing()) {    // 若正在刷新则将其关闭
                srlRefresh.setRefreshing(false);
            }
            switch (msg.what) {
                case -2: // OkHttp未获取成功，显示网络异常
                    pageNum--;
                    Toast.makeText(WorkWallOverviewActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    tvShowLoadWord.setText("加载更多");
                    break;
                case -1: // 用于显示服务器正忙
                    pageNum--;
                    Toast.makeText(WorkWallOverviewActivity.this, "服务器正忙", Toast.LENGTH_SHORT).show();
                    tvShowLoadWord.setText("加载更多");
                    break;
                case 0: // 0表示数据正常获取
                    adapter.setmWorkInfoList(workInfoList);
                    adapter.notifyDataSetChanged();
                    break;
                case 30:    // 获取模糊查询得到的idList
                    if (idList.size() == 0) {    // 表示模糊查询得到的内容为空
                        workInfoList.clear();   //清空现有的显示内容
                        adapter.setmWorkInfoList(workInfoList);
                        adapter.notifyDataSetChanged();
                        tvShowLoadWord.setText("没有查找到内容");
                        Toast.makeText(WorkWallOverviewActivity.this, "没有你所查找的内容哦", Toast.LENGTH_LONG).show();
                    } else {
                        workInfoList.clear();    //清空现在的workInfoList
                        // https://www.cyhfwq.top/designForum/work/getWorkInId?id_str=45
                        String str = HOST + "work/getWorkInId"
                                + ";jsessionid=" + mainBinder.getLocalJSessionId()
                                + "?id_str=";
                        String address;
                        for (int i = 0; i < idList.size(); i++) {
                            address = str + idList.get(i);
                            // 进行网络请求
                            MyOverviewUtil.searchWorkInfoById(address, handlerShowWork, workInfoList);
                        }
                    }
                    break;
                case 31:    // 通过idList查询得到各个作品，将其进行显示
                    // 这里是一个一个显示的
                    Log.e(TAG, "workInfoListSize");
                    adapter.setmWorkInfoList(workInfoList);
                    adapter.notifyDataSetChanged();
                    tvShowLoadWord.setText("没有更多内容了");
                    llLoadMore.setEnabled(false);
                    break;
                case 10:
                    tvShowLoadWord.setText("加载更多");
                    llLoadMore.setEnabled(true);
                    break;
                case 20:
                    tvShowLoadWord.setText("没有更多内容了");
                    llLoadMore.setEnabled(false);
                    break;
            }
            return false;
        }
    });
    private SwipeRefreshLayout srlRefresh;
    private LinearLayout llLoadMore;
    private TextView tvShowLoadWord;
    private int pageNum = 1; //初始化pageNum为1

    private void getApplicationData() {
        try {
            Thread.sleep(200);  //确保获取到数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainBinder = DesignForumApplication.mainBinder;
        HOST = DesignForumApplication.HOST;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_work_wall_overview);
        // 获得主应用中的数据
        getApplicationData();
        super.onCreate(savedInstanceState);

        initListener();

        // 1.对下拉刷新布局(srlRefresh)进行设置
        setSrlRefresh();

        // 2.对搜索框（etInput）设置监听器
        setEtInputListener();

        // 3.对RecyclerView设置Adapter
        setRvPreViewAdapter();
    }

    private void setSrlRefresh() {
        srlRefresh.setColorSchemeResources(
                R.color.colorSalmon
        );
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 清空原先的workInfoList，并重新获取前10条数据
                pageNum = 1;    //将页码变成1
                workInfoList.clear();
                jsonToWorkInfoList();
            }
        });
    }


    private void setRvPreViewAdapter() {
        adapter = new WorkWallOverviewItemAdapter(this, workInfoList);
        // 接口回调
        adapter.setOnItemClickListener(new WorkWallOverviewItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 点击选中项，启动WorkWallContentActivity
                WorkInfo workInfo = workInfoList.get(position);
                //使用Gson将指定的对象转成json字符串，再进行传输
                Gson gson = new Gson();
                String json = gson.toJson(workInfo);
                WorkWallContentActivity.actionStart(WorkWallOverviewActivity.this, json);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvPreview.setLayoutManager(manager);
        rvPreview.setAdapter(adapter);
    }


    // 1.对输入框（etInput）设置监听器
    private void setEtInputListener() {
        // 搜索框的回车事件监听
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String work_text = etInput.getText().toString();

                    String address = HOST + "work/getSeekWork" + ";jsessionid=" + mainBinder.getLocalJSessionId()
                            + "?work_text=" + work_text + "&work_type=1";
                    MyOverviewUtil.searchWorkIdFromKey(address, handlerShowWork, idList);
                    return false;
                }
                return false;
            }
        });
    }


    // 联网得到作品的json数据，将其转换成WorkInfo并存储到WorkInfoList中
    private void jsonToWorkInfoList() {
        // 获取json数据，不传其他参数时获取的是第1页内容，10条信息
        /**
         *  可传入的参数
         *  1.page_num 获取当前页码的信息
         *  2.page_size 获取该页显示的条数
         *  3.show_page_num 连续显示的页码
         *  4.label_type 获取分类
         */
        String address = HOST + "work/getWorkList;jsessionid=" + mainBinder.getLocalJSessionId() + "?work_type=1&page_num=" + pageNum;
        MyOverviewUtil.dealWorkInfoJson(address, handlerShowWork, workInfoList);
    }

    @Override
    public void findViews() {
    }

    /**
     * 获取json数据，并解析成WorkInfo，存储在 workInfoList中
     */
    @Override
    public void getData() {
        // 联网得到作品的json数据，将其转换成WorkInfo并存储到WorkInfoList中
        jsonToWorkInfoList();
    }


    @Override
    public void initViews() {
        rvPreview = findViewById(R.id.rv_work_wall_overview_preview);
        etInput = findViewById(R.id.et_work_wall_overview_input);
        srlRefresh = findViewById(R.id.srl_work_wall_overview_fresh_work_info);
        llLoadMore = findViewById(R.id.ll_work_wall_overview_click_load_more_area);
        tvShowLoadWord = findViewById(R.id.tv_work_wall_overview_show_load_word);
    }

    private void initListener() {
        llLoadMore.setOnClickListener(this);
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_work_wall_overview_click_load_more_area) {
            // 进行加载更多操作
            tvShowLoadWord.setText("加载中...");
            pageNum++;
            jsonToWorkInfoList();
        }
    }
}
