package com.inet.designforum.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public class DFFragmentHandler<T extends Fragment> extends Handler {

    private WeakReference<T> fragment;
    private DFHandlerMassage handlerMassage;

    public DFFragmentHandler(T fragment) {
        this(fragment, null);
    }

    public DFFragmentHandler(T fragment, DFHandlerMassage handlerMassage) {
        this.fragment = new WeakReference<T>(fragment);
        this.handlerMassage = handlerMassage;
    }

    @Override
    public void handleMessage(Message msg) {
        if (handlerMassage != null) {
            handlerMassage.handle(msg);
        }
    }
}
