package com.inet.designforum.workwall.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.util.DisplayUtil;

public class WorkWallContentImageShowActivity extends AppCompatActivity implements View.OnClickListener {

    private String imgUrl;
    private ImageView ivShowPic;
    private TextView tvShowBetter;
    private static final String TAG = "【ImgContent】";
    private RelativeLayout rlControlActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_wall_content_image_show);

        getIntentData();    //获取图片链接
        initView();     //初始化视图
        initListener(); //设置监听

        ivShowPic.setMaxWidth(DisplayUtil.getDisplayWidth(this));

        // 加载普通画质图片
        loadNormalPic();
    }

    private void initListener() {
        rlControlActivity.setOnClickListener(this);
        tvShowBetter.setOnClickListener(this::onClick);
    }

    private void loadNormalPic() {
        Glide.with(this).load(imgUrl).into(ivShowPic);
    }

    private void loadBetterPic() {
        StringBuffer buf = new StringBuffer(imgUrl);
        buf.insert(buf.length() - 4, "_compress");
        String betterImgUrl = buf.toString();
        Log.e(TAG, "betterImgUrl:" + betterImgUrl);

        RequestOptions options = new RequestOptions();
        RequestOptions placeholder = options.placeholder(R.drawable.ic_work_wall_content_pre_load)
                .error(R.drawable.ic_work_wall_content_error);
        Glide.with(this).load(betterImgUrl).apply(placeholder).into(ivShowPic);
    }

    private void initView() {
        rlControlActivity = findViewById(R.id.rl_work_wall_content_image_show);
        ivShowPic = findViewById(R.id.iv_work_wall_content_image_show_img);
        tvShowBetter = findViewById(R.id.tv_work_wall_content_image_show_watch_better_img);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra("imgUrl");
    }

    public static void actionStart(Context context, String imgUrl) {
        Intent intent = new Intent(context, WorkWallContentImageShowActivity.class);
        intent.putExtra("imgUrl", imgUrl);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_work_wall_content_image_show_watch_better_img) {
            // 加载更好的图片
            loadBetterPic();
            tvShowBetter.setVisibility(View.GONE);
        }
        if (id == R.id.rl_work_wall_content_image_show) {
            finish();   //关闭预览图片活动
        }
    }
}