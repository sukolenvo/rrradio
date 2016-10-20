package com.dakare.radiorecord.app.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.listener.IPlayerStateListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class WidgetListener implements IPlayerStateListener {

    private final Service service;
    private final RemoteViews views;
    private final AppWidgetManager appWidgetManager;

    public WidgetListener(final Service service) {
        this.service = service;
        views = new RemoteViews(service.getPackageName(), R.layout.widget);
        Intent intent = new Intent(service, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent player = PendingIntent.getActivity(service, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_container, player);
        Intent stopIntent = new Intent(service, PlayerService.class);
        stopIntent.setAction(NotificationListener.ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(service, 0, stopIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_stop, stopPending);
        Intent pauseIntent = new Intent(service, PlayerService.class);
        pauseIntent.setAction(NotificationListener.ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(service, 0, pauseIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        Intent resumeIntent = new Intent(service, PlayerService.class);
        resumeIntent.setAction(NotificationListener.ACTION_RESUME);
        PendingIntent resumePending = PendingIntent.getService(service, 0, resumeIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        Intent nextIntent = new Intent(service, PlayerService.class);
        nextIntent.setAction(NotificationListener.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(service, 0, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        Intent previousIntent = new Intent(service, PlayerService.class);
        previousIntent.setAction(NotificationListener.ACTION_PREVIOUS);
        PendingIntent previousPending = PendingIntent.getService(service, 0, previousIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        appWidgetManager = AppWidgetManager.getInstance(service);
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.STOP) {
            views.setViewVisibility(R.id.button_media_play, View.VISIBLE);
            views.setViewVisibility(R.id.button_media_pause, View.GONE);
        } else {
            PlaylistItem playlistItem = message.getPlaying();
            views.setTextViewText(R.id.text_media_title, message.getSong() == null ? buildTitle(playlistItem.getTitle(), playlistItem.getSubtitle()) : buildTitle(message.getArtist(), message.getSong()));
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled()) {
                views.setImageViewResource(R.id.image_media_preview, playlistItem.getStation().getIcon());
            }
            if (message.getState() == PlayerState.PLAY) {
                views.setViewVisibility(R.id.button_media_play, View.GONE);
                views.setViewVisibility(R.id.button_media_pause, View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.button_media_play, View.VISIBLE);
                views.setViewVisibility(R.id.button_media_pause, View.GONE);
            }
        }
        appWidgetManager.updateAppWidget(new ComponentName(service, WidgetReceiver.class), views);
    }

    private String buildTitle(final String main, final String second) {
        if (second == null) {
            return main;
        }
        return main + "-" + second;
    }

    @Override
    public void onIconChange(final Bitmap image) {
        views.setImageViewBitmap(R.id.image_media_preview, image);
        appWidgetManager.updateAppWidget(new ComponentName(service, WidgetReceiver.class), views);
    }
}
