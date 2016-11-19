package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


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
    private final RemoteViews collapsed;
    private final RemoteViews expanded;
    private final Notification notification;
    private final NotificationManager notificationManager;
    private boolean foreground;

    public NotificationListener(final Service service) {
        this.service = service;
        Intent intent = new Intent(service, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        collapsed = new RemoteViews(service.getPackageName(), R.layout.notification_collapsed);
        expanded = new RemoteViews(service.getPackageName(), R.layout.notification_expanded);
        notification = new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(service, CONTENT_CODE, intent, 0))
                .setCustomBigContentView(expanded)
                .setCustomContentView(collapsed)
                .setOngoing(true)
                .build();
        Intent stopIntent = new Intent(service, PlayerService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(service, STOP_CODE, stopIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        expanded.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        Intent pauseIntent = new Intent(service, PlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(service, PAUSE_CODE, pauseIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        expanded.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        Intent resumeIntent = new Intent(service, PlayerService.class);
        resumeIntent.setAction(ACTION_RESUME);
        PendingIntent resumePending = PendingIntent.getService(service, RESUME_CODE, resumeIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        expanded.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        Intent nextIntent = new Intent(service, PlayerService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(service, NEXT_CODE, nextIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        expanded.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        Intent previousIntent = new Intent(service, PlayerService.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent previousPending = PendingIntent.getService(service, PREVIOUS_CODE, previousIntent, 0);
        expanded.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.STOP) {
            service.stopForeground(true);
            foreground = false;
        } else {
            PlaylistItem playlistItem = message.getPlaying();
            collapsed.setTextViewText(R.id.text_media_title, message.getSong() == null ? buildTitle(playlistItem.getTitle(), playlistItem.getSubtitle()) : message.getSong());
            expanded.setTextViewText(R.id.text_media_title, message.getSong() == null ? buildTitle(playlistItem.getTitle(), playlistItem.getSubtitle()) : buildTitle(message.getArtist(), message.getSong()));
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled()) {
                collapsed.setImageViewResource(R.id.image_media_preview, playlistItem.getStation().getIcon());
                expanded.setImageViewResource(R.id.image_media_preview, playlistItem.getStation().getIcon());
            }
            if (message.getState() == PlayerState.PLAY) {
                collapsed.setViewVisibility(R.id.button_media_play, View.GONE);
                collapsed.setViewVisibility(R.id.button_media_pause, View.VISIBLE);
                expanded.setViewVisibility(R.id.button_media_play, View.GONE);
                expanded.setViewVisibility(R.id.button_media_pause, View.VISIBLE);
            } else {
                collapsed.setViewVisibility(R.id.button_media_play, View.VISIBLE);
                collapsed.setViewVisibility(R.id.button_media_pause, View.GONE);
                expanded.setViewVisibility(R.id.button_media_play, View.VISIBLE);
                expanded.setViewVisibility(R.id.button_media_pause, View.GONE);
            }
            if (foreground) {
                notificationManager.notify(1, notification);
            } else {
                service.startForeground(1, notification);
                foreground = true;
            }
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        collapsed.setImageViewBitmap(R.id.image_media_preview, image);
        expanded.setImageViewBitmap(R.id.image_media_preview, image);
        notificationManager.notify(1, notification);
    }

    private String buildTitle(final String main, final String second) {
        if (second == null) {
            return main;
        }
        return main + "-" + second;
    }


}
