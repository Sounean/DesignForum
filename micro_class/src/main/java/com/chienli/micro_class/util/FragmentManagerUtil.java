package com.chienli.micro_class.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Stack;

public class FragmentManagerUtil {
    private static final Stack<WeakReference<Fragment>> STACK = new Stack<>();

    private FragmentManagerUtil() throws IllegalAccessException {
        throw new IllegalAccessException("this FragmentManagerUtil class can't init");
    }

    public static void add(Fragment fragment) {
        STACK.add(new WeakReference<>(fragment));
    }

    public static void remove(Fragment fragment) {
        for (WeakReference<Fragment> f : STACK) {
            if (f.get().equals(fragment)) {
                STACK.remove(f);
                break;
            }
        }
    }

    @NonNull
    public static <T extends Fragment> T findFragmentByClass(Class<T> fragmentClass) {
        for (WeakReference<Fragment> f : STACK) {
            if (f.get().getClass().equals(fragmentClass)) {
                return (T) f.get();
            }
        }
        return null;// 多数情况下是不会为null的, 没事不要去获取, 一般是用于引用数据对象或者不同层次UI之间的方法调用
    }
}
