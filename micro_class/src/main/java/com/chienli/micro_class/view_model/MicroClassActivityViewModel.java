package com.chienli.micro_class.view_model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.MicroClassInfo;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.fragment.CommentFragment;
import com.chienli.micro_class.ui.fragment.VideoListFragment;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.chienli.micro_class.util.FragmentManagerUtil;
import com.seselin.view.ExpandLayout;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JzvdStd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MicroClassActivityViewModel {
    private static final String TAG = "MicroClassActivityViewM";

    public final Context context;
    public final ObservableField<MicroClassInfo> info = new ObservableField<>();
    public final ObservableInt focusVideoPosition = new ObservableInt(0);// ??????????????????????????????????????????

    public final int userId;
    public final int classId;

    public final ObservableBoolean isError = new ObservableBoolean(false);// ????????????????????????????????????????????????????????????????????????
    public final ObservableBoolean isLoading = new ObservableBoolean(false);// ????????????????????????
    public final ObservableBoolean isShow = new ObservableBoolean(false);// ??????????????????


    public final ObservableList<Fragment> fragmentList = new ObservableArrayList<>(); // ????????????Fragment???List, ????????????ViewPager

    public MicroClassActivityViewModel(Context context, int userId, int classId) {
        this.context = context;
        this.userId = userId;
        this.classId = classId;

        focusVideoPosition.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                updateData();
            }
        });

        initFragmentList();
        updateData();
    }

    private void initFragmentList() {
        fragmentList.add(VideoListFragment.getInstance(userId, classId));
        fragmentList.add(CommentFragment.getInstance(focusVideoPosition.get()));
    }

    /**
     * ???????????????????????????
     */
    private void updateData() {
        startLoading();
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        FormBody body = new FormBody.Builder()
                                .add("userId", String.valueOf(userId))
                                .add("courseId", String.valueOf(classId))
                                .build();
                        Request request = new Request.Builder()
                                .post(body)
                                .url("https://www.cyhfwq.top/designForum/Course/getVideo;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId())
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
                .map(new Function<String, MicroClassInfo>() {
                    @Override
                    public MicroClassInfo apply(String s) {
                        return JSONObject.parseObject(s).getJSONObject("content").getJSONObject("Course_list").toJavaObject(MicroClassInfo.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<MicroClassInfo>() {
                    @Override
                    public void accept(MicroClassInfo microClassInfo) throws Exception {
                        info.set(microClassInfo);
                        sendStudyMsg();
                        startShow(200);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: ", throwable);
                        startError(200);
                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void sendStudyMsg() {
        if (info.get().getStudy_video().contains(info.get().getVideo().get(focusVideoPosition.get()).getId())) {
            // ??????????????????
            return;
        }
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        String url = DesignForumApplication.HOST + "record/videoRecord";
                        FormBody body = new FormBody.Builder()
                                .add("userId", String.valueOf(userId))
                                .add("courseId", String.valueOf(classId))
                                .add("videoId", String.valueOf(info.get().getVideo().get(focusVideoPosition.get()).getId()))
                                .build();
                        Request request = new Request.Builder()
                                .post(body)
                                .url(url)
                                .build();
                        Response response = DesignForumApplication.client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String json = response.body().string();
                            try {
                                int code = JSON.parseObject(json).getInteger("code");
                                emitter.onNext(code);
                            } catch (JSONException e) {
                                // ????????????????????????????????????????????????????????????????????????
                                int code = Integer.valueOf(json);
                                emitter.onNext(code);
                            }
                        } else {
                            emitter.onError(new Exception("?????????????????? : " + response.toString()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer code) throws Exception {
                        if (code == 0) {
                            // ??????
                            info.get().getStudy_video().add(info.get().getVideo().get(focusVideoPosition.get()).getId());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "error: ", throwable);
                    }
                });
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

    private void startLoading(int delay) {
        Observable
                .timer(delay, TimeUnit.MILLISECONDS)
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        isLoading.set(true);
                        isError.set(false);
                        isShow.set(false);
                    }
                });
    }

    private void startError(int delay) {
        Observable
                .timer(delay, TimeUnit.MILLISECONDS)
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        isLoading.set(false);
                        isError.set(true);
                        isShow.set(false);
                    }
                });

    }

    public void startShow(int delay) {
        Observable
                .timer(delay, TimeUnit.MILLISECONDS)
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        isLoading.set(false);
                        isError.set(false);
                        isShow.set(true);
                    }
                });
    }


    /**
     * ??????????????????????????????????????????????????????????????????????????????
     *
     * @param layout ???????????????{@link com.seselin.view}
     */
    @BindingAdapter("onExpandViewInflate")
    public static void onExpandViewInflate(ExpandLayout layout, boolean isExpand) {
        layout.initExpand(isExpand); // ???????????????
    }

    @BindingAdapter({"videoPosition", "videoBeanList"})
    public static void onFocusVideoPositionChanged(JzvdStd video, int position, List<MicroClassInfo.VideoBean> videoBeans) {
        if (videoBeans != null && videoBeans.size() > 0) {
            MicroClassInfo.VideoBean bean = videoBeans.get(position);
            video.setUp(DesignForumApplication.HOST + "images/Course/" + bean.getVideoLink(), bean.getVideoText(), JzvdStd.NORMAL_ORIENTATION);
            try {
                Glide.with(video.thumbImageView).load(DesignForumApplication.HOST + "images/Course/" + ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info.get().getCourseImage()).into(video.thumbImageView); // ???????????????????????????
            } catch (NullPointerException e) {
                Log.e(TAG, "onFocusVideoPositionChanged: ", e);
            }
        }
    }

    @BindingAdapter({"vpFragmentList", "context"})
    public static void setVpAdapter(ViewPager vp, List<Fragment> fragmentList, Context context) {
        TabLayout tab = ((View) vp.getParent()).findViewById(R.id.tab_layout);
        vp.setAdapter(new BaseFragmentPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager(), fragmentList));
        tab.setupWithViewPager(vp);
    }

    /**
     * ?????????????????????id?????????????????????????????????????????????
     *
     * @param v "???????????????????????????"
     */
    public static void changeExpandLayout(View v) {
        View view = (View) v.getParent();
        ExpandLayout layout = view.findViewById(R.id.expand_layout);
        layout.toggleExpand();
    }

    public static void onLikeClick(View img) {
        try {
            boolean isLike = (boolean) img.getTag(R.integer.img_tag);
            like(!isLike, img);
        } catch (NullPointerException e) {
            Log.e(TAG, "onCollectClick: ", e);
            like(true, img);
        }
    }

    public static void onCollectClick(View img) {
        try {
            boolean isCollect = (boolean) img.getTag(R.integer.img_tag);
            collect(!isCollect, img);
        } catch (NullPointerException e) {
            Log.e(TAG, "onCollectClick: ", e);
            collect(true, img);
        }
    }

    // state???true?????????????????????????????????????????????????????????????????????
    private static void like(boolean state, View img) {
        if (state) {
            // ????????????
            MicroClassInfo info = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info.get();
            if (info != null) {
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                if (DesignForumApplication.mainBinder.getUserInfo().getCourse_like().contains(info.getId())) {
                    Glide.with(img).load(R.drawable.ic_like_m_c_rad).into((ImageView) img);
                    img.setTag(R.integer.img_tag, true);
                } else {
                    // ???????????????????????????
                    Observable
                            .create(new ObservableOnSubscribe<Integer>() {
                                @Override
                                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                    // TODO : ????????????
                                    String url = DesignForumApplication.HOST + "Course/courseLike";
                                    FormBody body = new FormBody.Builder()
                                            .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                                            .add("targetId", String.valueOf(info.getId()))
                                            .build();

                                    Request request = new Request.Builder()
                                            .url(url)
                                            .post(body)
                                            .build();

                                    Response response = DesignForumApplication.client.newCall(request).execute();
                                    if (response.isSuccessful() && response.body() != null) {
                                        JSONObject obj = JSON.parseObject(response.body().string());
                                        int code = obj.getInteger("code");
                                        if (code == 0) {
                                            // success
                                            emitter.onNext(code);
                                        } else {
                                            emitter.onError(new Exception("?????????????????????; json code = " + code));
                                        }
                                    } else {
                                        emitter.onError(new Exception("?????????????????????; response = " + response));
                                    }
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class))))
                            .subscribe(new Consumer<Integer>() {
                                @Override
                                public void accept(Integer integer) throws Exception {

                                    Glide.with(img).load(R.drawable.ic_like_m_c_rad).into((ImageView) img);
                                    img.setTag(R.integer.img_tag, true);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e(TAG, "Error: ", throwable);
                                }
                            });
                }
            }
        } else {
            // ????????????
            Glide.with(img).load(R.drawable.ic_like_micro_class).into((ImageView) img);
            img.setTag(R.integer.img_tag, false);
        }
    }

    private static void collect(boolean state, View img) {
        if (state) {
            // ????????????
            MicroClassInfo info = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info.get();
            if (info != null) {
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                if (DesignForumApplication.mainBinder.getUserInfo().getCourse_collect().contains(info.getId())) {
                    Glide.with(img).load(R.drawable.ic_collect_m_c_rad).into((ImageView) img);
                    img.setTag(R.integer.img_tag, true);
                } else {
                    // ???????????????????????????
                    Observable
                            .create(new ObservableOnSubscribe<Integer>() {
                                @Override
                                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                    // TODO : ????????????
                                    String url = DesignForumApplication.HOST + "Course/collectSize";
                                    FormBody body = new FormBody.Builder()
                                            .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                                            .add("targetId", String.valueOf(info.getId()))
                                            .build();

                                    Request request = new Request.Builder()
                                            .url(url)
                                            .post(body)
                                            .build();

                                    Response response = DesignForumApplication.client.newCall(request).execute();
                                    if (response.isSuccessful() && response.body() != null) {
                                        JSONObject obj = JSON.parseObject(response.body().string());
                                        int code = obj.getInteger("code");
                                        if (code == 0) {
                                            // success
                                            emitter.onNext(code);
                                        } else {
                                            emitter.onError(new Exception("?????????????????????; json code = " + code));
                                        }
                                    } else {
                                        emitter.onError(new Exception("?????????????????????; response = " + response));
                                    }
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class))))
                            .subscribe(new Consumer<Integer>() {
                                @Override
                                public void accept(Integer integer) throws Exception {
                                    Glide.with(img).load(R.drawable.ic_collect_m_c_rad).into((ImageView) img);
                                    img.setTag(R.integer.img_tag, true);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e(TAG, "Error: ", throwable);
                                    if (throwable instanceof JSONException) {
                                        Toast.makeText(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class), "????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        } else {
            // ????????????
            Glide.with(img).load(R.drawable.ic_collect_micro_class).into((ImageView) img);
            img.setTag(R.integer.img_tag, false);
        }
    }

    /**
     * ??????????????????
     *
     * @param view
     * @param type
     */
    @BindingAdapter("initImageView")
    public static void initImageView(View view, int type) {
        switch (type) {
            case LIKE: {
                MicroClassInfo info = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info.get();
                if (info != null) {
                    // ???????????????
                    if (DesignForumApplication.mainBinder.getUserInfo().getCourse_like().contains(info.getId())) {
                        Glide.with(view).load(R.drawable.ic_like_m_c_rad).into((ImageView) view);
                        view.setTag(R.integer.img_tag, true);
                    } else {
                        Glide.with(view).load(R.drawable.ic_like_micro_class).into((ImageView) view);
                        view.setTag(R.integer.img_tag, false);
                    }
                }
                break;
            }
            case COLLECT: {
                MicroClassInfo info = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM().info.get();
                if (info != null) {
                    // ???????????????
                    if (DesignForumApplication.mainBinder.getUserInfo().getCourse_collect().contains(info.getId())) {
                        Glide.with(view).load(R.drawable.ic_collect_m_c_rad).into((ImageView) view);
                        view.setTag(R.integer.img_tag, true);
                    } else {
                        Glide.with(view).load(R.drawable.ic_collect_micro_class).into((ImageView) view);
                        view.setTag(R.integer.img_tag, false);
                    }
                }
                break;
            }
            default:
        }
    }

    public static final int LIKE = 1;
    public static final int COLLECT = 2;

    @BindingAdapter({"setInputCommentListener"})
    public static void setInputCommentListener(EditText et, TextView.OnEditorActionListener listener) {
        et.setOnEditorActionListener(listener);
    }

    public static final TextView.OnEditorActionListener INPUT_COMMENT_LISTENER = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                MicroClassActivity activity = ActivityManagerUtil.findActivityByClass(MicroClassActivity.class);
                MicroClassActivityViewModel microClassVM = activity.getBinding().getMicroClassVM();
                ProgressDialog dialog = new ProgressDialog(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("??????????????????");
                Observable
                        .empty()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                dialog.show();
                            }
                        })
                        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                        .subscribe();

                Observable
                        .create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                // HOST + comment/insComment
                                // userId?????????id
                                // discussType???3????????????
                                // parentId?????????id
                                // discussText??????????????? ??????????????????50
                                String url = DesignForumApplication.HOST + "comment/insComment";/*;jsessionid=" + DesignForumApplication.mainBinder.getLocalJSessionId();*/

                                FormBody body = new FormBody.Builder()
                                        .add("userId", String.valueOf(DesignForumApplication.mainBinder.getUserInfo().getId()))
                                        .add("discussType", "3")
                                        .add("parentId", String.valueOf(microClassVM.info.get().getVideo().get(microClassVM.focusVideoPosition.get()).getId()))
                                        .add("discussText", v.getText().toString())
                                        .build();

                                Request request = new Request.Builder()
                                        .post(body)
                                        .url(url)
                                        .build();

                                Response response = DesignForumApplication.client.newCall(request).execute();
                                if (response.isSuccessful() && response.body() != null) {
                                    String json = response.body().string();
                                    int code = JSON.parseObject(json).getInteger("code");
                                    if (code == 0) {
                                        // ??????
                                        emitter.onNext(code);
                                    } else if (code == 1) {
                                        // ??????
                                        emitter.onError(new Exception("????????????"));
                                    } else if (code == 7) {
                                        // ????????????
                                        emitter.onError(new Exception("????????????"));
                                    }
                                } else {
                                    emitter.onError(new Exception("??????????????????" + response));
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                // ????????????
                                dialog.dismiss();
                                Toast.makeText(activity, "????????????", Toast.LENGTH_SHORT).show();
                                v.setText("");// ????????????
                                v.clearFocus();

                                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                Observable
                                        .timer(1, TimeUnit.SECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnNext(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long aLong) throws Exception {
                                                FragmentManagerUtil.findFragmentByClass(CommentFragment.class).getBinding().getCommentVM().updateData(); // ?????????????????????
                                            }
                                        })
                                        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                                        .subscribe();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "error: ", throwable);
                                dialog.dismiss();
                                Toast.makeText(activity, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;
            }
            return false;
        }
    };

    private static class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList;

        private BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0: {
                    return "????????????";
                }
                case 1: {
                    return "??????";
                }
            }
            return super.getPageTitle(position);
        }
    }
}
