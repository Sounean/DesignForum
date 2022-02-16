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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.inet.designforum.R;
import com.inet.designforum.util.DFFragmentHandler;
import com.inet.designforum.util.DFHandlerMassage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SettingChangePasswordDialog extends DialogFragment implements View.OnClickListener, DFHandlerMassage {

    private Handler handler = new DFFragmentHandler<>(this, this);


    private Button mDialogPositiveButton;

    private EditText userOldPassword;
    private TextInputLayout userOldPasswordInfo;
    private EditText userNewPassword;
    private TextInputLayout userNewPasswordInfo;
    private EditText userNewPasswordAgain;
    private TextInputLayout userNewPasswordInfoAgain;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_change_password_dialog, null, false);
        initView(view);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("提交", null)
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

    @Override
    public void onClick(View v) {
        if (!verification()) return;
        String url = DesignForumApplication.HOST + "InformationController/updateUserPaaword;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
        FormBody body = new FormBody.Builder()
                .add("former_password", userOldPassword.getText().toString()) // 旧密码
                .add("user_password", userNewPassword.getText().toString())
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        DesignForumApplication.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> Toast.makeText(getContext(), "网络访问出错", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "onResponse response.body() != null ? " + (response.body() != null));
                Log.e(TAG, "onResponse: " + response.toString());
                if (response.isSuccessful() && (response.body() != null)) {
                    JSONObject json = JSONObject.parseObject(response.body().string());
                    int code = json.getInteger("code");
                    Log.e(TAG, "onResponse code = " + code);
                    if (code == 0) {
                        // 成功，先logout，然后finish
                        DesignForumApplication.mainBinder.logout();
                        getActivity().finish();
                    } else if (code == 1) {
                        // 有误
                        setError(userOldPasswordInfo, "原密码错误");
                    } else {
                        // 未知
                        setError(userNewPasswordInfo, "未知错误");
                    }
                }
            }
        });
    }

    private static final String TAG = "SettingChangePasswordD";

    private boolean verification() {
        String oldPwd = userOldPassword.getText().toString();
        String newPwd = userNewPassword.getText().toString();
        String newPwdAgain = userNewPasswordAgain.getText().toString();
        if (TextUtils.isEmpty(oldPwd) || oldPwd.length() <= 6 || oldPwd.length() >= 16) {
            setError(userOldPasswordInfo, "请输入正确的密码，长度为6到16位");
            return false;
        }
        if (TextUtils.isEmpty(newPwd) || newPwd.length() <= 6 || newPwd.length() >= 16) {
            setError(userNewPasswordInfo, "请输入正确的密码，长度为6到16位");
            return false;
        }
        if (TextUtils.isEmpty(newPwdAgain) || !newPwd.equals(newPwdAgain)) {
            setError(userOldPasswordInfo, "两次输入的密码不一致");
            return false;
        }
        return true;
    }

    @Override
    public void handle(Message message) {

    }

    public static DialogFragment getInstance() {
        return new SettingChangePasswordDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView(View view) {
        userOldPassword = view.findViewById(R.id.user_old_password);
        userOldPasswordInfo = view.findViewById(R.id.user_old_password_info);
        userNewPassword = view.findViewById(R.id.user_new_password);
        userNewPasswordInfo = view.findViewById(R.id.user_new_password_info);
        userNewPasswordAgain = view.findViewById(R.id.user_new_password_again);
        userNewPasswordInfoAgain = view.findViewById(R.id.user_new_password_info_again);
    }
}
