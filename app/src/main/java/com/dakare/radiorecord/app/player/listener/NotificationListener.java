package com.dakare.radiorecord.app.player.listener;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

public class NotificationListener implements IPlayerStateListener {

    public static final String ACTION_STOP = "stop";
    public static final String ACTION_PREVIOUS = "previous";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_PLAY_PAUSE = "play_pause";
    public static final int CONTENT_CODE = 0;
    public static final int STOP_CODE = 1;
    public static final int PAUSE_CODE = 2;
    public static final int RESUME_CODE = 3;
    public static final int NEXT_CODE = 4;
    public static final int PREVIOUS_CODE = 5;

    private final Service service;
    private final NotificationManager notificationManager;
    private boolean foreground;
    private final NotificationHelper notificationHelper;

    public NotificationListener(final Service service) {
        this.service = service;
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationHelper = new NotificationHelper(service);
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.STOP) {
            service.stopForeground(true);
            foreground = false;
        } else {
            notificationHelper.updateTitle(message);
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled()) {
                notificationHelper.setImage(message.getPlaying().getStation().getNotificationStationIcon());
            }
            notificationHelper.setPlaying(message.getState() == PlayerState.PLAY);
            if (foreground) {
                updateNotification();
            } else {
                service.startForeground(1, notificationHelper.getBuilder().build());
                foreground = true;
            }
        }
    }

    private void updateNotification() {
        try {
            notificationManager.notify(1, notificationHelper.getBuilder().build());
        } catch (Exception e) {
            Log.e("NotificationListener", "Can't update notification", e);
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        if (image != null) {
            notificationHelper.setImage(image);
            updateNotification();
        }
    }

}
