package com.inet.designforum.workwall.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.inet.designforum.workwall.bean.WorkComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyContentUtil {
    private static final String TAG = "【ContentUtil】";

    // 为EditText设置TextWatcher
    public static void setTextWatcher(Context context, EditText etInputComment) {
        etInputComment.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = etInputComment.getSelectionStart();
                selectionEnd = etInputComment.getSelectionEnd();
                if (s.length() > 50) {
                    s.delete(selectionStart - 1, selectionEnd);
                    etInputComment.setText(s);
                    etInputComment.setSelection(50);
                    Toast.makeText(context, "评论内容不能超过50字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // 处理作品评论的json，最终封装成List<WorkComment>
    public static void dealWorkCommentJson(String address, Handler handler, List<WorkComment> workCommentList) {

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
                    // 第一层解析，获得状态码
                    JSONObject jsonObject1 = new JSONObject(json);
                    int code = jsonObject1.optInt("code");  //Web端定义返回的状态码
                    Log.e(TAG, "获取的code = " + code);

                    /**
                     code 是 Web端定义返回的状态码，0是成功，1是失败，3是登录时间超时，4是sessionId不正确，8是数据为空
                     */
                    if (code == 8) {
                        Log.e(TAG, "Code：8，数据为空");
                        Message msg = new Message();
                        msg.what = 11;  // 表示没有更多评论了
                        msg.arg1 = 0;   // 表示还没有评论
                        handler.sendMessage(msg);
                        return;
                    }
                    if (code == 0) {    // 0表示获取数据成功
                        // 第二层解析
                        JSONObject jsonObject2 = jsonObject1.optJSONObject("content");

                        // 第三层解析
                        JSONObject jsonObject3 = jsonObject2.optJSONObject("content");
                        int pageNum = jsonObject3.optInt("pageNum");    //拿到当前请求的页码
                        int pages = jsonObject3.optInt("pages");    // 拿到共有几页
                        int total = jsonObject3.optInt("total");
                        JSONArray list = jsonObject3.optJSONArray("list");

                        // 第四层解析，获得作品内容
                        String jsonData;
                        Gson gson = new Gson();
                        WorkComment workComment;
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject = list.getJSONObject(i);
                            jsonData = jsonObject.toString();
                            workComment = gson.fromJson(jsonData, WorkComment.class);
                            workCommentList.add(workComment); // 将对象添加
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        msg.arg1 = total;
                        handler.sendMessage(msg);   // 0表示获取数据成功
                        if (pages > pageNum) {   // 如果页码数大于当前页码，则显示加载更多发送(10)，否则显示没有更多内容了发送(20)
                            handler.sendEmptyMessage(10);
                        } else {
                            handler.sendEmptyMessage(11);
                        }
                    } else {
                        if (code == 1) {
                            Log.e(TAG, "Code：1，获取失败");
                        } else if (code == 3) {
                            Log.e(TAG, "Code：3，登录超时");
                        } else if (code == 4) {
                            Log.e(TAG, "Code：4，sessionId不正确");
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


    // 【点赞或收藏或关注或取消关注】
    public static void sendOptionInfo(String address, Map<String, String> map, Handler handler, int type) {
        HttpUtil.sendOkHttpPost(address, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(-2);   //网络异常
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code(); //HTTP请求的状态码
                //对HTTP请求状态码的处理
                if (statusCode != 200) {
                    handler.sendEmptyMessage(-1);
                    Log.e(TAG, "HTTP请求状态码为：" + statusCode + "，数据未正确获取！");
                    return;
                }
                Log.e(TAG, "HTTP请求状态码为：" + statusCode);
                if (response.body() != null) {
                    String json = response.body().string();
                    try {
                        // 解析一层，获得code
                        JSONObject jsonObject = new JSONObject(json);
                        int code = jsonObject.optInt("code");
                        if (code == 0) {
                            if (type == 1) {
                                handler.sendEmptyMessage(21);    //显示评论点赞成功
                            } else if (type == 2) {
                                handler.sendEmptyMessage(22);    //显示作品点赞成功
                            } else if (type == 3) {
                                handler.sendEmptyMessage(23);    //显示作品收藏成功
                            } else if (type == 4) {
                                handler.sendEmptyMessage(24);    //显示关注成功
                            } else if (type == 5) {
                                handler.sendEmptyMessage(25);    //显示取消关注成功
                            }
                        } else {
                            handler.sendEmptyMessage(-1);   //显示服务器正忙
                        }
                        Log.e(TAG, "MyContentUtil Code:" + code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "response.body为空");
                }
            }
        });
    }

}
