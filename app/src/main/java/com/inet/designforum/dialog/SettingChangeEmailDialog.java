package com.inet.designforum.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.fragment.SettingFragment;
import com.inet.designforum.util.DFFragmentHandler;
import com.inet.designforum.util.DFHandlerMassage;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SettingChangeEmailDialog extends DialogFragment implements View.OnClickListener, DFHandlerMassage {


    private EditText mNewEmail;
    private TextInputLayout mNewEmailInfo;
    private Button mGetAuthBtn;
    private EditText mAuthCode;
    private TextInputLayout mAuthCodeInfo;

    private Handler handler = new DFFragmentHandler<>(this,this);

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_change_email_dialog, null, false);

        mNewEmail = view.findViewById(R.id.new_email);
        mNewEmailInfo = view.findViewById(R.id.new_email_info);
        mGetAuthBtn = view.findViewById(R.id.get_auth_code);
        mAuthCode = view.findViewById(R.id.auth_code);
        mAuthCodeInfo = view.findViewById(R.id.auth_code_info);

        mGetAuthBtn.setOnClickListener(this);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", null)
                .setCancelable(false)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    private static final String emailRegex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
    private String authCode;
    private String newEmail;

    private static String authEmailUrl = DesignForumApplication.HOST + "/designForum/VerifyController/mailVerify;jsessionid=";
    private static String updateEmailUrl = DesignForumApplication.HOST + "/designForum/VerifyController/updateUserEmail;jsessionid=";

    @Override
    public void onClick(View v) {
        // 确认的点击事件，再此之前除非调用相关方法，否则不会关闭对话框
        mGetAuthBtn.setClickable(false);
        int id = v.getId();
        if (id == R.id.get_auth_code) {
            newEmail = mNewEmail.getText().toString();
            if (newEmail.matches(emailRegex)) {
                // 符合条件
                String url = authEmailUrl + DesignForumApplication.mainBinder.getLocalJSessionId();
                FormBody body = new FormBody.Builder()
                        .add("user_email", newEmail)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handler.post(() -> Toast.makeText(getContext(), "网络访问出错", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && (response.body() != null)) {
                            JSONObject json = JSONObject.parseObject(response.body().string());
                            int code = json.getInteger("code");
                            if (code == 0) {
                                authCode = json.getJSONObject("content").getString("verification_code");
                                Message msg = handler.obtainMessage();
                                msg.arg1 = 60;
                                msg.obj = mGetAuthBtn;
                                msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
                                handler.sendMessage(msg);
                            } else if (code == 1) {
                                // 已注册
                                handler.post(() -> {
                                    mNewEmailInfo.setError("该邮箱地址已注册");
                                    mGetAuthBtn.setClickable(true);
                                });
                                handler.postDelayed(() -> {
                                    mNewEmailInfo.setError("");
                                    mGetAuthBtn.setClickable(true);
                                }, 2000L);
                            } else if (code == 2) {
                                // 不支持
                                handler.post(() -> {
                                    mNewEmailInfo.setError("不支持该邮箱地址");
                                    mGetAuthBtn.setClickable(true);
                                });

                                handler.postDelayed(() -> {
                                    mNewEmailInfo.setError("");
                                }, 2000L);
                            } else {
                                // 未知
                                handler.post(() -> {
                                    mNewEmailInfo.setError("未知错误");
                                    mGetAuthBtn.setClickable(true);
                                });

                                handler.postDelayed(() -> {
                                    mNewEmailInfo.setError("");
                                    mGetAuthBtn.setClickable(true);
                                }, 2000L);
                            }
                        }
                    }
                });

            } else {
                mNewEmailInfo.setError("请输入正确的邮箱地址");
                handler.postDelayed(() -> mNewEmailInfo.setError(""), 2000L);
            }
        } else {
            if (authCode == null || "".equals(authCode)) {
                mAuthCodeInfo.setError("请先获取验证码");
                handler.postDelayed(() -> mAuthCodeInfo.setError(""), 2000L);
            } else {
                String userAuthCode = mAuthCode.getText().toString();
                if (!userAuthCode.equals("")) {
                    if (userAuthCode.equals(authCode)) {

                        String url = updateEmailUrl + DesignForumApplication.mainBinder.getLocalJSessionId();
                        FormBody body = new FormBody.Builder()
                                .add("user_email", newEmail)
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                handler.post(() -> Toast.makeText(getContext(), "网络访问出错", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful() && response.body() != null) {
                                    JSONObject json = JSONObject.parseObject(response.body().string());
                                    int code = json.getInteger("code");
                                    if (code == 0) {
                                        UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                                        info.setUser_email(newEmail);
                                        DesignForumApplication.mainBinder.updateUserInfo(info);
                                        handler.post(() -> Toast.makeText(getActivity(), "邮箱修改成功", Toast.LENGTH_SHORT).show());
                                        if (SettingChangeEmailDialog.this.getParentFragment() != null) {
                                            ((SettingFragment) SettingChangeEmailDialog.this.getParentFragment()).update();
                                        }
                                    }else {
                                        handler.post(() -> Toast.makeText(getActivity(), "邮箱修改失败", Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }
                        });


                    }
                } else {
                    mAuthCodeInfo.setError("请先输入验证码");
                    handler.postDelayed(() -> mAuthCodeInfo.setError(""), 2000L);
                }
            }
        }
    }

    private static final int HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN = 1;

    @Override
    public void handle(Message msg) {
        switch (msg.what) {
            case HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN: {
                int cd = msg.arg1;
                Button btn = (Button) msg.obj;
                if (cd == 0) {
                    btn.setClickable(true);
                    btn.setText("获取验证码");
                } else if (cd > 0 && cd < 61) {
                    btn.setText(String.format(Locale.US, "%d秒后可再次发送", cd));
                    msg.obj = btn;
                    msg.arg1 = cd - 1;
                    handler.sendMessageDelayed(msg, 1000L);
                }
                break;
            }
        }
    }

    public static DialogFragment getInstance() {
        return new SettingChangeEmailDialog();
    }

}
