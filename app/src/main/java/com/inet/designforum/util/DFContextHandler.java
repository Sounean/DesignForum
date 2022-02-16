package com.inet.designforum.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public class DFContextHandler<T extends Context> extends Handler {

    private WeakReference<T> context;
    private DFHandlerMassage handlerMassage;

    public DFContextHandler(T context) {
        this(context, null);
    }

    public DFContextHandler(T context, DFHandlerMassage handlerMassage) {
        this.context = new WeakReference<T>(context);
        this.handlerMassage = handlerMassage;
    }

    @Override
    public void handleMessage(Message msg) {
        if (handlerMassage != null) {
            handlerMassage.handle(msg);
        }
    }
}
