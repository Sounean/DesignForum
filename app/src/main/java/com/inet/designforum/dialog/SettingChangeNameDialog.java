package com.inet.designforum.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.fragment.SettingFragment;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SettingChangeNameDialog extends DialogFragment implements View.OnClickListener {

    private Handler handler = new SettingHandler(this);
    private TextInputLayout mNameEditInfo;
    private EditText mNameEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_setting_change_name_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .create();
        mNameEdit = view.findViewById(R.id.name_edit);
        mNameEditInfo = view.findViewById(R.id.name_edit_info);
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;
    }

    public static DialogFragment getInstance() {
        return new SettingChangeNameDialog();
    }

    private static final String TAG = "SettingChangeNameDialog";

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: ");

        String name = mNameEdit.getText().toString();
        if ("".equals(name) || name.length() < 3) {
            handler.post(() -> mNameEditInfo.setError("不可以为空，或者长度小于3"));
            handler.postDelayed(() -> mNameEditInfo.setError(""), 1000L);
            return;
        }
        String url = DesignForumApplication.HOST + "/InformationController/updateUserName;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
        FormBody body = new FormBody.Builder()
                .add("user_name", name)
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        DesignForumApplication.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onClick: Fail");
                handler.post(() -> Toast.makeText(getContext(), "网络访问失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "onResponse: 1");
                    String json = response.body().string();
                    int code = JSON.parseObject(json).getInteger("code");
                    Log.e(TAG, "onResponse: code = " + code);
                    if (code == 0) {
                        Log.e(TAG, "onResponse: 2");
                        handler.post(() -> {
                            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                            info.setUser_name(name);
                            DesignForumApplication.mainBinder.updateUserInfo(info);
                            SettingChangeNameDialog.this.dismiss();
                            if (SettingChangeNameDialog.this.getParentFragment() != null) {
                                ((SettingFragment) SettingChangeNameDialog.this.getParentFragment()).update();
                            }
                        });
                    } else {
                        Log.e(TAG, "onResponse: 3");
                        handler.post(() -> mNameEditInfo.setError("该用户名已存在"));
                        handler.postDelayed(() -> mNameEditInfo.setError(""), 1000L);
                    }
                } else {
                    handler.post(() -> mNameEditInfo.setError("网络访问出错"));
                    handler.postDelayed(() -> mNameEditInfo.setError(""), 1000L);
                }
            }
        });
    }

    private static class SettingHandler extends Handler {
        private WeakReference<DialogFragment> mDialog;

        private SettingHandler(DialogFragment dialog) {
            mDialog = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
}
