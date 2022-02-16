package com.chienli.micro_class.util;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Stack;

public class ActivityManagerUtil {
    private static final Stack<WeakReference<Activity>> STACK = new Stack<>();

    private ActivityManagerUtil() throws IllegalAccessException {
        throw new IllegalAccessException("this ActivityManagerUtil class can't init");
    }

    public static void add(Activity activity) {
        STACK.add(new WeakReference<>(activity));
    }

    public static void remove(Activity activity) {
        for (WeakReference<Activity> a : STACK) {
            if (a.get().equals(activity)){
                STACK.remove(a);
                break;
            }
        }
    }

    @NonNull
    public static <T extends Activity> T findActivityByClass(Class<T> activityClass){
        for (WeakReference<Activity> a : STACK) {
            if (a.get().getClass().equals(activityClass)){
                return (T) a.get();
            }
        }
        return null;// 多数情况下是不会为null的
    }
}
