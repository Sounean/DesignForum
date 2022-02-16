package com.chienli.micro_class.view_model;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.data_model.SearchItemInfo;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MicroClassSearchActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MicroClassSearchActivit";
    public final MutableLiveData<List<SearchItemInfo>> rclData = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isError = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();

    public MicroClassSearchActivityViewModel(@NonNull Application application) {
        super(application);
        isLoading.setValue(true);
        isError.setValue(false);
        isSuccess.setValue(false);
        rclData.setValue(new ArrayList<>());
    }

    public void searchByKey(LifecycleOwner lifecycleOwner, String key) {
//        if (isLoading.getValue() != null && isLoading.getValue()) { // 不为null且正在加载中
//            return;
//        }
        isLoading.postValue(true);
        Observable
                .create(new ObservableOnSubscribe<JSONObject>() {
                    @Override
                    public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                        Log.e(TAG, "subscribe: 开始数据访问" );
                        String url = DesignForumApplication.HOST + "video/getValue";
                        FormBody body = new FormBody.Builder()
                                .add("key", key)
                                .build();

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Response response = DesignForumApplication.client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject obj = JSON.parseObject(response.body().string());
                            int code = obj.getInteger("code");
                            if (code == 100) {
                                // success
                                emitter.onNext(obj);
                            } else {
                                emitter.onError(new Exception("网络访问出错了; json code = " + code));
                            }
                        } else {
                            emitter.onError(new Exception("网络访问出错了; response = " + response));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, List<SearchItemInfo>>() {
                    @Override
                    public List<SearchItemInfo> apply(JSONObject s) throws Exception {
                        return s.getJSONObject("content").getJSONArray("vaule").toJavaList(SearchItemInfo.class);
                    }
                })
                .doOnNext(new Consumer<List<SearchItemInfo>>() {
                    @Override
                    public void accept(List<SearchItemInfo> searchItemInfos) throws Exception {
                        Log.e(TAG, "onNext");
                        rclData.setValue(searchItemInfos);
                        isError.setValue(false);
                        isLoading.setValue(false);
                        isSuccess.setValue(true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "onError: ", throwable);
                        isError.setValue(true);
                        isLoading.setValue(false);
                        isSuccess.setValue(false);
                    }
                })
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                .subscribe();
    }

}
