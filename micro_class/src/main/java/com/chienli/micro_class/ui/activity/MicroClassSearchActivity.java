package com.chienli.micro_class.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.R;
import com.chienli.micro_class.R2;
import com.chienli.micro_class.data_model.SearchItemInfo;
import com.chienli.micro_class.ui.activity.base.BaseActivity;
import com.chienli.micro_class.ui.adapter.MicroClassSearchRecyclerViewAdapter;
import com.chienli.micro_class.ui.adapter.recycler_click_interface.OnItemClickListener;
import com.chienli.micro_class.view_model.MicroClassSearchActivityViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class MicroClassSearchActivity extends BaseActivity implements OnRefreshLoadMoreListener, OnItemClickListener, TextView.OnEditorActionListener {

    private static final String KEY = "KEY";
    @BindView(R2.id.et_all_toolbar_search)
    EditText etAllToolbarSearch;
    @BindView(R2.id.rcl_search)
    RecyclerView rclSearch;
    @BindView(R2.id.is_loading_search)
    LinearLayout isLoadingSearch;
    @BindView(R2.id.is_error_search)
    TextView isErrorSearch;
    @BindView(R2.id.ref_search)
    SmartRefreshLayout refSearch;

    private MicroClassSearchActivityViewModel viewModel;
    private MicroClassSearchRecyclerViewAdapter adapter;
    private static final String TAG = "MicroClassSearchActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_class_search);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(MicroClassSearchActivity.this).get(MicroClassSearchActivityViewModel.class);
        // ?????????????????????????????????????????????????????????????????????????????????
        initView();
        initObservers();
        initData();
    }

    private void initData() {
        String key = getIntent().getStringExtra(KEY);
        if (key != null) {
            viewModel.searchByKey(this, key);
        }
    }

    private void initView() {
        // ??????recyclerView???Adapter
        rclSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MicroClassSearchRecyclerViewAdapter(this, viewModel.rclData.getValue(), this);
        rclSearch.setAdapter(adapter);
        // ??????????????????
        refSearch.setOnRefreshLoadMoreListener(this);

        etAllToolbarSearch.setText(getIntent().getStringExtra(KEY)); // ???????????????????????????
        etAllToolbarSearch.setOnEditorActionListener(this);
    }

    private void initObservers() {
        // ??????recyclerView???????????????
        viewModel.rclData.observe(this, new Observer<List<SearchItemInfo>>() {
            @Override
            public void onChanged(@Nullable List<SearchItemInfo> searchItemInfo) {
                if (adapter != null) {
                    Log.e(TAG, "onChanged: update adapter");
                    adapter.update(searchItemInfo);
                }
            }
        });
        // ????????????
        viewModel.isError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.e(TAG, "onChanged: error = " + aBoolean);
                if (aBoolean == null) {
                    return;
                }
                if (aBoolean) {
                    isErrorSearch.setVisibility(View.VISIBLE);
                } else {
                    isErrorSearch.setVisibility(View.GONE);
                }
            }
        });
        // ????????????
        viewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.e(TAG, "onChanged: loading = " + aBoolean);
                if (aBoolean == null) {
                    return;
                }
                if (aBoolean) {
                    isLoadingSearch.setVisibility(View.VISIBLE);
                } else {
                    isLoadingSearch.setVisibility(View.GONE);
                }
            }
        });
        // ??????????????????
        viewModel.isSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.e(TAG, "onChanged: success = " + aBoolean);
                if (aBoolean == null) {
                    return;
                }
                if (aBoolean) {
                    rclSearch.setVisibility(View.VISIBLE);
                } else {
                    rclSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    // ????????????
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    // ????????????
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }

    /**
     * recyclerView?????????????????????
     *
     * @param v        itemView
     * @param position ??????
     */
    @Override
    public void onClick(View v, int position) {
        Log.e(TAG, "onClick: " + position);
        List<SearchItemInfo> data = viewModel.rclData.getValue();
        if (data != null && position < data.size()) {
            MicroClassActivity.startMicroClassActivity(this, DesignForumApplication.mainBinder.getUserInfo().getId(), data.get(position).getCourseId());
        }
    }


    // ?????????????????????
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String key = v.getText().toString();
            if ("".equals(key)) {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
                return false;
            }
            viewModel.searchByKey(this, v.getText().toString());
            // ???????????????
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return true;
        }
        return false;
    }

    public static void startMicroClassSearchActivity(Context context, String key) {
        Intent intent = new Intent(context, MicroClassSearchActivity.class);
        intent.putExtra(KEY, key);
        context.startActivity(intent);
    }
}
