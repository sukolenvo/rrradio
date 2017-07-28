package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.listener.remote.NotificationRemote;
import com.dakare.radiorecord.app.player.listener.remote.NotificationRemoteFactory;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.view.theme.Theme;


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
    private Notification notification;
    private final NotificationManager notificationManager;
    private boolean foreground;
    private final Theme theme;

    public NotificationListener(final Service service) {
        this.service = service;
        theme = PreferenceManager.getInstance(service).getTheme();
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(RecordApplication.getInstance(), PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notification = new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(service, CONTENT_CODE, intent, 0))
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.STOP) {
            service.stopForeground(true);
            foreground = false;
        } else {
            NotificationRemote notificationRemote = NotificationRemoteFactory.create(service.getPackageName(), notification);
            notificationRemote.updateTitle(message);
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled()) {
                notificationRemote.setImage(theme.getStationIcon(message.getPlaying().getStation()));
            }
            notificationRemote.setPlaying(message.getState() == PlayerState.PLAY);
            if (foreground) {
                notificationRemote.notify(notificationManager, 1);
            } else {
                notificationRemote.setupIntents(service);
                service.startForeground(1, notification);
                foreground = true;
            }
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        if (image != null) {
            NotificationRemote notificationRemote = NotificationRemoteFactory.create(service.getPackageName(), notification);
            notificationRemote.setImage(image);
            notificationRemote.notify(notificationManager, 1);
        }
    }

}
