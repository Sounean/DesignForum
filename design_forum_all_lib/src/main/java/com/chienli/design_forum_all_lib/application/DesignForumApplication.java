package com.chienli.design_forum_all_lib.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.chienli.design_forum_all_lib.receiver.AlarmReceiver;
import com.chienli.design_forum_all_lib.service.MainServices;

import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DesignForumApplication extends Application {

    // 公开的数据，其他的模块调用的时候注意禁止修改
    public static final String HOST = "https://www.cyhfwq.top/designForum/";
    public static OkHttpClient client;
    public static MainServices.MainBinder mainBinder;
    public static Handler handler;// 发送简单的Toast信息，在UI线程中进行操作

    private static final String TAG = "DesignForumApplication";

    public DesignForumApplication() {
        super();
        Log.e(TAG, "DesignForumApplication");

    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mainBinder = (MainServices.MainBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mainBinder = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mContent = new WeakReference<>(getApplicationContext());
        handler = new DesignForumHandle(getApplicationContext());
        client = new OkHttpClient.Builder()
                .connectTimeout(2000L, TimeUnit.MILLISECONDS)
                .callTimeout(2000L, TimeUnit.MILLISECONDS)
                .cookieJar(new CookieJar() {
                    //Cookie缓存区
                    private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl arg0, @NonNull List<Cookie> arg1) {
                        //移除相同的url的Cookie
                        String host = arg0.host();
                        List<Cookie> cookiesList = cookiesMap.get(host);
                        if (cookiesList != null){
                            cookiesMap.remove(host);
                        }
                        //再重新天添加
                        cookiesMap.put(host, arg1);
                    }

                    @NonNull
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl arg0) {
                        List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                        //注：这里不能返回null，否则会报NULLException的错误。
                        //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                        return cookiesList != null ? cookiesList : new ArrayList<>();
                    }
                })
                .build();
        LitePal.initialize(this);
        // 随缘启动把，我已经迷茫了
        bindService(new Intent(this, MainServices.class), serviceConn, BIND_AUTO_CREATE);
        Log.e(TAG, "bindService");
        // 注册AlarmReceiver，保证Service在后台能够定时刷新数据
        registerReceiver(new AlarmReceiver(), new IntentFilter());

    }

    /**
     * 不要用这个玩意，它只会在模拟器上被执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        if (mainBinder != null) {
            mainBinder.saveData();// 保存数据
        }
        super.onTrimMemory(level);
    }

    static class DesignForumHandle extends Handler {
        private WeakReference<Context> contextWeakReference;

        private DesignForumHandle(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private static WeakReference<Context> mContent;

    private static class AppWeakReference extends WeakReference<Context> {

        private Context application;

        public AppWeakReference(Context referent) {
            super(referent);
            application = referent;
        }
    }

    public static Context getMyAppContext() {
        return mContent.get();
    }

}
