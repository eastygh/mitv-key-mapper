package ru.easty.andoird.tvkeymapper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.easty.andoird.tvkeymapper.service.LogcatMonitorService;

public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TVKeyMapper", "InstallReceiver triggered: " + intent.getAction());
        Intent serviceIntent = new Intent(context, LogcatMonitorService.class);
        context.startForegroundService(serviceIntent);
    }
}
