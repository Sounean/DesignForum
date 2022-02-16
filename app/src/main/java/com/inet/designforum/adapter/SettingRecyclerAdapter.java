package com.inet.designforum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.SettingConfig;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.adapter.recycler_click_interface.DFRecyclerViewClickableAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static com.inet.designforum.fragment.SettingFragment.ACCOUNT_MANAGER;
import static com.inet.designforum.fragment.SettingFragment.MAIN_SETTING;
import static com.inet.designforum.fragment.SettingFragment.PERSONAL_INFO;

public class SettingRecyclerAdapter extends DFRecyclerViewClickableAdapter<SettingRecyclerAdapter.Holder> {

    private static final String TAG = "SettingRecyclerAdapter";

    private String type;
    private SettingConfig config;
    private Context mContext;

    public SettingRecyclerAdapter(Context context, OnItemClickListener listener, String type) {
        super(listener);
        this.type = type;
        this.mContext = context;
        readConfig(); // 初始化config对象

        // 初始化相关的View
        switch (type) {
            case MAIN_SETTING: {
                MAIN_SETTING_VIEWS = new View[MAIN_SETTING_ITEMS.length];
                ImageView iv1 = new ImageView(mContext);
                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
                MAIN_SETTING_VIEWS[0] = iv1;

                SwitchCompat msgSwitch = new SwitchCompat(mContext);
                msgSwitch.setChecked(config.isMessageNotification());
                msgSwitch.setClickable(false);
                msgSwitch.setFocusable(false);
                MAIN_SETTING_VIEWS[1] = msgSwitch;

                SwitchCompat netSwitch = new SwitchCompat(mContext);
                netSwitch.setChecked(config.isMobileNetworkLoadsImage());
                netSwitch.setClickable(false);
                netSwitch.setFocusable(false);
                MAIN_SETTING_VIEWS[2] = netSwitch;

                ImageView iv2 = new ImageView(mContext);
                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
                MAIN_SETTING_VIEWS[3] = iv2;

                msgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.e(TAG, "SettingRecyclerAdapter: msgSwitch - " + isChecked);
                    config.setMessageNotification(isChecked);
                    saveConfig();
                });
                netSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.e(TAG, "SettingRecyclerAdapter: netSwitch - " + isChecked);
                    config.setMobileNetworkLoadsImage(isChecked);
                    saveConfig();
                });

                msgSwitch.setTag("msgSwitch");
                netSwitch.setTag("netSwitch");
                break;
            }
            case ACCOUNT_MANAGER: {
                ACCOUNT_MANAGER_VIEWS = new View[ACCOUNT_MANAGER_ITEMS.length];
                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                ImageView iv1 = new ImageView(mContext);
                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
                ACCOUNT_MANAGER_VIEWS[0] = iv1;

                TextView tvName = new TextView(mContext);
                tvName.setText(info.getUser_name());
                tvName.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[1] = tvName;

                TextView tvEmail = new TextView(mContext);
                tvEmail.setText(info.getUser_email());
                tvEmail.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[2] = tvEmail;

                TextView tvPhone = new TextView(mContext);
                tvPhone.setText(info.getAccount());
                tvPhone.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[3] = tvPhone;

                ImageView iv2 = new ImageView(mContext);
                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
                ACCOUNT_MANAGER_VIEWS[4] = iv2;

                ImageView iv3 = new ImageView(mContext);
                iv3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv3);
                ACCOUNT_MANAGER_VIEWS[5] = iv3;
                break;
            }
            case PERSONAL_INFO: {
                PERSONAL_INFO_VIEWS = new View[PERSONAL_INFO_ITEMS.length];
                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();

                TextView tvGender = new TextView(mContext);
                tvGender.setText(((info.getUser_gender() != null) && "1".equals(info.getUser_gender())) ? "男" : "女");
                tvGender.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[0] = tvGender;

                TextView tvBirthday = new TextView(mContext);
                tvBirthday.setText(info.getUser_birthday());
                tvBirthday.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[1] = tvBirthday;

                TextView tvCity = new TextView(mContext);
                tvCity.setText(info.getUser_city());
                tvCity.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[2] = tvCity;

                TextView tvCareer = new TextView(mContext);
                tvCareer.setText(info.getUser_career());
                tvCareer.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[3] = tvCareer;
                break;
            }
            default: {
                // 默认直接爆炸
                break;
            }
        }
    }

    @NonNull
    @Override
    public SettingRecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_setting_recycler_item, viewGroup, false));
    }


    private static final String[] MAIN_SETTING_ITEMS = {"账号管理", "消息设置", "移动网络下加载图片", "清理缓存"};
    public View[] MAIN_SETTING_VIEWS;
    private static final String[] ACCOUNT_MANAGER_ITEMS = {"修改个人资料", "修改昵称", "修改邮箱地址", "修改手机号", "修改密码", "修改头像"};
    public View[] ACCOUNT_MANAGER_VIEWS;
    private static final String[] PERSONAL_INFO_ITEMS = {"修改性别", "修改出生日期", "修改城市", "修改专业"};
    public View[] PERSONAL_INFO_VIEWS;

    @Override
    public void onBindViewHolder(@NonNull SettingRecyclerAdapter.Holder holder, int i) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        switch (type) {
            case MAIN_SETTING: {
                // 主设置界面
                holder.widgetLayout.removeAllViews();
                holder.title.setText(MAIN_SETTING_ITEMS[i]);
                holder.widgetLayout.addView(MAIN_SETTING_VIEWS[i], params);
                break;
            }
            case ACCOUNT_MANAGER: {
                holder.itemView.setClickable(true);
                holder.widgetLayout.removeAllViews();
                holder.title.setText(ACCOUNT_MANAGER_ITEMS[i]);
                holder.widgetLayout.addView(ACCOUNT_MANAGER_VIEWS[i], params);
                if (i == 3){
                    holder.itemView.setClickable(false);
                }
                break;
            }
            case PERSONAL_INFO: {
                holder.widgetLayout.removeAllViews();
                holder.title.setText(PERSONAL_INFO_ITEMS[i]);
                holder.widgetLayout.addView(PERSONAL_INFO_VIEWS[i], params);
                break;
            }
            default: {
                // 默认直接爆炸
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case MAIN_SETTING: {
                return 4;
            }
            case ACCOUNT_MANAGER: {
                return 6;
            }
            case PERSONAL_INFO: {
                return 4;
            }
            default: {
                // 默认直接爆炸
                return -1;
            }
        }
    }

    class Holder extends DFRecyclerViewClickableAdapter.DFViewHolder {

        TextView title;
        RelativeLayout widgetLayout; // 塞图片或者Spinner

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.setting_title);
            widgetLayout = itemView.findViewById(R.id.setting_widget);
        }
    }

    // config对象的保存路径
    private static final String CONFIG_FILE_PATH = "setting_config_json";

    // 该方法在主设置的消息通知和加载图片的spinner改变之后调用，其他的不用管
    private void saveConfig() {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(DesignForumApplication.getMyAppContext().openFileOutput(CONFIG_FILE_PATH, Context.MODE_PRIVATE)));
            String json = JSON.toJSONString(config);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readConfig() {
        // files文件夹
        File files = DesignForumApplication.getMyAppContext().getFilesDir();
        File configFile = new File(files, CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            config = new SettingConfig();
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(DesignForumApplication.getMyAppContext().openFileInput(CONFIG_FILE_PATH)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            config = JSON.parseObject(sb.toString(), SettingConfig.class);
            if (config == null) {
                config = new SettingConfig();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            config = new SettingConfig();
        }
    }

    public void updateInfo() {
        Log.e(TAG, "updateInfo");
        // 再次更新相关的View
//        switch (type) {
//            case MAIN_SETTING: {
////                MAIN_SETTING_VIEWS = new View[MAIN_SETTING_ITEMS.length];
////                ImageView iv1 = new ImageView(mContext);
////                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
////                MAIN_SETTING_VIEWS[0] = iv1;
////
////                SwitchCompat msgSwitch = new SwitchCompat(mContext);
////                msgSwitch.setChecked(config.isMessageNotification());
////                msgSwitch.setClickable(false);
////                msgSwitch.setFocusable(false);
////                MAIN_SETTING_VIEWS[1] = msgSwitch;
////
////                SwitchCompat netSwitch = new SwitchCompat(mContext);
////                netSwitch.setChecked(config.isMobileNetworkLoadsImage());
////                netSwitch.setClickable(false);
////                netSwitch.setFocusable(false);
////                MAIN_SETTING_VIEWS[2] = netSwitch;
////
////                ImageView iv2 = new ImageView(mContext);
////                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
////                MAIN_SETTING_VIEWS[3] = iv2;
////
////                msgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
////                    Log.e(TAG, "SettingRecyclerAdapter: msgSwitch - " + isChecked);
////                    config.setMessageNotification(isChecked);
////                    saveConfig();
////                });
////                netSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
////                    Log.e(TAG, "SettingRecyclerAdapter: netSwitch - " + isChecked);
////                    config.setMobileNetworkLoadsImage(isChecked);
////                    saveConfig();
////                });
////
////                msgSwitch.setTag("msgSwitch");
////                netSwitch.setTag("netSwitch");
//                break;
//            }
//            case ACCOUNT_MANAGER: {
////                ACCOUNT_MANAGER_VIEWS = new View[ACCOUNT_MANAGER_ITEMS.length];
//                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
////                ImageView iv1 = new ImageView(mContext);
////                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
////                ACCOUNT_MANAGER_VIEWS[0] = iv1;
//
//                TextView tvName = (TextView) ACCOUNT_MANAGER_VIEWS[1];
//                tvName.setText(info.getUser_name());
//
//                TextView tvEmail = (TextView) ACCOUNT_MANAGER_VIEWS[2];
//                tvEmail.setText(info.getUser_email());
//
//                TextView tvPhone = (TextView) ACCOUNT_MANAGER_VIEWS[3];
//                tvPhone.setText(info.getAccount());
//
////                ImageView iv2 = new ImageView(mContext);
////                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
////                ACCOUNT_MANAGER_VIEWS[4] = iv2;
//
////                ImageView iv3 = new ImageView(mContext);
////                iv3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv3);
////                ACCOUNT_MANAGER_VIEWS[5] = iv3;
//                break;
//            }
//            case PERSONAL_INFO: {
////                PERSONAL_INFO_VIEWS = new View[PERSONAL_INFO_ITEMS.length];
//                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
//
//                TextView tvGender = (TextView) PERSONAL_INFO_VIEWS[0];
//                tvGender.setText(((info.getUser_gender() != null) && "1".equals(info.getUser_gender())) ? "男" : "女");
//
//                TextView tvBirthday = (TextView) PERSONAL_INFO_VIEWS[1];
//                tvBirthday.setText(info.getUser_birthday());
//
//                TextView tvCity = (TextView) PERSONAL_INFO_VIEWS[2];
//                tvCity.setText(info.getUser_city());
//
//                TextView tvCareer = (TextView) PERSONAL_INFO_VIEWS[3];
//                tvCareer.setText(info.getUser_career());
//                break;
//            }
//            default: {
//                // 默认直接爆炸
//                break;
//            }
//        }
        switch (type) {
            case MAIN_SETTING: {
                MAIN_SETTING_VIEWS = new View[MAIN_SETTING_ITEMS.length];
                ImageView iv1 = new ImageView(mContext);
                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
                MAIN_SETTING_VIEWS[0] = iv1;

                SwitchCompat msgSwitch = new SwitchCompat(mContext);
                msgSwitch.setChecked(config.isMessageNotification());
                msgSwitch.setClickable(false);
                msgSwitch.setFocusable(false);
                MAIN_SETTING_VIEWS[1] = msgSwitch;

                SwitchCompat netSwitch = new SwitchCompat(mContext);
                netSwitch.setChecked(config.isMobileNetworkLoadsImage());
                netSwitch.setClickable(false);
                netSwitch.setFocusable(false);
                MAIN_SETTING_VIEWS[2] = netSwitch;

                ImageView iv2 = new ImageView(mContext);
                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
                MAIN_SETTING_VIEWS[3] = iv2;

                msgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.e(TAG, "SettingRecyclerAdapter: msgSwitch - " + isChecked);
                    config.setMessageNotification(isChecked);
                    saveConfig();
                });
                netSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.e(TAG, "SettingRecyclerAdapter: netSwitch - " + isChecked);
                    config.setMobileNetworkLoadsImage(isChecked);
                    saveConfig();
                });

                msgSwitch.setTag("msgSwitch");
                netSwitch.setTag("netSwitch");
                break;
            }
            case ACCOUNT_MANAGER: {
                ACCOUNT_MANAGER_VIEWS = new View[ACCOUNT_MANAGER_ITEMS.length];
                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                ImageView iv1 = new ImageView(mContext);
                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv1);
                ACCOUNT_MANAGER_VIEWS[0] = iv1;

                TextView tvName = new TextView(mContext);
                tvName.setText(info.getUser_name());
                tvName.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[1] = tvName;

                TextView tvEmail = new TextView(mContext);
                tvEmail.setText(info.getUser_email());
                tvEmail.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[2] = tvEmail;

                TextView tvPhone = new TextView(mContext);
                tvPhone.setText(info.getAccount());
                tvPhone.setGravity(Gravity.CENTER);
                ACCOUNT_MANAGER_VIEWS[3] = tvPhone;

                ImageView iv2 = new ImageView(mContext);
                iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv2);
                ACCOUNT_MANAGER_VIEWS[4] = iv2;

                ImageView iv3 = new ImageView(mContext);
                iv3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Glide.with(mContext).load(R.drawable.ic_arrow).into(iv3);
                ACCOUNT_MANAGER_VIEWS[5] = iv3;
                break;
            }
            case PERSONAL_INFO: {
                PERSONAL_INFO_VIEWS = new View[PERSONAL_INFO_ITEMS.length];
                UserInfo info = DesignForumApplication.mainBinder.getUserInfo();

                TextView tvGender = new TextView(mContext);
                tvGender.setText(((info.getUser_gender() != null) && "1".equals(info.getUser_gender())) ? "男" : "女");
                tvGender.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[0] = tvGender;

                TextView tvBirthday = new TextView(mContext);
                tvBirthday.setText(info.getUser_birthday());
                tvBirthday.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[1] = tvBirthday;

                TextView tvCity = new TextView(mContext);
                tvCity.setText(info.getUser_city());
                tvCity.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[2] = tvCity;

                TextView tvCareer = new TextView(mContext);
                tvCareer.setText(info.getUser_career());
                tvCareer.setGravity(Gravity.CENTER);
                PERSONAL_INFO_VIEWS[3] = tvCareer;
                break;
            }
            default: {
                // 默认直接爆炸
                break;
            }
        }
//        this.notifyItemRangeRemoved(0, getItemCount() - 1);
        notifyDataSetChanged();
    }
}
