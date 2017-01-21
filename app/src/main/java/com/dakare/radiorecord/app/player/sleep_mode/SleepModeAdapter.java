package com.dakare.radiorecord.app.player.sleep_mode;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

import java.util.EnumMap;
import java.util.Map;

public class SleepModeAdapter extends ArrayAdapter<SleepMode> implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final LayoutInflater inflater;
    private final EnumMap<SleepMode, SleepSettings> settings = new EnumMap<>(SleepMode.class);
    private SleepMode selected;
    private final SleepModeCallback sleepModeCallback;
    private boolean changed;

    public SleepModeAdapter(final Context context, final SleepModeCallback sleepModeCallback) {
        super(context, 0);
        this.inflater = LayoutInflater.from(context);
        this.sleepModeCallback = sleepModeCallback;
        addAll(SleepMode.values());
        PreferenceManager instance = PreferenceManager.getInstance(context);
        for (SleepMode sleepMode : SleepMode.values()) {
            settings.put(sleepMode, instance.getSleepSettings(sleepMode));
        }
        selected = instance.getSleepMode();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_sleep_mode, parent, false);
        } else {
            view = convertView;
        }
        ViewHolder.from(view);
        SleepMode item = getItem(position);
        ViewHolder.icon.setChecked(item == selected);
        ViewHolder.title.setText(item.buildTitle(getContext(), settings.get(item)));
        ViewHolder.settingsIcon.setTag(item);
        if (item.isConfigurable()) {
            ViewHolder.settingsIcon.setVisibility(View.VISIBLE);
            ViewHolder.settingsIcon.setOnClickListener(this);
        } else {
            ViewHolder.settingsIcon.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(final View v) {
        final SleepMode sleepMode = (SleepMode) v.getTag();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
                SleepSettings sleepSettings = new SleepSettings(hourOfDay, minute);
                sleepModeCallback.onModeChanged();
                settings.put(sleepMode, sleepSettings);
                notifyDataSetChanged();
                changed = true;
            }
        }, settings.get(sleepMode).getHour(), settings.get(sleepMode).getMinute(), true).show();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        selected = getItem(position);
        changed = true;
        sleepModeCallback.onModeChanged();
        notifyDataSetChanged();
    }

    public void save() {
        if (changed) {
            PreferenceManager preferenceManager = PreferenceManager.getInstance(getContext());
            for (Map.Entry<SleepMode, SleepSettings> entry : settings.entrySet()) {
                preferenceManager.setSleepSettings(entry.getKey(), entry.getValue());
            }
            preferenceManager.setSleepMode(selected);
            if (selected == SleepMode.OFF) {
                Toast.makeText(getContext(), R.string.sleep_timer_canceled, Toast.LENGTH_LONG).show();
            } else {
                long delay = selected.nextSleepIn(System.currentTimeMillis(), settings.get(selected));
                Toast.makeText(getContext(), getContext().getString(R.string.sleep_in_hint, ((int) (delay / 1000 / 3600)),
                        ((int) (delay / 1000 / 60)) % 60), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class ViewHolder {
        private static RadioButton icon;
        private static TextView title;
        private static View settingsIcon;

        private static void from(final View parent) {
            icon = (RadioButton) parent.findViewById(R.id.select_icon);
            title = (TextView) parent.findViewById(R.id.quality_text);
            settingsIcon = parent.findViewById(R.id.settings_icon);
        }
    }

}
