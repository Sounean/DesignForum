package com.chienli.micro_class.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.chienli.micro_class.R;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.util.ActivityManagerUtil;

public class InputCommentDialog extends DialogFragment {

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Activity activity = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class);
//
//        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_input_comment, null, false);
//
//        AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setView(binding.getRoot())
//                .create();
//        return dialog;
//    }

    public static DialogFragment getInstance() {
        return new InputCommentDialog();
    }
}
