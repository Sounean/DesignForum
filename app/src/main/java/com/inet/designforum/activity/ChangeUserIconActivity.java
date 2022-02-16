package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;
import com.inet.designforum.util.DFContextHandler;
import com.inet.designforum.util.Util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 修改Icon所使用的Activity，接收返回值与返回数据，读取图片，按完成按钮后就提交图片数据并上传给服务器
 * 并提示是否成功
 */
public class ChangeUserIconActivity extends BaseActivity implements View.OnClickListener {
    private Handler handler = new DFContextHandler<>(this);

    private ImageView img;
    private LinearLayout ok;
    private LinearLayout back;

    private Uri imgUri;

    private static String uploadImgUrl = DesignForumApplication.HOST + "InformationController/setUserHeadPhone;jsessionid=";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_user_icon);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void findViews() {
        img = findViewById(R.id.img);
        ok = findViewById(R.id.ll_toolbar_all_ok);
        back = findViewById(R.id.ll_toolbar_all_back);

        ok.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void getData() {
        imgUri = getIntent().getParcelableExtra(IMG_URI);
    }

    @Override
    public void initViews() {
        Glide.with(img).load(imgUri).into(img);
    }

    private static final String IMG_URI = "imgUri";

    /**
     * 开启修改用户头像上传界面的界面，先展示一下选中的头像，然后点"完成"上传
     *
     * @param context
     * @param imgUri
     */
    public static void startActivity(Context context, Uri imgUri /*User Account*/) {
        Intent intent = new Intent(context, ChangeUserIconActivity.class);
        intent.putExtra(IMG_URI, imgUri);
        context.startActivity(intent);
    }

    private static final String TAG = "ChangeUserIconActivity";

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_toolbar_all_ok) {
            if (imgUri != null) {
                new Thread(() -> {
                    try {
                        String url = uploadImgUrl + DesignForumApplication.mainBinder.getLocalJSessionId();
                        Bitmap bitmap = Glide.with(this).asBitmap().load(imgUri).submit().get();
                        FormBody body = new FormBody.Builder()
                                .add("headImgBase64", Util.bitmapToStr(bitmap))
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                handler.post(() -> Toast.makeText(ChangeUserIconActivity.this, "修改失败", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful() && response.body() != null) {
                                    String json = response.body().string();
                                    int code = JSON.parseObject(json).getInteger("code");
                                    Log.e(TAG, "onResponse: code = " + code);
                                    if (code == 0) {
                                        handler.post(() -> Toast.makeText(ChangeUserIconActivity.this, "修改成功", Toast.LENGTH_SHORT).show());
                                        UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                                        info.setUser_head(JSONObject.parseObject(json).getJSONObject("content").getString("user_head"));
                                        DesignForumApplication.mainBinder.updateUserInfo(info);
                                    } else {
                                        handler.post(() -> Toast.makeText(ChangeUserIconActivity.this, "修改失败", Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }
                        });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                return;
            }
            Toast.makeText(this, "图片不可用", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ll_toolbar_all_back) {
            // 返回按钮
            finish();
        }

    }
}
