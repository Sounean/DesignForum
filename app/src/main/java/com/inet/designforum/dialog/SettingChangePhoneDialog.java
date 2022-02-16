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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.inet.designforum.R;
import com.inet.designforum.util.DFFragmentHandler;
import com.inet.designforum.util.DFHandlerMassage;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SettingChangePhoneDialog extends DialogFragment implements View.OnClickListener, DFHandlerMassage {


    private Handler handler = new DFFragmentHandler<>(this, this);

    //    private EditText mOldAuthCode;
//    private TextInputLayout mOldAuthCodeInfo;
//    private Button mGetOldAuthCodeBtn;
    private EditText mNewPhone;
    private TextInputLayout mNewPhoneInfo;
    private EditText mNewAuthCode;
    private TextInputLayout mNewAuthCodeInfo;
    private Button mGetNewAuthCodeBtn;

    private Button mDialogPositiveButton;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_change_phone_dialog, null, false);

//        mOldAuthCode = view.findViewById(R.id.old_auth_code);
//        mOldAuthCodeInfo = view.findViewById(R.id.old_auth_code_info);
//        mGetOldAuthCodeBtn = view.findViewById(R.id.get_old_auth_code);
        mNewPhone = view.findViewById(R.id.new_phone);
        mNewPhoneInfo = view.findViewById(R.id.new_phone_info);
        mNewAuthCode = view.findViewById(R.id.new_auth_code);
        mNewAuthCodeInfo = view.findViewById(R.id.new_auth_code_info);
        mGetNewAuthCodeBtn = view.findViewById(R.id.get_new_auth_code);

//        mGetOldAuthCodeBtn.setOnClickListener(this);
        mGetNewAuthCodeBtn.setOnClickListener(this);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", null)
                .setCancelable(false)
                .create();

        dialog.show();
        mDialogPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mDialogPositiveButton.setOnClickListener(this);
        return dialog;
    }

    private void setError(TextInputLayout layout, String msg) {
        layout.setError(msg);
        layout.postDelayed(() -> layout.setError(""), 2000L);
    }

    private static final int HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN = 1;

    @Override
    public void handle(Message msg) {
        switch (msg.what) {
            case HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN: {
                // 该分支只需要指定what=HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN,arg1=cd,obj=对应的btn即可
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

    private static String authPhoneUrl = DesignForumApplication.HOST + "/VerifyController/phoneVerify;jsessionid=";

    private String oldAuthCode;
    private String newAuthCode;

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.get_old_auth_code) {
//            mGetOldAuthCodeBtn.setClickable(false);
//
//            String url = authPhoneUrl + DesignForumApplication.mainBinder.getLocalJSessionId();
//            FormBody body = new FormBody.Builder()
//                    .add("user_phone", DesignForumApplication.mainBinder.getUserInfo().getAccount())
//                    .build();
//            Request req = new Request.Builder()
//                    .post(body)
//                    .url(url)
//                    .build();
//
//            DesignForumApplication.client.newCall(req).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    handler.post(() -> Toast.makeText(getContext(), "网络访问出错", Toast.LENGTH_SHORT).show());
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    if (response.isSuccessful() && (response.body() != null)) {
//                        JSONObject json = JSONObject.parseObject(response.body().string());
//                        int code = json.getInteger("code");
//                        if (code == 0) {
//                            // 成功
//                            oldAuthCode = json.getJSONObject("content").getString("verification_code");
//                            Message msg = handler.obtainMessage();
//                            msg.arg1 = 60;
//                            msg.obj = mGetOldAuthCodeBtn;
//                            msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
//                            handler.sendMessage(msg);
//                            handler.post(() -> Toast.makeText(getContext(), "信息已发送", Toast.LENGTH_SHORT).show());
//                        }else if (code == 1){
//                            // 已注册
//                            setError(mOldAuthCodeInfo,"");
//                        }else if (code == 2){
//                            // 失败
//
//                        }else {
//                            // 未知
//
//                        }
//                    }
//                }
//            });
        /*} else */if (id == R.id.get_new_auth_code) {
            // 先验证
            if (!verificationPhone()) return;
            mGetNewAuthCodeBtn.setClickable(false);
            String url = authPhoneUrl + DesignForumApplication.mainBinder.getLocalJSessionId();
            FormBody body = new FormBody.Builder()
                    .add("user_phone", DesignForumApplication.mainBinder.getUserInfo().getAccount())
                    .build();
            Request req = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();

            DesignForumApplication.client.newCall(req).enqueue(new Callback() {
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
                            // 成功
                            oldAuthCode = json.getJSONObject("content").getString("verification_code");
                            Message msg = handler.obtainMessage();
                            msg.arg1 = 60;
                            msg.obj = mGetNewAuthCodeBtn;
                            msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
                            handler.sendMessage(msg);
                            handler.post(() -> Toast.makeText(getContext(), "信息已发送", Toast.LENGTH_SHORT).show());
                        } else if (code == 1) {
                            // 已注册
                            setError(mNewAuthCodeInfo, "该账号已被注册");
                        } else if (code == 2) {
                            // 失败
                            setError(mNewAuthCodeInfo, "信息发送失败");
                        } else {
                            // 未知
                            setError(mNewAuthCodeInfo, "未知错误");
                        }
                    }
                }
            });
        } else if (id == mDialogPositiveButton.getId()) {
            if (!verificationAuthCode()) return;

        }
    }

    private boolean verificationPhone(){
        String phone = mNewPhone.getText().toString();
        if (TextUtils.isEmpty(phone) || !phone.matches("\\d{11}")){
            setError(mNewPhoneInfo,"请输入正确的手机号");
            return false;
        }
        return true;
    }


    private boolean verificationAuthCode(){
        String authCode = mNewAuthCode.getText().toString();
        if (TextUtils.isEmpty(authCode) || !authCode.matches("\\d{4}")){
            setError(mNewPhoneInfo,"请输入正确的验证码");
            return false;
        }
        return true;
    }

    public static DialogFragment getInstance() {
        return new SettingChangeEmailDialog();
    }
}
