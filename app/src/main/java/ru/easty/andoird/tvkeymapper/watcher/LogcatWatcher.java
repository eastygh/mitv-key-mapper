package ru.easty.andoird.tvkeymapper.watcher;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatWatcher {

    public interface OnKeyDetectedListener {
        void onNetflixKey();
        void onLiveKey();
    }

    private Process logcatProcess;
    private final OnKeyDetectedListener listener;

    public LogcatWatcher(OnKeyDetectedListener listener) {
        this.listener = listener;
    }

    public void startWatching() {
        new Thread(() -> {
            try {
                logcatProcess = Runtime.getRuntime().exec("logcat");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(logcatProcess.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("interceptKeyBeforeQueueing")) {
                        if (line.contains("netflix key")) {
                            Log.d("LogcatWatcher", "Netflix key detected");
                            listener.onNetflixKey();
                        } else if (line.contains("keycode_button2") || line.contains("livechannel")) {
                            Log.d("LogcatWatcher", "Live TV key detected");
                            listener.onLiveKey();
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("watcher","Exception on logcat reader",e);
            }
        }).start();
    }

    public void stopWatching() {
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
    }
}
