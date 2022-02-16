package com.inet.designforum.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.inet.designforum.R;
import com.inet.designforum.fragment.base.BaseFragment;
import com.inet.designforum.util.DFFragmentHandler;
import com.inet.designforum.util.UriPathUtil;
import com.inet.designforum.util.Util;
import com.sendtion.xrichtext.RichTextEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainUploadFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_IMG = 201;
    private RichTextEditor editor; // 一般内容
    private LinearLayout ok;// 添加图片的布局
    private RichTextEditor title;
    private Button upload;

    private Handler handler = new DFFragmentHandler<>(this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_upload, container, false);
        init(view);
        return view;
    }

    @Override
    public void findViews(View view) {
        editor = view.findViewById(R.id.et_main_upload);
        ok = view.findViewById(R.id.ll_toolbar_all_add);
        title = view.findViewById(R.id.et_title_main_upload);
        upload = view.findViewById(R.id.btn_main_upload);
    }

    @Override
    public void getData(View view) {

    }

    @Override
    public void initViews(View view) {
        ok.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    private static final String TAG = "MainUploadFragment";

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_toolbar_all_add) {
            pickImage();
            Log.e(TAG, "onClick ok");
        } else if (id == R.id.btn_main_upload) {
            if (DesignForumApplication.mainBinder.isLogin()) {
                postData();
            } else {
                Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMG) {
            // 获取图片
            if (data == null) {
                // 说明也是取消了
                return;
            }
            Uri imageUri = data.getData();
            if (imageUri == null) {
                // 说明是取消了
                return;
            }
            Log.e(TAG, UriPathUtil.getRealPathFromUri(getActivity(), imageUri));
            editor.addImageViewAtIndex(editor.getLastIndex(), UriPathUtil.getRealPathFromUri(getActivity(), imageUri));
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMG);
    }

    private void postData() {
        // https://www.cyhfwq.top/designForum/work/addWork
        upload.setClickable(false);
        new Thread(() -> {
            try {
//                String url = DesignForumApplication.HOST + "work/addWorkPhone;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                String url = DesignForumApplication.HOST + "work/addWorkPhone";
                List<RichTextEditor.EditData> dataList = editor.buildEditData();
                String content = dataList.get(0).inputStr;
                List<String> imgList = new ArrayList<>();
                FormBody.Builder build = new FormBody.Builder()
                        .add("workType", "1")
                        .add("workLabels", "1")
//                        .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                        .add("workText", content.equals("") ? "这个人没有留下内容" : content);
                StringBuilder sb = new StringBuilder();
                for (RichTextEditor.EditData data : dataList) {
                    if (data.imagePath != null) {
                        String s = Util.bitmapToStr(Glide.with(this).asBitmap().load(data.imagePath).submit().get());
                        build.add("imgFile", s);
                        Log.e(TAG, "postData: " + s);
                    }
                }


                Request request = new Request.Builder()
                        .url(url)
                        .post(build.build())
                        .build();

                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show());
                        handler.post(() -> upload.setClickable(true));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            Log.e(TAG, "onResponse: " + response);
                            if (response.isSuccessful() && (response.body() != null)) {
                                JSONObject json = JSONObject.parseObject(response.body().string());
                                int code = json.getInteger("code");
                                Log.e(TAG, "onResponse: code = " + code);
                                Log.e(TAG, "onResponse: json = " + json.toJSONString());
                                if (code == 0) {
                                    handler.post(() -> {
                                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                        editor.clearAllLayout();
                                    });

                                } else {
                                    handler.post(() -> Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show());
                                    Log.e(TAG, "onResponse: json code = " + code);
                                }
                            } else {
                                Log.e(TAG, "onResponse: " + response);
                                handler.post(() -> Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show());
                            }
                            handler.post(() -> upload.setClickable(true));
                        } catch (JSONException e) {
                            handler.post(() -> Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show());
                            handler.post(() -> upload.setClickable(true));
                            Log.e(TAG, "onResponse: ", e);
                        }
                    }
                });
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
