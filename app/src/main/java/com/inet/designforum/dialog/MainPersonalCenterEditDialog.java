package com.inet.designforum.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.inet.designforum.activity.MainActivity;
import com.inet.designforum.activity.SettingActivity;
import com.inet.designforum.fragment.SettingFragment;

import java.util.Objects;

public class MainPersonalCenterEditDialog extends DialogFragment {


    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: {
                    // 修改头像
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(intent, MainActivity.GET_IMG);
                    break;
                }
                case 1: {
                    // 修改个人资料
                    SettingActivity.startActivity(getContext(), SettingFragment.PERSONAL_INFO);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setItems(new String[]{"修改头像", "修改个人资料"}, listener);
        return builder.create();
    }

    public static DialogFragment getInstance() {
        return new MainPersonalCenterEditDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(getClass().getSimpleName(), data.toURI());
        if (requestCode == 222) {
            Uri imageUri = data.getData();
            Toast.makeText(getContext(), imageUri.getEncodedPath(), Toast.LENGTH_SHORT).show();
        }
    }
}
