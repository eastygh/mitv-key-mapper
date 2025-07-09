package ru.easty.andoird.tvkeymapper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.easty.andoird.tvkeymapper.R;
import ru.easty.andoird.tvkeymapper.model.AppEntry;

public class AppListAdapter extends ArrayAdapter<AppEntry> {

    public AppListAdapter(@NonNull Context context, @NonNull List<AppEntry> apps) {
        super(context, 0, apps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createRow(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createRow(position, convertView, parent);
    }

    private View createRow(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(getContext(), R.layout.app_spinner_item, null);
        AppEntry entry = getItem(position);
        if (entry != null) {
            ((ImageView) view.findViewById(R.id.app_icon)).setImageDrawable(entry.icon);
            ((TextView) view.findViewById(R.id.app_label)).setText(entry.label);
        }
        return view;
    }
}

