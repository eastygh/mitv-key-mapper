package ru.easty.andoird.tvkeymapper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ru.easty.andoird.tvkeymapper.R;
import ru.easty.andoird.tvkeymapper.adapter.AppListAdapter;
import ru.easty.andoird.tvkeymapper.model.AppEntry;
import ru.easty.andoird.tvkeymapper.service.LogcatMonitorService;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS = "tvkey_prefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner netflixSpinner = findViewById(R.id.spinner_netflix);
        Spinner liveSpinner = findViewById(R.id.spinner_live);
        Button saveButton = findViewById(R.id.save_button);
        Button permissionButton = findViewById(R.id.permission_button);

        List<AppEntry> apps = getInstalledApps();
        AppListAdapter adapter = new AppListAdapter(this, apps);
        netflixSpinner.setAdapter(adapter);
        liveSpinner.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedNetflix = prefs.getString("key_netflix_package", null);
        String savedLive = prefs.getString("key_live_package", null);

        if (savedNetflix != null) {
            for (int i = 0; i < apps.size(); i++) {
                if (apps.get(i).packageName.equals(savedNetflix)) {
                    netflixSpinner.setSelection(i);
                    break;
                }
            }
        }

        if (savedLive != null) {
            for (int i = 0; i < apps.size(); i++) {
                if (apps.get(i).packageName.equals(savedLive)) {
                    liveSpinner.setSelection(i);
                    break;
                }
            }
        }

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key_netflix_package", ((AppEntry) netflixSpinner.getSelectedItem()).packageName);
            editor.putString("key_live_package", ((AppEntry) liveSpinner.getSelectedItem()).packageName);
            editor.apply();
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
        });

        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        Intent serviceIntent = new Intent(this, LogcatMonitorService.class);
        startForegroundService(serviceIntent);
    }

    private List<AppEntry> getInstalledApps() {
        List<AppEntry> entries = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo appInfo : apps) {
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                String label = pm.getApplicationLabel(appInfo).toString();
                Drawable icon = pm.getApplicationIcon(appInfo);
                entries.add(new AppEntry(label, appInfo.packageName, icon));
            }
        }
        return entries;
    }
}
