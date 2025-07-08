package ru.easty.andoird.tvkeymapper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.easty.andoird.tvkeymapper.service.LogcatMonitorService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LogcatMonitorService.class);
            context.startForegroundService(serviceIntent);
        }
    }

}
