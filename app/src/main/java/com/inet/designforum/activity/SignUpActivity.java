package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final int HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN = 1;

    private Handler handler = new SignUpHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sign_up);
        super.onCreate(savedInstanceState);


    }

    private EditText mPhone;
    private TextInputLayout mPhoneInfo;
    private EditText mPwd;
    private TextInputLayout mPwdInfo;
    private EditText mPwdAgain;
    private TextInputLayout mPwdAgainInfo;
    private EditText mAuthCode;
    private TextInputLayout mAuthCodeInfo;
    private Button mSignUpBtn;
    private Button mGetAuthBtn;
    private TextView mToLogin;

    private LinearLayout mBack;
    private TextView mTitle;

    // 缓存中的验证码
    private String localAuthCode;

    @Override
    public void findViews() {
        mPhone = findViewById(R.id.user_phone);
        mPhoneInfo = findViewById(R.id.user_phone_info);
        mPwd = findViewById(R.id.user_password);
        mPwdInfo = findViewById(R.id.user_password_info);
        mPwdAgain = findViewById(R.id.user_password_again);
        mPwdAgainInfo = findViewById(R.id.user_password_info_again);
        mAuthCode = findViewById(R.id.user_auth_code);
        mAuthCodeInfo = findViewById(R.id.user_auth_code_info);
        mGetAuthBtn = findViewById(R.id.get_auth_code);
        mSignUpBtn = findViewById(R.id.btn_sign_up);
        mToLogin = findViewById(R.id.start_login);

        mBack = findViewById(R.id.ll_toolbar_all_back);
        mTitle = findViewById(R.id.tv_toolbar_all_title);
    }

    @Override
    public void getData() {
        // nothing
    }

    @Override
    public void initViews() {
        mSignUpBtn.setOnClickListener(this);
        mGetAuthBtn.setOnClickListener(this);
        mToLogin.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mTitle.setText("注册");
    }

    @Override
    public void onClick(View v) {
        // 验证不通过就返回，在validation方法中设置InputLayout的Error信息
        switch (v.getId()) {
            case R.id.get_auth_code: {
                if (!validation()) return;
                // 先不准用，不然点好多次
                mGetAuthBtn.setClickable(false);
                // 根据信息获取验证码，按钮变为不可用，然后加个倒计时
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String signUpJSessionId = DesignForumApplication.mainBinder.getJSessionId();
                            if (signUpJSessionId == null) {
                                handler.post(() -> Toast.makeText(SignUpActivity.this, "请求验证码失败", Toast.LENGTH_SHORT).show());
                                return;
                            }
                            String userPhone = mPhone.getText().toString();

                            FormBody body = new FormBody.Builder()
                                    .add("user_phone", userPhone)
                                    .build();

                            Request request = new Request.Builder()
                                    .post(body)
                                    .url(DesignForumApplication.HOST + "VerifyController/phoneVerify;jsessionid=" + signUpJSessionId)
                                    .build();

                            Log.e(TAG, "run: " + DesignForumApplication.HOST + "VerifyController/phoneVerify;jsessionid=" + signUpJSessionId);

                            Response response = DesignForumApplication.client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    String json = response.body().string();
                                    Log.e(TAG, "run: " + json);
                                    JSONObject jsonObj = JSON.parseObject(json);
                                    int code = jsonObj.getInteger("code");
                                    if (code == 0) {
                                        // 成功
                                        localAuthCode = jsonObj.getJSONObject("content").getString("verification_code");
                                        // 按钮变为不可用，然后加个倒计时
                                        handler.post(() -> {
                                            Message msg = handler.obtainMessage();
                                            msg.obj = 60;
                                            msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
                                            handler.sendMessage(msg);
                                            Toast.makeText(SignUpActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
                                        });
                                    } else if (code == 1) {
                                        // 已注册
                                        handler.post(() -> {
                                            Toast.makeText(SignUpActivity.this, "该手机号已注册，请重试", Toast.LENGTH_SHORT).show();
                                            mGetAuthBtn.setClickable(true);
                                        });
                                    } else if (code == 2) {
                                        // 发送失败
                                        handler.post(() -> {
                                            Toast.makeText(SignUpActivity.this, "短信发送失败，请检查手机号是否正确", Toast.LENGTH_SHORT).show();
                                            mGetAuthBtn.setClickable(true);
                                        });
                                    } else {
                                        handler.post(() -> {
                                            Toast.makeText(SignUpActivity.this, "未知异常", Toast.LENGTH_SHORT).show();
                                            mGetAuthBtn.setClickable(true);
                                        });
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                break;
            }
            case R.id.btn_sign_up: {
                if (!validation()) return;
                // 这里多一步验证验证码的是否为空
                String authCode = mAuthCode.getText().toString();
                if (authCode.equals("")) {
                    setError(mAuthCodeInfo, "验证码不可以为空");
                    return;
                }

                if (!authCode.equals(localAuthCode)) {
                    setError(mAuthCodeInfo, "验证码错误");
                    return;
                }

                // 进行注册
                String userPhone = mPhone.getText().toString();
                String userPassword = mPwd.getText().toString();
                String userName = "用户" + (Math.random() * 89999 + 10000); // 先是随机的用户名把
                String userStatus = "1"; // 1=学生 0=老师

                FormBody body = new FormBody.Builder()
                        .add("user_phone", userPhone)
                        .add("user_password", userPassword)
                        .add("user_name", userName)
                        .add("user_status", userStatus)
                        .build();

                Request request = new Request.Builder()
                        .post(body)
                        .url(DesignForumApplication.HOST + "VerifyController/register")
                        .build();

                new Thread(() -> {
                    try {
                        Response response = DesignForumApplication.client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                String json = response.body().string();
                                JSONObject jsonObj = JSON.parseObject(json);
                                int code = jsonObj.getInteger("code");
                                if (code == 0) {
                                    // 成功
                                    JSONObject content = jsonObj.getJSONObject("content");
                                    UserInfo info = new UserInfo();
                                    info.setUser_head(content.getString("user_head"));
                                    info.setHistory_time(content.getString("history_time"));
                                    info.setId(Integer.parseInt(content.getString("id")));
//                                    DesignForumApplication.mainBinder.updateUserInfo(info); 这一步在登陆的时候再用
                                    handler.post(SignUpActivity.this::finish);
                                } else if (code == 1) {
                                    // 手机号已存在
                                    handler.post(() -> {
                                        Toast.makeText(SignUpActivity.this, "手机号已存在", Toast.LENGTH_SHORT).show();
                                    });
                                } else if (code == 2) {
                                    // 用户名已存在
                                    handler.post(() -> {
                                        Toast.makeText(SignUpActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            }
            case R.id.start_login: {
                startActivity(new Intent(this, LoginActivity.class));
                break;
            }
            case R.id.ll_toolbar_all_back: {
                finish();
                handler.removeCallbacksAndMessages(null);// 移除掉所有的信息
                break;
            }
        }
    }

    private static final String TAG = "SignUpActivity";

    private boolean validation() {
        String phone = mPhone.getText().toString();
        if (phone.equals("") || !phone.matches("\\d{11}")) {
            setError(mPhoneInfo, "请输入正确的手机号");
            return false;
        }

        String pwd = mPwd.getText().toString();
        if (pwd.equals("") || pwd.length() < 6) {
            setError(mPwdInfo, "密码不可以为空，长度为6到16位");
            return false;
        }

        String pwdAgain = mPwdAgain.getText().toString();
        if (!pwdAgain.equals(pwd)) {
            setError(mPwdAgainInfo, "两次输入的密码不一致");
            return false;
        }
        return true;
    }

    private void setError(TextInputLayout layout, String msg) {
        layout.setError(msg);
        // 1秒后取消
        layout.postDelayed(() -> layout.setError(""), 1000L);
    }

    static class SignUpHandler extends Handler {
        private WeakReference<Context> contextWeakReference;

        private SignUpHandler(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            SignUpActivity activity = (SignUpActivity) contextWeakReference.get();
            switch (msg.what) {
                case HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN: {
                    int cd = (int) msg.obj;
                    if (cd == 0) {
                        activity.mGetAuthBtn.setClickable(true);
                        activity.mGetAuthBtn.setText("获取验证码");
                    } else if (cd > 0 && cd < 61) {
                        activity.mGetAuthBtn.setText(String.format( "%s秒后可再次发送", cd));
                        msg = obtainMessage();
                        msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
                        msg.obj = cd - 1;
                        sendMessageDelayed(msg, 1000L);
                    }
                    break;
                }
            }
        }
    }
}
