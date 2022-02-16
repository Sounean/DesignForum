package com.inet.designforum.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Util {
    private static final String TAG = "Util";

    /**
     * 判断服务是否后台运行(但是这个方法并不好用，完全找不到)
     *
     * @param mContext  Context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = Objects.requireNonNull(activityManager)
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRun = true;
                break;
            }
        }
        Log.e(TAG, "isServiceRun: " + className + " - " + isRun);
        return isRun;
    }

    /**
     * 压缩Bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = 1.0f;
        if (width > 1024 || height > 1024) {
            scale = 0.5f;
        }
        if (width > 10240 || height > 10240) {
            scale = 0.1f;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
    }

    /**
     * 将Bitmap转换成String
     */
    public static String bitmapToStr(Bitmap bitmap) {
        return Base64.encodeToString(bitmap2Bytes(bitmap), Base64.NO_WRAP | Base64.URL_SAFE);
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String getHistoryTime() {
        return new SimpleDateFormat("yyyyHHmmss", Locale.CHINA).format(new Date());
    }


}
