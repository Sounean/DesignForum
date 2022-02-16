package com.inet.designforum.workwall.util;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.inet.designforum.workwall.bean.WorkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyOverviewUtil {

    private static final String TAG = "【OverviewUtil】";


    // 根据模糊查询，搜索到所有有关作品的id
    public static void searchWorkIdFromKey(String address, Handler handler, List<Integer> idList) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(-2);   // 表示网络异常
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    handler.sendEmptyMessage(-1);
                    return;
                }
                String json = response.body().string(); //获得json数据
                int statusCode = response.code();   // 获得HTTP请求状态码
                Log.e(TAG, "StatusCode :" + statusCode);

                // 对HTTP请求状态码的处理
                if (statusCode != 200) {
                    Log.e(TAG, "HTTP请求状态码为：" + statusCode + "，数据未正确获取！");
                    handler.sendEmptyMessage(-1);
                    return;
                }

                try {
                    idList.clear();
                    // 第一层解析，获得状态码
                    JSONObject jsonObject1 = new JSONObject(json);
                    int code = jsonObject1.optInt("code");  //Web端定义返回的状态码
                    Log.e(TAG, "获取的code = " + code);

                    /**
                     code 是 Web端定义返回的状态码，0是成功，1是失败，3是登录时间超时，4是sessionId不正确，8是数据为空
                     */
                    if (code == 8) {
                        Log.e(TAG, "Code：8，数据为空");
                        handler.sendEmptyMessage(30);
                        return;
                    }
                    if (code == 0) {    // 0表示获取数据成功
                        // 第二层解析
                        JSONObject jsonObject2 = jsonObject1.optJSONObject("content");
                        JSONArray jsonArray = jsonObject2.optJSONArray("content");

                        JSONObject jsonObject3;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject3 = jsonArray.getJSONObject(i);
                            int id = jsonObject3.optInt("id");
                            idList.add(id);
                        }
                        Log.e(TAG, "MyOverViewUtil idList :" + idList.toString());

                        handler.sendEmptyMessage(30);  // 30表示id获取数据成功
                    } else {
                        if (code == 1) {
                            Log.e(TAG, "Code：1，获取失败");
                        } else if (code == 3) {
                            Log.e(TAG, "Code：3，登录超时");
                        } else if (code == 4) {
                            Log.e(TAG, "Code：4，sessionId不正确");
                        } else if (code == 7) {
                            Log.e(TAG, "Code: 7，参数格式错误");
                        } else {
                            Log.e(TAG, "Code：" + code + "未知错误");
                        }
                        handler.sendEmptyMessage(-1);// 如果返回其他数值的状态码，则显示给用户服务器正忙
                        Log.e(TAG, "Web端服务器状态码 code :" + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // 通过作品id来进行搜索，与模糊查询一同使用
    public static void searchWorkInfoById(String address, Handler handler, List<WorkInfo> workInfoList) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(-2);   // 表示网络异常
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    handler.sendEmptyMessage(-1);   // 网络异常
                    return;
                }
                String json = response.body().string(); //获得json数据
                Log.e(TAG, "MyOverViewUtil response.body json: " + json);
                int statusCode = response.code();   // 获得HTTP请求状态码
                Log.e(TAG, "StatusCode :" + statusCode);

                // 对HTTP请求状态码的处理
                if (statusCode != 200) {
                    Log.e(TAG, "HTTP请求状态码为：" + statusCode + "，数据未正确获取！");
                    handler.sendEmptyMessage(-1);
                    return;
                }

                try {
                    // 第一层解析，获得状态码
                    JSONObject jsonObject1 = new JSONObject(json);
                    int code = jsonObject1.optInt("code");  //Web端定义返回的状态码
                    Log.e(TAG, "获取的code = " + code);

                    /**
                     code 是 Web端定义返回的状态码，0是成功，1是失败，3是登录时间超时，4是sessionId不正确，8是数据为空
                     */
                    if (code == 0) {
                        // 第二层解析
                        JSONObject jsonObject2 = jsonObject1.optJSONObject("content");

                        // 第三层解析
                        JSONArray jsonArray = jsonObject2.optJSONArray("content");
                        Log.e(TAG, "jsonArray.Length :" + jsonArray.length());

                        // 第四层解析，获得作品内容
                        String jsonData;
                        Gson gson = new Gson();
                        WorkInfo workInfo;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            jsonData = jsonObject.toString();
                            workInfo = gson.fromJson(jsonData, WorkInfo.class);
                            workInfoList.add(workInfo); // 将对象添加
                        }
                        handler.sendEmptyMessage(31);    // 0表示获取数据成功
                    } else {
                        if (code == 1) {
                            Log.e(TAG, "Code：1，获取失败");
                        } else if (code == 3) {
                            Log.e(TAG, "Code：3，登录超时");
                        } else if (code == 4) {
                            Log.e(TAG, "Code：4，sessionId不正确");
                        } else if (code == 7) {
                            Log.e(TAG, "Code: 7，参数格式错误");
                        } else {
                            Log.e(TAG, "Code：" + code + "未知错误");
                        }
                        handler.sendEmptyMessage(-1);// 如果返回其他数值的状态码，则显示给用户服务器正忙
                        Log.e(TAG, "Web端服务器状态码 code :" + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // 处理作品信息的json，最终封装成List<WorkInfo>
    public static void dealWorkInfoJson(String address, Handler handler, List<WorkInfo> workInfoList) {

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 连接失败时，用Toast提示网络异常
                handler.sendEmptyMessage(-2);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                int statusCode = response.code();   // 获得HTTP请求状态码
                Log.e(TAG, "StatusCode :" + statusCode);

                // 对HTTP请求状态码的处理
                if (statusCode != 200) {
                    Log.e(TAG, "HTTP请求状态码为：" + statusCode + "，数据未正确获取！");
                    handler.sendEmptyMessage(-1);
                    return;
                }

                // 如果返回的response.body()为空，则显示给用户服务器正忙
                if (response.body() == null) {
                    handler.sendEmptyMessage(-1);
                    return;
                }
                String json = response.body().string(); //获得json数据

                try {
                    // 第一层解析，获得状态码
                    JSONObject jsonObject1 = new JSONObject(json);
                    int code = jsonObject1.optInt("code");  //Web端定义返回的状态码
                    Log.e(TAG, "获取的code = " + code);

                    /**
                     code 是 Web端定义返回的状态码，0是成功，1是失败，3是登录时间超时，4是sessionId不正确，8是数据为空
                     */
                    if (code == 0) {
                        // 第二层解析
                        JSONObject jsonObject2 = jsonObject1.optJSONObject("content");

                        // 第三层解析
                        JSONObject jsonObject3 = jsonObject2.optJSONObject("content");
                        int pageNum = jsonObject3.optInt("pageNum");    //拿到当前请求的页码
                        int pages = jsonObject3.optInt("pages");    // 拿到共有几页

                        JSONArray list = jsonObject3.optJSONArray("list");

                        // 第四层解析，获得作品内容
                        String jsonData;
                        Gson gson = new Gson();
                        WorkInfo workInfo;
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject = list.getJSONObject(i);
                            jsonData = jsonObject.toString();
                            workInfo = gson.fromJson(jsonData, WorkInfo.class);
                            workInfoList.add(workInfo); // 将对象添加
                        }
                        handler.sendEmptyMessage(0);    // 0表示获取数据成功
                        if (pages > pageNum) {   // 如果页码数大于当前页码，则显示加载更多发送(10)，否则显示没有更多内容了发送(20)
                            handler.sendEmptyMessage(10);
                        } else {
                            handler.sendEmptyMessage(20);
                        }
                    } else {
                        if (code == 1) {
                            Log.e(TAG, "Code：1，获取失败");
                        } else if (code == 3) {
                            Log.e(TAG, "Code：3，登录超时");
                        } else if (code == 4) {
                            Log.e(TAG, "Code：4，sessionId不正确");
                        } else if (code == 8) {
                            Log.e(TAG, "Code：8，数据为空");
                        } else {
                            Log.e(TAG, "Code：" + code + "未知错误");
                        }
                        handler.sendEmptyMessage(-1);// 如果返回其他数值的状态码，则显示给用户服务器正忙
                        Log.e(TAG, "服务器状态码 code :" + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
