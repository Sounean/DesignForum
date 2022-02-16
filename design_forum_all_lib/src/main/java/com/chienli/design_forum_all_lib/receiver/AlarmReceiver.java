package com.chienli.design_forum_all_lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chienli.design_forum_all_lib.service.MainServices;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainServices.class);
        context.startService(i);
    }
}
