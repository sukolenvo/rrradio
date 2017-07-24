package com.dakare.radiorecord.app.player.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import java.io.Closeable;

public class LockListener implements IPlayerStateListener, Closeable {

    private final PowerManager.WakeLock wakeLock;
    private final WifiManager.WifiLock wifiLock;

    public LockListener(final Context context) {
        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RecordLockListener");
        wakeLock.setReferenceCounted(false);
        wifiLock = ((WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock("RecordLockListener");
        wifiLock.setReferenceCounted(false);
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.PLAY) {
            wakeLock.acquire();
            wifiLock.acquire();
        } else {
            wakeLock.release();
            wifiLock.release();
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        //Nothing to do
    }

    @Override
    public void close() {
        wifiLock.release();
        wakeLock.release();
    }
}
