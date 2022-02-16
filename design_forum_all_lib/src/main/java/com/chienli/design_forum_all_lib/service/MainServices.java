package com.chienli.design_forum_all_lib.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.chienli.design_forum_all_lib.receiver.AlarmReceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainServices extends Service {

    private static final String TAG = "MainServices";
    private static final String LOCAL_USER_INFO_FILE_NAME = "local_user_info";

    private UserInfo localUserInfo;

    private MainBinder mainBinder;

    private String JSessionId;

    private boolean isLogin = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mainBinder == null) {
            mainBinder = new MainBinder();
        }
        return mainBinder;
    }

    private static final long triggerAtTime = 1000 * 60 * 20;// 20分钟
    private static boolean isWait = true;

    @Override
    public void onCreate() {
        if (mainBinder == null) {
            mainBinder = new MainBinder();
        }
        File files = getApplication().getFilesDir();
        if (new File(files, LOCAL_USER_INFO_FILE_NAME).exists()) {
            new Thread(() -> {
                mainBinder.readData();
                Log.e(TAG, "FileFound: " + files.getAbsolutePath() + "/" + LOCAL_USER_INFO_FILE_NAME);
                if (localUserInfo != null) {
                    String historyTime = localUserInfo.getHistory_time();
                    String id = String.valueOf(localUserInfo.getId());
                    if (!TextUtils.isEmpty(historyTime) && !TextUtils.isEmpty(id)) {
                        int code = mainBinder.autoLogin();
                        Log.e(TAG, "onCreate: autoLogin code = " + code);
                        if (code == 0) {
                            // 成功
                            // emmm,没啥好做的
                        } else {
                            // 失败
                            localUserInfo = null; // 置空就是登出了
                        }
                    }
                }
            }).start();
        }
        isWait = true;
        super.onCreate();
        // 开启新线程刷新
        new Thread(() -> {
            while (isWait) {
                try {
                    Thread.sleep(triggerAtTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainBinder.autoLogin();
            }
        }).start();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(() -> Log.d("MainServices", "executed at " + new Date().toString())).start();
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    public void onDestroy() {
        mainBinder.saveData();
        isWait = false;
        super.onDestroy();
    }

    public class MainBinder extends Binder {

        public void saveData() {
            String json;
            BufferedWriter writer = null;
            try {
                json = JSON.toJSONString(localUserInfo);
                writer = new BufferedWriter(new OutputStreamWriter(getApplication().openFileOutput(LOCAL_USER_INFO_FILE_NAME, MODE_PRIVATE)));
                writer.write(json);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        public void readData() {
            BufferedReader reader;
            StringBuilder json = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(getApplication().openFileInput(LOCAL_USER_INFO_FILE_NAME)));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                localUserInfo = JSON.parseObject(json.toString(), UserInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 登陆
         *
         * @param info 该传入的UserInfo只需要知道用户名和用户密码
         * @return 返回0为正确，返回1或其他为错误
         */
        public int login(UserInfo info) {
            try {
                String url = DesignForumApplication.HOST + "VerifyController/login";
                FormBody body = new FormBody.Builder()
                        .add("user_phone", info.getAccount())
                        .add("user_password", info.getPassword())
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = DesignForumApplication.client.newCall(request).execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String json = response.body().string();
                        int code = Integer.parseInt(String.valueOf(JSON.parseObject(json).get("code")));
                        if (code == 0) {
                            isLogin = true;
                            String content = JSON.parseObject(json).getString("content");
                            localUserInfo = JSON.parseObject(content, UserInfo.class); // 保存数据到缓存
                            JSessionId = JSON.parseObject(content).getString("JSESSIONID");
                            saveData(); // 保存数据到硬盘
                        }
                        return code;
                    }
                }
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        }

        /**
         * 自动登陆，发送缓存中的时间戳信息以及ID（这个ID是指账号嘛），然后返回状态码，再然后根据状态码来决定
         * 是开启登录界面还是获取当前的JSessionId
         * <p>
         * 一般的访问流程
         * 1. 获取系统中存在的JSessionId
         * 1.1 为null，说明没有登陆，先登陆
         * 1.2 不为null，说明有缓存，但是时间未知，一般来说应用刚启动会获取到最新的JSessionId
         * 1.2.1 访问成功，获取到正确的资源
         * 1.2.2 访问不成功，说明JSessionId过期，重新再调用自动登陆
         * 1.2.2.1 返回的状态码为0，登陆成功，重新用getLocalJSessionId()获取Id访问
         * 1.2.2.2 返回的状态码为1，登陆失败，直接跳转到登陆界面
         *
         * @return 状态码 0=成功 1=失败
         */
        public int autoLogin() {
            if (localUserInfo == null) return 1;
            // https://www.cyhfwq.top/designForum/VerifyController/selfLogin?id=1&history_time=1
            try {
                String url = DesignForumApplication.HOST + "VerifyController/selfLogin";
                FormBody body = new FormBody.Builder()
                        .add("id", String.valueOf(localUserInfo.getId()))
                        .add("history_time", localUserInfo.getHistory_time())
                        .build();
                Log.e(TAG, "autoLogin: id = " + String.valueOf(localUserInfo.getId()));
                Log.e(TAG, "autoLogin: history_time = " + localUserInfo.getHistory_time());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = DesignForumApplication.client.newCall(request).execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String json = response.body().string();
                        int code = Integer.parseInt(String.valueOf(JSON.parseObject(json).get("code")));
                        if (code == 0) {
                            isLogin = true;
                            String content = JSON.parseObject(json).getString("content");
                            JSessionId = JSON.parseObject(content).getString("JSESSIONID");
                            Log.e(TAG, "autoLogin: JSESSIONID = " + JSessionId);
                        } else {
                            Log.e(TAG, "autoLogin: 登陆失败 ");
                            isLogin = false;
                            logout();
                        }
                        return code;
                    }
                }
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        }

        /**
         * UserInfo置为null
         * JSessionId置为null
         */
        public void logout() {
            localUserInfo = null;
            JSessionId = null;
            isLogin = false;
            saveData(); // 保证内存和磁盘中数据的一致
        }

        /**
         * 注册，
         * <p>
         * 返回:
         * code:
         * 0: 注册成功
         * 1:手机号已注册
         * 2. 用户名已存在
         * <p>
         * /designForum/VerifyController/register
         * <p>
         * 发送:
         * user_phone: 手机号
         * user_name: 用户名(昵称)
         * user_password: 用户密码
         * user_status: 1 定死身份 1:学生 0:老师
         *
         * @param info 该传入的UserInfo只需要知道用户名和用户密码
         * @return 返回0为正确，返回1为错误
         */
        public int signIn(UserInfo info) {
            return 0;
        }

        /**
         * @return 返回缓存中的UserInfo，返回的只是副本，修改是没有用哒
         */
        public UserInfo getUserInfo() {
            try {
                return localUserInfo.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * 是否为登陆状态的应该区分一下
         *
         * @return 为null就是没有登陆啊
         */
        public boolean isLogin() {
            return isLogin;
        }

        /**
         * 此处获取的JSessionId，只应该用于注册
         * 想要获取缓存中现有的JSessionId，应该使用 getLocalJSessionId();
         *
         * @return 获取注册专用的JSessionId
         */
        public String getJSessionId() {
            try {
                Request request = new Request.Builder()
                        .url(DesignForumApplication.HOST + "VerifyController/getSession")
                        .build();

                Response resp = DesignForumApplication.client.newCall(request).execute();
                if (resp.isSuccessful()) {
                    if (resp.body() != null) {
                        String body = resp.body().string();
                        JSessionId = JSON.parseObject(body).getJSONObject("content").getString("JSESSIONID");
                        return JSessionId;
                    }
                } else {
                    DesignForumApplication.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                DesignForumApplication.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), "网络连接出错", Toast.LENGTH_SHORT).show();
                    }
                });
                JSessionId = null;
            } catch (Exception e) {
                Log.e(TAG, "貌似是网络验证出错啊", e);
            }
            return JSessionId;
        }

        public String getLocalJSessionId() {
            return JSessionId;
        }

        /**
         * @param info 此处传入的UserInfo必须是从binder中获取，修改过的，手动new的会导致信息覆盖
         */
        public void updateUserInfo(UserInfo info) {
            try {
                localUserInfo = info.clone();
                saveData(); // 保存数据到硬盘
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }
}
