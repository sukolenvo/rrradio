package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class NotificationListener extends AbstractPlayerStateListener implements ImageLoadingListener
{
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_PREVIOUS = "precious";
    public static final String ACTION_NEXT = "next";

    private final Service service;
    private String lastUrl;
    private final RemoteViews collapsed;
    private final RemoteViews expanded;
    private final Notification notification;
    private final NotificationManager notificationManager;

    public NotificationListener(final Service service)
    {
        this.service = service;
        Intent intent = new Intent(service, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notification = new Notification.Builder(service)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(service, 0, intent, 0))
                .setOngoing(true).getNotification();
        collapsed = new RemoteViews(service.getPackageName(), R.layout.notification_collapsed);
        notification.contentView = collapsed;
        expanded = new RemoteViews(service.getPackageName(), R.layout.notification_expanded);
        if (Build.VERSION.SDK_INT >= 16)
        {
            notification.bigContentView = expanded;
        }
        Intent stopIntent = new Intent(service, PlayerService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(service, 0, stopIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_pause, stopPending);
        collapsed.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        expanded.setOnClickPendingIntent(R.id.button_media_pause, stopPending);
        expanded.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        Intent nextIntent = new Intent(service, PlayerService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(service, 0, nextIntent, 0);
        collapsed.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        expanded.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        Intent previousIntent = new Intent(service, PlayerService.class);
        previousIntent.setAction(ACTION_NEXT);
        PendingIntent previousPending = PendingIntent.getService(service, 0, previousIntent, 0);
        expanded.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPlaybackChange(final PlaybackStatePlayerMessage message)
    {
        if (message.isPlaying())
        {
            PlaylistItem playlistItem = message.getItems().get(message.getPosition());
            collapsed.setTextViewText(R.id.text_media_title, message.getSong() == null ? playlistItem.getStation().getName() : message.getSong());
            expanded.setTextViewText(R.id.text_media_title, message.getSong() == null ? playlistItem.getStation().getName() : (message.getArtist() + "-" + message.getSong()));
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled())
            {
                lastUrl = null;
                collapsed.setImageViewResource(R.id.image_media_preview, playlistItem.getStation().getIcon());
                expanded.setImageViewResource(R.id.image_media_preview, playlistItem.getStation().getIcon());
            } else
            {
                lastUrl = message.getIcon();
                ImageLoader.getInstance().loadImage(message.getIcon(), new ImageSize(128, 128), this);
            }
            service.startForeground(1, notification);
        } else
        {
            lastUrl = null;
            service.stopForeground(true);
        }
    }

    @Override
    public void onLoadingStarted(final String imageUri, final View view)
    {
        //Nothing to do
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason)
    {
        //Nothing to do
    }

    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage)
    {
        if (lastUrl != null && lastUrl.equals(imageUri))
        {
            collapsed.setImageViewBitmap(R.id.image_media_preview, loadedImage);
            expanded.setImageViewBitmap(R.id.image_media_preview, loadedImage);
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view)
    {
        //Nothing to do
    }
}
