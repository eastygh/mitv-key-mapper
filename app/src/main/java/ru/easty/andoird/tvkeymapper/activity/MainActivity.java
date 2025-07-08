package ru.easty.andoird.tvkeymapper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ru.easty.andoird.tvkeymapper.R;
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

        List<String> apps = getInstalledApps();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, apps);
        netflixSpinner.setAdapter(adapter);
        liveSpinner.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedNetflix = prefs.getString("key_netflix_package", null);
        String savedLive = prefs.getString("key_live_package", null);

        if (savedNetflix != null) netflixSpinner.setSelection(apps.indexOf(savedNetflix));
        if (savedLive != null) liveSpinner.setSelection(apps.indexOf(savedLive));

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key_netflix_package", (String) netflixSpinner.getSelectedItem());
            editor.putString("key_live_package", (String) liveSpinner.getSelectedItem());
            editor.apply();
        });

        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        // Запуск сервиса при открытии
        Intent serviceIntent = new Intent(this, LogcatMonitorService.class);
        startForegroundService(serviceIntent);
    }

    private List<String> getInstalledApps() {
        List<String> packages = new ArrayList<>();
        PackageManager pm = getPackageManager();
        for (var pkg : pm.getInstalledApplications(0)) {
            if (pm.getLaunchIntentForPackage(pkg.packageName) != null) {
                packages.add(pkg.packageName);
            }
        }
        return packages;
    }
}
