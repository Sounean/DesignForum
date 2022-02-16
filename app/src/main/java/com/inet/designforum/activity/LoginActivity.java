package com.inet.designforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.activity.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * 登陆使用的Activity
 * 注意密码长度不小于6位，不超过15位
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button btnLogin;

    private Handler handler = new LoginHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        TextView title = findViewById(R.id.tv_toolbar_all_title);
        title.setText("登录");

        // 返回按钮
        LinearLayout back = findViewById(R.id.ll_toolbar_all_back);
        back.setOnClickListener(v -> finish());

        //跳转到注册页
        TextView startSignUp = findViewById(R.id.start_sign_up);
        startSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

    }

    private EditText mPhone;
    private TextInputLayout mPhoneInfo;
    private EditText mPwd;
    private TextInputLayout mPwdInfo;

    @Override
    public void findViews() {
        btnLogin = findViewById(R.id.btn_login);
        mPhone = findViewById(R.id.user_account);
        mPhoneInfo = findViewById(R.id.user_account_info);
        mPwd = findViewById(R.id.user_password);
        mPwdInfo = findViewById(R.id.user_password_info);
    }

    @Override
    public void getData() {

    }

    @Override
    public void initViews() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            // do....
            if (!validation()) return;
            new Thread(() -> {
                String userPhone = mPhone.getText().toString();
                String userPassword = mPwd.getText().toString();

                UserInfo info = new UserInfo();
                info.setAccount(userPhone);
                info.setPassword(userPassword);
                int code = DesignForumApplication.mainBinder.login(info);
                if (code == 0) {
                    // 成功
                    handler.post(() -> {
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        UserInfo update = DesignForumApplication.mainBinder.getUserInfo();
                        update.setAccount(userPhone);
                        DesignForumApplication.mainBinder.updateUserInfo(update); // 在此保存手机号的信息，因为数据接口中并没有相关的方法
                        finish();
                    });
                } else if (code == 1) {
                    handler.post(() -> {
                        Toast.makeText(LoginActivity.this, "账号不正确或密码错误", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private void setError(TextInputLayout layout, String msg) {
        layout.setError(msg);
        // 1秒后取消
        layout.postDelayed(() -> layout.setError(""), 1000L);
    }

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
        return true;
    }

    static class LoginHandler extends Handler {
        private WeakReference<Context> contextWeakReference;

        private LoginHandler(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
        }
    }
}
