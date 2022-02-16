package com.chienli.micro_class.view_model;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.MicroClassBaseInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;

import java.util.List;

public class ItemMicroClassMainRclViewModel {
    private static final String TAG = "ItemMicroClassMainRclVi";
    private static final RequestOptions OPTIONS = RequestOptions.centerCropTransform().error(R.drawable.ic_error_cloud);

    public ItemMicroClassMainRclViewModel() {
    }

    // 这个布局中的点击事件，通过类名使用
    public static void onItemClick(View view, List<MicroClassBaseInfo> list, int position) {
        Log.e(TAG, "onItemClick");
        Toast.makeText(view.getContext(), position + "", Toast.LENGTH_SHORT).show();
        MicroClassActivity.startMicroClassActivity(view.getContext(), DesignForumApplication.mainBinder.getUserInfo().getId(), list.get(position).getId());
    }

    @BindingAdapter("setImgUrl")
    public static void setImgUrl(ImageView img, String url) {
        Glide.with(img).load(url).apply(OPTIONS).into(img);
    }
}
