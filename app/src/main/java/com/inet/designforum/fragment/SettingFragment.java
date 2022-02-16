package com.inet.designforum.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.Province;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.inet.designforum.R;
import com.inet.designforum.activity.MainActivity;
import com.inet.designforum.adapter.SettingRecyclerAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;
import com.inet.designforum.dialog.SettingChangeEmailDialog;
import com.inet.designforum.dialog.SettingChangeNameDialog;
import com.inet.designforum.dialog.SettingChangePasswordDialog;
import com.inet.designforum.dialog.SettingChangePhoneDialog;
import com.inet.designforum.fragment.base.BaseFragment;
import com.inet.designforum.util.DFFragmentHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SettingFragment extends BaseFragment implements OnItemClickListener, View.OnClickListener {

    private String type = "Null";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        if (getArguments() != null) {
            type = getArguments().getString("TYPE");
        }
        super.init(view);
        return view;
    }

    private RecyclerView mRecycler;
    private LinearLayout mBack;
    private TextView mTitle;
    private LinearLayout mActionLayout;
    private TextView mActionTitle;


    private Handler handler = new DFFragmentHandler<>(this);

    @Override
    public void findViews(View mView) {
        mRecycler = mView.findViewById(R.id.rcl_setting);
        mBack = mView.findViewById(R.id.ll_toolbar_back);
        mTitle = mView.findViewById(R.id.tv_toolbar_title);
        mActionLayout = mView.findViewById(R.id.ll_toolbar_action);
        mActionTitle = mView.findViewById(R.id.tv_toolbar_action);
    }

    @Override
    public void getData(View mView) {
        // 在type为PERSONAL_INFO的时候获取城市信息和专业信息
        if (this.type.equals(PERSONAL_INFO)) {
            String urlProvince = DesignForumApplication.HOST + "ConstantController/getCity;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
            Request requestProvince = new Request.Builder()
                    .url(urlProvince)
                    .build();

            DesignForumApplication.client.newCall(requestProvince).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    handler.post(() -> Toast.makeText(getActivity(), "网络访问失败", Toast.LENGTH_SHORT).show());
                    getProvinceSuccessful = false;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.e(TAG, "onResponse: " + response);
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        int code = JSON.parseObject(json).getInteger("code");
                        if (code == 0) {
                            province = JSONObject.parseObject(json).getJSONObject("content").getJSONArray("city").toJavaList(Province.class);
                            getProvinceSuccessful = true;

                        } else {
                            getProvinceSuccessful = false;
                            handler.post(() -> Toast.makeText(getActivity(), "数据访问错误: Error-" + code, Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });

            String urlCareer = DesignForumApplication.HOST + "ConstantController/getAllCareer;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
            Request requestCareer = new Request.Builder()
                    .url(urlCareer)
                    .build();
            DesignForumApplication.client.newCall(requestCareer).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    handler.post(() -> Toast.makeText(getActivity(), "网络访问失败", Toast.LENGTH_SHORT).show());
                    getCareerSuccessful = false;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.e(TAG, "onResponse: " + response);
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        int code = JSON.parseObject(json).getInteger("code");
                        if (code == 0) {
                            JSONObject obj = JSONObject.parseObject(json);
                            career = obj.getJSONObject("content").getJSONArray("career_name").toJavaList(String.class);
                            getCareerSuccessful = true;
                        } else {
                            getCareerSuccessful = false;
                            handler.post(() -> Toast.makeText(getActivity(), "数据访问错误: Error-" + code, Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });
        }
    }

    private List<Province> province = new ArrayList<>();
    private List<String> career = new ArrayList<>();
    private boolean getProvinceSuccessful = false;
    private boolean getCareerSuccessful = false;

    private SettingRecyclerAdapter adapter;

    @Override
    public void initViews(View mView) {
        adapter = new SettingRecyclerAdapter(getActivity(), this, type);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(adapter);
        mBack.setOnClickListener(this);

        switch (type) {
            case MAIN_SETTING: {
                mActionLayout.setVisibility(View.GONE);
                mTitle.setText("设置");
                break;
            }
            case ACCOUNT_MANAGER: {
                mActionLayout.setVisibility(View.GONE);
                mTitle.setText("账号信息");
                break;
            }
            case PERSONAL_INFO: {
                mActionTitle.setText("提交");
                mTitle.setText("个人资料");
                mActionLayout.setOnClickListener(this);
                break;
            }
        }
    }

    private static final String TAG = "SettingFragment";

    @Override
    public void onClick(View v, int position) {
        switch (type) {
            case MAIN_SETTING: {
                if (position == 0) {
                    // 账号管理
                    if (DesignForumApplication.mainBinder.getUserInfo() == null) {
                        Toast.makeText(getContext(), "您还未登陆", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Fragment fragment = SettingFragment.getInstance(SettingFragment.ACCOUNT_MANAGER);
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.setting_fragment_root, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (position == 1) {
                    // 消息通知
                    SwitchCompat sw = v.findViewWithTag("msgSwitch");
                    if (sw != null) {
                        sw.setChecked(!sw.isChecked());
                    }
                } else if (position == 2) {
                    // 移动网络加载图片
                    SwitchCompat sw = v.findViewWithTag("netSwitch");
                    if (sw != null) {
                        sw.setChecked(!sw.isChecked());
                    } else {
                        Log.e(TAG, "onClick: sw == null");
                    }
                } else if (position == 3) {
                    // TODO : 清理缓存，有空做一下
                    Toast.makeText(getContext(), "清理完成", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ACCOUNT_MANAGER: {
                if (position == 0) {
                    Fragment fragment = SettingFragment.getInstance(SettingFragment.PERSONAL_INFO);
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.setting_fragment_root, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (position == 1) {
                    // 修改昵称
                    DialogFragment dialog = SettingChangeNameDialog.getInstance();
                    dialog.show(getChildFragmentManager(), null);
                } else if (position == 2) {
                    // 修改邮箱地址
                    DialogFragment dialog = SettingChangeEmailDialog.getInstance();
                    dialog.show(getChildFragmentManager(), null);
                } else if (position == 3) {
                    // 修改手机号
                    // 做到一半才发现并没有给修改手机号的接口（；´д｀）ゞ
                    DialogFragment dialog = SettingChangePhoneDialog.getInstance();
                    dialog.show(getChildFragmentManager(), null);
                } else if (position == 4) {
                    // 修改密码
                    DialogFragment dialog = SettingChangePasswordDialog.getInstance();
                    dialog.show(getChildFragmentManager(), null);
                } else if (position == 5) {
                    // 修改头像
                    // 开启相关的界面进行修改，回调在BaseActivity里
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(intent, MainActivity.GET_IMG);
                }
                break;
            }
            case PERSONAL_INFO: {
                // 这里的信息只是在本地进行修改，可以不用DialogFragment，直接手撸对话框，然后
                if (position == 0) {
                    // 修改性别
//                    TextView tvName = new TextView(getActivity());
//                    tvName.setText("AAA");
//                    tvName.setGravity(Gravity.CENTER);
//                    adapter.PERSONAL_INFO_VIEWS[0] = tvName;
//                    adapter.notifyDataSetChanged();
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setItems(new String[]{"男", "女"}, (dialog1, which) -> {
                                switch (which) {
                                    case 0: {
                                        // 男
                                        TextView tvName = new TextView(getActivity());
                                        tvName.setText("男");
                                        tvName.setGravity(Gravity.CENTER);
                                        adapter.PERSONAL_INFO_VIEWS[0] = tvName;
                                        adapter.notifyDataSetChanged();
                                        dialog1.dismiss();
                                        break;
                                    }
                                    case 1: {
                                        // 女
                                        TextView tvName = new TextView(getActivity());
                                        tvName.setText("女");
                                        tvName.setGravity(Gravity.CENTER);
                                        adapter.PERSONAL_INFO_VIEWS[0] = tvName;
                                        adapter.notifyDataSetChanged();
                                        dialog1.dismiss();
                                        break;
                                    }
                                }
                            })
                            .setCancelable(true)
                            .create();
                    dialog.show();
                } else if (position == 1) {
                    // 修改出生日期
                    Calendar c = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                        TextView tvDate = new TextView(getActivity());
                        tvDate.setText(String.format(Locale.US, "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                        tvDate.setGravity(Gravity.CENTER);
                        adapter.PERSONAL_INFO_VIEWS[1] = tvDate;
                        adapter.notifyDataSetChanged();
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                    break;
                } else if (position == 2) {
                    // 修改城市
                    if (!getProvinceSuccessful) {
                        Toast.makeText(getActivity(), "数据获取异常", Toast.LENGTH_SHORT).show();
                        getData(null);
                        return;
                    }
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_change_city_dialog, null, false);

                    Spinner sp = view.findViewById(R.id.province_spinner);
                    List<String> listProvince = new ArrayList<>();
                    for (Province p : province) {
                        listProvince.add(p.getProvince());
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listProvince);
                    sp.setAdapter(arrayAdapter);


                    Spinner sc = view.findViewById(R.id.city_spinner);
                    sc.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, province.get(0).getCities()));

                    // 设置监听
//                    sp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                        }
//                    });

                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String s = ((TextView) view).getText().toString();
                            for (int i = 0; i < province.size(); i++) {
                                Province pr = province.get(i);
                                if (s.equals(pr.getProvince())) {
                                    sc.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, pr.getCities()));
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setCancelable(false)
                            .setPositiveButton("确认", null)
                            .setNegativeButton("取消", null)
                            .setView(view)
                            .create();

                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(positiveBtn -> {
                        // 确认的点击事件
                        String p = (String) sp.getSelectedItem();
                        String c = (String) sc.getSelectedItem();
                        TextView tvCity = new TextView(getActivity());
                        tvCity.setText(String.format("%s %s", p, c));
                        tvCity.setGravity(Gravity.CENTER);
                        adapter.PERSONAL_INFO_VIEWS[2] = tvCity;
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });

                } else if (position == 3) {
                    // 修改专业
                    if (!getCareerSuccessful) {
                        Toast.makeText(getActivity(), "数据获取异常", Toast.LENGTH_SHORT).show();
                        getData(null);
                        return;
                    }
                    String[] c = new String[career.size()];
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setItems(career.toArray(c), (dialog12, which) -> {
                                TextView tvCareer = new TextView(getActivity());
                                tvCareer.setText(career.get(which));
                                tvCareer.setGravity(Gravity.CENTER);
                                adapter.PERSONAL_INFO_VIEWS[3] = tvCareer;
                                adapter.notifyDataSetChanged();

                            }).create();
                    dialog.show();
                }
                break;
            }
        }
    }

    public void update() {
        adapter.updateInfo();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        if (i == R.id.ll_toolbar_back) {
            if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                activity.onBackPressed();
            } else {
                activity.getSupportFragmentManager().popBackStack();
            }
        } else if (i == R.id.ll_toolbar_action) {
            // 个人资料提交按钮的点击事件
            String gender = ((TextView) adapter.PERSONAL_INFO_VIEWS[0]).getText().toString();
            String birthday = ((TextView) adapter.PERSONAL_INFO_VIEWS[1]).getText().toString();
            String city = ((TextView) adapter.PERSONAL_INFO_VIEWS[2]).getText().toString();
            String career = ((TextView) adapter.PERSONAL_INFO_VIEWS[3]).getText().toString();

            String url = DesignForumApplication.HOST + "InformationController/updateUser;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();

            FormBody body = new FormBody.Builder()
                    .add("user_gender", gender)
                    .add("user_birthday", birthday)
                    .add("user_city", city)
                    .add("user_career", career)
                    .build();

            Request request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();

            DesignForumApplication.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    handler.post(() -> Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        int code = JSON.parseObject(json).getInteger("code");
                        if (code == 0) {
                            UserInfo info = DesignForumApplication.mainBinder.getUserInfo();
                            info.setUser_gender(gender);
                            info.setUser_birthday(birthday);
                            info.setUser_career(career);
                            info.setUser_gender(gender.equals("男") ? "1" : "0");
                            DesignForumApplication.mainBinder.updateUserInfo(info);
                            handler.post(() -> Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show());
                        } else {
                            handler.post(() -> Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        adapter.updateInfo();
        super.onStart();
        Log.e(TAG, "onStart");
    }

    public static final String MAIN_SETTING = "MAIN_SETTING";
    public static final String ACCOUNT_MANAGER = "ACCOUNT_MANAGER";
    public static final String PERSONAL_INFO = "PERSONAL_INFO";

    /**
     * 获取设置界面的fragment，最后因为多级设置目录，通过type返回不同的内容来展示、模拟返回栈
     *
     * @param type MAIN_SETTING  主设置
     *             ACCOUNT_MANAGER 账号管理
     *             PERSONAL_INFO 个人资料
     * @return SettingFragment
     */
    public static Fragment getInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        Fragment fragment = new SettingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
