package com.dakare.radiorecord.app.player.sleep_mode;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

public class SleepTimerSetupDialog extends AbstractDialog implements SleepModeCallback {

    private final TextView sleepIn;

    public SleepTimerSetupDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_sleep_timer_settings);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ListView listView = (ListView) findViewById(R.id.sleep_mode_list);
        final SleepModeAdapter adapter = new SleepModeAdapter(getContext(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        sleepIn = (TextView) findViewById(R.id.sleep_in_text);
        PreferenceManager instance = PreferenceManager.getInstance(context);
        SleepMode sleepMode = instance.getSleepMode();
        if (sleepMode == SleepMode.OFF) {
            sleepIn.setVisibility(View.GONE);
        } else {
            long delay = sleepMode.nextSleepIn(instance.getSleepModeTs(), instance.getSleepSettings(sleepMode));
            sleepIn.setText(context.getString(R.string.sleep_in_hint, ((int) (delay / 1000 / 3600)), ((int) (delay / 1000 / 60)) % 60));
        }
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.save();
                dismiss();
            }
        });
    }

    @Override
    public void onModeChanged() {
        sleepIn.setVisibility(View.GONE);
    }
}
