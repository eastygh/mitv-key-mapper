package ru.easty.andoird.tvkeymapper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class ButtonOverrideService extends AccessibilityService {

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d("KeyEvent", "Pressed: " + event.getKeyCode());
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_BUTTON_1: // возможно Netflix
//                case KeyEvent.KEYCODE_PROG_RED:
//                case KeyEvent.KEYCODE_F1:
//                    launchApp("com.google.android.youtube.tv"); // Пример: YouTube
//                    return true;
//            }
//        }
        return super.onKeyEvent(event);
    }

    private void launchApp(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("ButtonOverrideService", "Accessibility Service connected!");
    }

}

