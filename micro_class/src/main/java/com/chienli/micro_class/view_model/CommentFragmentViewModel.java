package com.chienli.micro_class.view_model;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.data_model.CommentInfo;
import com.chienli.micro_class.data_model.MicroClassInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.adapter.CommentRecyclerViewAdapter;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class CommentFragmentViewModel {
    private static final String TAG = "CommentFragmentViewMode";

    public final ObservableField<CommentInfo> info = new ObservableField<>();
    public final Context context = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class);
    public final MicroClassActivityViewModel parentViewModel = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM(); // 父容器的信息
    public final ObservableInt videoId;

    public CommentFragmentViewModel() {
        videoId = parentViewModel.focusVideoPosition;
        videoId.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateData();
            }
        });
        updateData();
    }

    public void updateData() {
        Log.e(TAG, "updateData");
        io.reactivex.Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        int pageNum = 1;
                        CommentInfo info = CommentFragmentViewModel.this.info.get();
                        if (info != null) {
                            pageNum = info.getPageNum() + 1;
                        }
                        MicroClassInfo microClassInfo = parentViewModel.info.get();
                        if (microClassInfo == null) {
                            emitter.onError(new Exception("parentViewModel.info.get() == null, 1s后尝试进行下一次的循环"));
                            io.reactivex.Observable
                                    .timer(1, TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Long>() {

                                        private Disposable d;

                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            this.d = d;
                                        }

                                        @Override
                                        public void onNext(Long aLong) {
                                            Log.e(TAG, "onNext: timer");
                                            d.dispose();
                                            updateData();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e(TAG, "onError: timer");
                                        }

                                        @Override
                                        public void onComplete() {
                                            Log.e(TAG, "onComplete: timer");
                                            d.dispose();
                                            updateData();
                                        }
                                    });
                            return;
                        }
                        String url = DesignForumApplication.HOST + "comment/getComment;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();
                        FormBody body = new FormBody.Builder()
                                .add("discuss_type", "3")
                                .add("id", String.valueOf(microClassInfo.getVideo().get(videoId.get()).getId()))
                                .add("page_num", String.valueOf(pageNum))
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        Response response = DesignForumApplication.client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String json = response.body().string();
                            int code = JSON.parseObject(json).getInteger("code");
                            if (code == 0) {
                                emitter.onNext(json);
                            } else if (code == 8) {
                                // 返回的数据为空
                                emitter.onComplete();
                            } else {
                                emitter.onError(new Exception("服务器数据访问异常 json code = " + code));
                            }
                        } else {
                            emitter.onError(new Exception("网络访问异常 : " + response.toString()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<String, CommentInfo>() {
                    @Override
                    public CommentInfo apply(String s) throws Exception {
                        return JSONObject.parseObject(s).getJSONObject("content").getJSONObject("content").toJavaObject(CommentInfo.class);
                    }
                })
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<CommentInfo>() {
                    @Override
                    public void accept(CommentInfo comment) throws Exception {
                        if (CommentFragmentViewModel.this.info.get() == null) {
                            // 第一次构建
                            CommentFragmentViewModel.this.info.set(comment);
                            return;
                        }
                        // 先构造一个与旧数据相同的集合
                        List<CommentInfo.CommentBean> oldComments = new ArrayList<>(CommentFragmentViewModel.this.info.get().getList());
                        List<CommentInfo.CommentBean> newComments = comment.getList();

                        if (oldComments.size() == 0) {
                            CommentFragmentViewModel.this.info.set(comment);
                            return;
                        }

                        if (oldComments.get(0).getParentId() == newComments.get(0).getParentId()) { // 发送异常就会中止，
                            oldComments.addAll(newComments);
                        } else {
                            CommentFragmentViewModel.this.info.set(comment);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "error: ", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        io.reactivex.Observable
                                .empty()
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                                .subscribe(new Observer<Object>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Object o) {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @BindingAdapter({"commentAdapterData"})
    public static void commentRclAdapter(RecyclerView rcl, ObservableField<CommentInfo> info) {
        LinearLayoutManager manager = new LinearLayoutManager(rcl.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rcl.setLayoutManager(manager);
        rcl.setAdapter(new CommentRecyclerViewAdapter(rcl.getContext(), info));
    }
}
