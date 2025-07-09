package ru.easty.andoird.tvkeymapper.service;

import static ru.easty.andoird.tvkeymapper.consts.Consts.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatMonitorService extends Service {
    private static final String PREFS = "tvkey_prefs";
    private static final int NOTIFICATION_ID = 1;
    private Process logcatProcess;
    private Thread logThread;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel = new NotificationChannel(
                "tvlog",
                "TV Key Mapper Log",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "tvlog")
                .setContentTitle("TV Key Mapper")
                .setContentText("Listening for key presses...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);

        startLogcatWatcher();
    }

    private Notification buildNotification() {
        String channelId = "tvkey_logcat_channel";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "TV Key Mapper Service",
                NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("TVKeyMapper Running")
                .setContentText("Monitoring hardware keys via logcat…")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .build();
    }

    private void startLogcatWatcher() {
        if (logThread != null && logThread.isAlive()) return;

        logThread = new Thread(() -> {
            boolean needsRestart = false;

            while (checkSelfPermission(android.Manifest.permission.READ_LOGS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "READ_LOGS permission not granted. Retrying in 10 seconds...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    return;
                }
                needsRestart = true;
            }

            if (needsRestart) {
                Log.i(TAG, "Permission granted. Restarting process...");
                System.exit(0);
            }

            try {
                Log.i(TAG, "Starting logcat monitoring...");
                logcatProcess = Runtime.getRuntime().exec("logcat -T 1 -v time WindowManager:I *:S");
                BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
                String line;
                while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
                    if (line.contains("interceptKeyBeforeQueueing")) {
                        if (line.contains("netflix key")) {
                            Log.d(TAG, "Detected Netflix key press");
                            launchMappedApp("key_netflix_package");
                        } else if (line.contains("keycode_button2") || line.contains("livechannel")) {
                            Log.d(TAG, "Detected Live TV key press");
                            launchMappedApp("key_live_package");
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading logcat", e);
                new Handler(Looper.getMainLooper()).postDelayed(this::startLogcatWatcher, 10000);
            }
        });
        logThread.start();
    }

    private void launchMappedApp(String key) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String packageName = prefs.getString(key, null);
        if (packageName != null) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Log.w(TAG, "Mapped package not found: " + packageName);
            }
        } else {
            Log.w(TAG, "No app mapped for key: " + key);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logcatProcess != null) logcatProcess.destroy();
        if (logThread != null) logThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}