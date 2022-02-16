package com.inet.designforum.workwall.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    // Request请求
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    // Post请求
    public static void sendOkHttpPost(String address, Map<String, String> map, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();

        Set<Map.Entry<String, String>> set = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            builder.add(key, value); //将Map中的键值对作为表单内容添加进去
        }

        // 构建出表单
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
