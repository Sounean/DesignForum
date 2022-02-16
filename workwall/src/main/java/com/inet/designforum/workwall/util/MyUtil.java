package com.inet.designforum.workwall.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyUtil {
    private static final String TAG = "【MyUtil】";

    // 日期时间转换，得到上传日期
    public static String getUpdateTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s", Locale.getDefault());
        String format = null;
        try {
            Date upDate = sdf.parse(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            format = simpleDateFormat.format(upDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "MyOverviewUtil.getUpdateTime 日期格式转换错误");
        }
        return format;
    }

    // 得到比较后的时间
    public static String getCompareTime(String pattern, String time) {
        if (time == null) {
            Log.e(TAG, "json中的日期字符串未获取到");
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            Date upDate = sdf.parse(time);  // 上传时间
            // 得到时间间隔
            long timeSpace = System.currentTimeMillis() - upDate.getTime();
            int daySpace = 60 * 60 * 1000 * 24;
            long days = timeSpace / daySpace;

            int hourSpace = 60 * 60 * 1000;
            long hours = (timeSpace - days * daySpace) / hourSpace;

            int minuteSpace = 60 * 1000;
            long minutes = (timeSpace - days * daySpace - hours * hourSpace) / minuteSpace;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String format = simpleDateFormat.format(upDate);

            if (days == 0 && hours == 0) {
                return minutes + "分钟前";
            }
            if (days == 0) {
                return hours + "小时前";
            }
            if (days > 0 && days <= 3) {
                return days + "天前";
            }
            if (days > 3) {
                return format;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "MyOverviewUtil.getCompareTime 日期格式转换错误");
        }
        Log.e(TAG, "json中的日期字符串未获取到");
        return "";
    }
}
