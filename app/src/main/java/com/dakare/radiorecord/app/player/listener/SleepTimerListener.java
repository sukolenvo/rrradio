package com.dakare.radiorecord.app.player.listener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.sleep_mode.SleepMode;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class SleepTimerListener implements IPlayerStateListener, Closeable, Runnable, SharedPreferences.OnSharedPreferenceChangeListener {

    private final Handler handler;
    private final Context context;
    private boolean playing;

    public SleepTimerListener(final Context context) {
        handler = new Handler();
        this.context = context;
        initHandler();
        PreferenceManager.getInstance(context).registerChangeListener(this);
    }

    private void initHandler() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        SleepMode sleepMode = preferenceManager.getSleepMode();
        if (sleepMode != SleepMode.OFF) {
            long delay = sleepMode.nextSleepIn(preferenceManager.getSleepModeTs(), preferenceManager.getSleepSettings(sleepMode));
            if (delay < TimeUnit.MINUTES.toMillis(1)) {
                PreferenceManager.getInstance(context).setSleepMode(SleepMode.OFF);
            } else {
                handler.postDelayed(this, delay);
            }
        }
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.PLAY) {
            playing = true;
        } else {
            playing = false;
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        //Nothing to do
    }

    @Override
    public void close() {
        handler.removeCallbacks(this);
        PreferenceManager.getInstance(context).unregisterChangeListener(this);
    }

    @Override
    public void run() {
        if (playing) {
            context.startService(new Intent(context, PlayerService.class).setAction(NotificationListener.ACTION_STOP));
        }
        Toast.makeText(context, R.string.sleep_timer_toast, Toast.LENGTH_LONG).show();
        PreferenceManager.getInstance(context).setSleepMode(SleepMode.OFF);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (PreferenceManager.SLEEP_MODE_KEY.equals(key)) {
            handler.removeCallbacks(this);
            initHandler();
        }
    }
}
