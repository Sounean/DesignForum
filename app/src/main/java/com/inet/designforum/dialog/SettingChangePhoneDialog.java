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
                .setNegativeButton("??????", null)
                .setPositiveButton("??????", null)
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
                // ????????????????????????what=HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN,arg1=cd,obj=?????????btn??????
                int cd = msg.arg1;
                Button btn = (Button) msg.obj;
                if (cd == 0) {
                    btn.setClickable(true);
                    btn.setText("???????????????");
                } else if (cd > 0 && cd < 61) {
                    btn.setText(String.format(Locale.US, "%d?????????????????????", cd));
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
//                    handler.post(() -> Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show());
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    if (response.isSuccessful() && (response.body() != null)) {
//                        JSONObject json = JSONObject.parseObject(response.body().string());
//                        int code = json.getInteger("code");
//                        if (code == 0) {
//                            // ??????
//                            oldAuthCode = json.getJSONObject("content").getString("verification_code");
//                            Message msg = handler.obtainMessage();
//                            msg.arg1 = 60;
//                            msg.obj = mGetOldAuthCodeBtn;
//                            msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
//                            handler.sendMessage(msg);
//                            handler.post(() -> Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show());
//                        }else if (code == 1){
//                            // ?????????
//                            setError(mOldAuthCodeInfo,"");
//                        }else if (code == 2){
//                            // ??????
//
//                        }else {
//                            // ??????
//
//                        }
//                    }
//                }
//            });
        /*} else */if (id == R.id.get_new_auth_code) {
            // ?????????
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
                    handler.post(() -> Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && (response.body() != null)) {
                        JSONObject json = JSONObject.parseObject(response.body().string());
                        int code = json.getInteger("code");
                        if (code == 0) {
                            // ??????
                            oldAuthCode = json.getJSONObject("content").getString("verification_code");
                            Message msg = handler.obtainMessage();
                            msg.arg1 = 60;
                            msg.obj = mGetNewAuthCodeBtn;
                            msg.what = HANDLER_EVENT_AUTH_CODE_BTN_COUNTDOWN;
                            handler.sendMessage(msg);
                            handler.post(() -> Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show());
                        } else if (code == 1) {
                            // ?????????
                            setError(mNewAuthCodeInfo, "?????????????????????");
                        } else if (code == 2) {
                            // ??????
                            setError(mNewAuthCodeInfo, "??????????????????");
                        } else {
                            // ??????
                            setError(mNewAuthCodeInfo, "????????????");
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
            setError(mNewPhoneInfo,"???????????????????????????");
            return false;
        }
        return true;
    }


    private boolean verificationAuthCode(){
        String authCode = mNewAuthCode.getText().toString();
        if (TextUtils.isEmpty(authCode) || !authCode.matches("\\d{4}")){
            setError(mNewPhoneInfo,"???????????????????????????");
            return false;
        }
        return true;
    }

    public static DialogFragment getInstance() {
        return new SettingChangeEmailDialog();
    }
}
