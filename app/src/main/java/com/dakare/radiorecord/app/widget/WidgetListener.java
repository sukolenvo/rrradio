package com.dakare.radiorecord.app.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
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
    private final AppWidgetManager appWidgetManager;
    private final ComponentName componentName;

    public WidgetListener(final Service service) {
        this.service = service;
        componentName = new ComponentName(service, WidgetReceiver.class);
        appWidgetManager = AppWidgetManager.getInstance(service);
    }

    public static RemoteViews buildWidget(final Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent player = PendingIntent.getActivity(context, NotificationListener.CONTENT_CODE, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_container, player);
        Intent stopIntent = new Intent(context, PlayerService.class);
        stopIntent.setAction(NotificationListener.ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(context, NotificationListener.STOP_CODE, stopIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_stop, stopPending);
        Intent pauseIntent = new Intent(context, PlayerService.class);
        pauseIntent.setAction(NotificationListener.ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(context, NotificationListener.PAUSE_CODE, pauseIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        Intent resumeIntent = new Intent(context, PlayerService.class);
        resumeIntent.setAction(NotificationListener.ACTION_RESUME);
        PendingIntent resumePending = PendingIntent.getService(context, NotificationListener.RESUME_CODE, resumeIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        Intent nextIntent = new Intent(context, PlayerService.class);
        nextIntent.setAction(NotificationListener.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(context, NotificationListener.NEXT_CODE, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        Intent previousIntent = new Intent(context, PlayerService.class);
        previousIntent.setAction(NotificationListener.ACTION_PREVIOUS);
        PendingIntent previousPending = PendingIntent.getService(context, NotificationListener.PREVIOUS_CODE, previousIntent, 0);
        views.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        return views;
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        RemoteViews views = buildWidget(service);
        if (message.getState() == PlayerState.STOP) {
            views.setViewVisibility(R.id.button_media_play, View.VISIBLE);
            views.setViewVisibility(R.id.button_media_pause, View.GONE);
        } else {
            PlaylistItem playlistItem = message.getPlaying();
            views.setTextViewText(R.id.text_media_title, message.getSong() == null
                    ? buildTitle(playlistItem.getTitle(), playlistItem.getSubtitle()) : buildTitle(message.getArtist(), message.getSong()));
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
        appWidgetManager.updateAppWidget(componentName, views);
    }

    private String buildTitle(final String main, final String second) {
        if (second == null) {
            return main;
        }
        return main + "-" + second;
    }

    @Override
    public void onIconChange(final Bitmap image) {
        RemoteViews views = new RemoteViews(service.getPackageName(), R.layout.widget);
        views.setImageViewBitmap(R.id.image_media_preview, image);
        appWidgetManager.partiallyUpdateAppWidget(appWidgetManager.getAppWidgetIds(componentName), views);
    }
}
