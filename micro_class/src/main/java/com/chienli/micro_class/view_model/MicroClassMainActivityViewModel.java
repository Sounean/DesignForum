package com.chienli.micro_class.view_model;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.data_model.MicroClassBaseInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.activity.MicroClassMainActivity;
import com.chienli.micro_class.ui.activity.MicroClassSearchActivity;
import com.chienli.micro_class.ui.adapter.base.BaseBindingRecyclerViewAdapter;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.chienli.micro_class.view_model.base.BaseOnListChangedCallback;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class MicroClassMainActivityViewModel {
    private static final String TAG = "MicroMainActivityViewMo";

    public final Context context;// ????????????????????????????????????????????????????????????
    public final ObservableArrayList<String> bannerImgUrlList = new ObservableArrayList<>();// ????????????????????????
    public final ObservableArrayList<MicroClassBaseInfo> microClassBaseInfoList = new ObservableArrayList<>();// ????????????????????????id???????????????????????????
    public final ObservableBoolean isError = new ObservableBoolean(false);// ????????????????????????????????????????????????????????????????????????
    public final ObservableBoolean isLoading = new ObservableBoolean(false);// ????????????????????????
    public final ObservableBoolean isShow = new ObservableBoolean(false);// ??????????????????

    // RecyclerView?????????
    public final ObservableInt rclItemLayoutId = new ObservableInt();// RecyclerView????????????Id
    public final ObservableInt rclAdapterModelBRId = new ObservableInt();
    public final ObservableInt rclAdapterPositionBRId = new ObservableInt();

    public MicroClassMainActivityViewModel(Context context, int rclItemLayoutId, int rclAdapterModelBRId, int rclAdapterPositionBRId) {
        this.context = context;
        this.rclItemLayoutId.set(rclItemLayoutId);
        this.rclAdapterModelBRId.set(rclAdapterModelBRId);
        this.rclAdapterPositionBRId.set(rclAdapterPositionBRId);
        updateData();
    }

    private void startLoading() {
        isLoading.set(true);
        isError.set(false);
        isShow.set(false);
    }

    private void startError() {
        isLoading.set(false);
        isError.set(true);
        isShow.set(false);
    }

    public void startShow() {
        isLoading.set(false);
        isError.set(false);
        isShow.set(true);
    }

    /**
     * ????????????????????????????????????
     * ???????????????????????????????????????bannerImgUrlList????????????microClassBaseInfoList
     * ?????????????????????isError???isLoading???isShow
     */
    public void updateData() {
        Log.e(TAG, "updateData");
        startLoading();
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        Log.e(TAG, "source");
                        Request request = new Request.Builder()
                                .url("https://www.cyhfwq.top/designForum/Course/getCourses;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId())
                                .build();
                        Response response = DesignForumApplication.client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String json = response.body().string();
                            int code = JSON.parseObject(json).getInteger("code");
                            if (code == 100) {
                                emitter.onNext(json);
                            } else {
                                emitter.onError(new Exception("??????????????????????????? json code = " + code));
                            }
                        } else {
                            emitter.onError(new Exception("?????????????????? : " + response.toString()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, List<MicroClassBaseInfo>>() {
                    @Override
                    public List<MicroClassBaseInfo> apply(String s) throws Exception {
                        Log.e(TAG, "apply");
                        return JSONObject.parseObject(s).getJSONObject("content").getJSONArray("Course_list").toJavaList(MicroClassBaseInfo.class);
                    }
                })
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Observer<List<MicroClassBaseInfo>>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(List<MicroClassBaseInfo> microClassBaseInfos) {
                        Log.e(TAG, "onNext");
                        // ????????????????????????????????????????????????
                        // ?????????????????? ????????????????????????
                        microClassBaseInfoList.clear();
                        microClassBaseInfoList.addAll(microClassBaseInfos);
                        Log.e(TAG, "onNext: list size = " + microClassBaseInfoList.size());
                        for (MicroClassBaseInfo microClassBaseInfo : microClassBaseInfos) {
                            bannerImgUrlList.add(DesignForumApplication.HOST + "images/Course/" + microClassBaseInfo.getPath() + "/" + microClassBaseInfo.getImage());
                        }
                        startShow();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                        d.dispose();
                        startError();
                    }

                    @Override
                    public void onComplete() {
                        d.dispose();
                    }
                });
    }


    /**
     * ????????????RecyclerView???BindingAdapter???????????????????????????ID????????????????????????
     *
     * @param rcl                    RecyclerView
     * @param layoutId               item?????????id
     * @param adapterModels          ??????????????????????????????????????????????????????ViewModel
     * @param rclAdapterModelBRId    ????????????BR???
     * @param rclAdapterPositionBRId ???????????????position???BR?????????rclAdapterModelBRId??????
     */
    @BindingAdapter({"rclItemLayoutId", "rclAdapterModel", "rclAdapterModelBRId", "rclAdapterPositionBRId", "context"})
    public static void setRclAdapter(RecyclerView rcl, int layoutId, ObservableArrayList<?> adapterModels, int rclAdapterModelBRId, int rclAdapterPositionBRId, Context context) {
        Log.e(TAG, "setRclAdapter");
        rcl.setLayoutManager(new GridLayoutManager(context, 2));
//        LinearLayoutManager manager = new LinearLayoutManager(context);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        rcl.setLayoutManager(manager);
        rcl.setAdapter(new BaseBindingRecyclerViewAdapter<>(layoutId, adapterModels, rclAdapterModelBRId, rclAdapterPositionBRId));
    }

    @BindingAdapter("bannerData")
    public static void setBannerImgUrls(Banner banner, ObservableArrayList<String> bannerImgUrlList) {
        Log.e(TAG, "setBannerImgUrls");
        banner.setImages(bannerImgUrlList);
        //??????????????????????????????true
        banner.isAutoPlay(true);
        //??????????????????
        banner.setDelayTime(4000);
        //???????????????????????????banner???????????????????????????
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        banner.start();
        // ????????????????????????
        bannerImgUrlList.addOnListChangedCallback(new BaseOnListChangedCallback<ObservableArrayList<String>>() {
            @Override
            public void onChanged(ObservableArrayList<String> sender) {
                banner.update(sender);
            }
        });

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ObservableArrayList<MicroClassBaseInfo> info = ActivityManagerUtil.findActivityByClass(MicroClassMainActivity.class).getBinding().getMainVM().microClassBaseInfoList;
                if (info != null) {
                    MicroClassActivity.startMicroClassActivity(ActivityManagerUtil.findActivityByClass(MicroClassMainActivity.class),
                            DesignForumApplication.mainBinder.getUserInfo().getId(),
                            info.get(position).getId());
                }
            }
        });
    }

    @BindingAdapter("setSearchEditListener")
    public static void setSearchEditListener(View v, TextView.OnEditorActionListener listener) {
        ((EditText) v).setOnEditorActionListener(listener);
    }

    public static TextView.OnEditorActionListener LISTENER = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                MicroClassSearchActivity.startMicroClassSearchActivity(ActivityManagerUtil.findActivityByClass(MicroClassMainActivity.class), v.getText().toString());
                return true;
            }
            return false;
        }
    };


}
