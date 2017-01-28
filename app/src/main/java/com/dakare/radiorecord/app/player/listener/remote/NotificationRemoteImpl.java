package com.dakare.radiorecord.app.player.listener.remote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import static com.dakare.radiorecord.app.player.listener.NotificationListener.*;

public class NotificationRemoteImpl implements NotificationRemote {

    private final RemoteViews collapsed;
    private Notification notification;

    public NotificationRemoteImpl(final String packageName, final Notification notification) {
        this.notification = notification;
        collapsed = new RemoteViews(packageName, R.layout.notification_collapsed);
        notification.contentView = collapsed;
    }

    protected void setPreviousIntent(final PendingIntent previousPending) {
        collapsed.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
    }

    protected void setNextIntent(final PendingIntent nextPending) {
        collapsed.setOnClickPendingIntent(R.id.button_media_next, nextPending);
    }

    protected void setPlayIntent(final PendingIntent resumePending) {
        collapsed.setOnClickPendingIntent(R.id.button_media_play, resumePending);
    }

    protected void setPauseIntent(final PendingIntent pausePending) {
        collapsed.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
    }

    protected void setStopIntent(final PendingIntent stopPending) {
        collapsed.setOnClickPendingIntent(R.id.button_media_close, stopPending);
    }

    @Override
    public void setImage(final Bitmap image) {
        collapsed.setImageViewBitmap(R.id.image_media_preview, image);
    }

    @Override
    public void setImage(final int resId) {
        collapsed.setImageViewResource(R.id.image_media_preview, resId);
    }

    @Override
    public void notify(final NotificationManager notificationManager, final int id) {
        try {
            notificationManager.notify(id, notification);
        } catch (RuntimeException e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void updateTitle(final PlaybackStatePlayerMessage message) {
        collapsed.setTextViewText(R.id.text_media_title, message.getSong() == null
                                                         ? buildTitle(message.getPlaying().getTitle(), message.getPlaying().getSubtitle())
                                                         : message.getSong());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setPlaying(final boolean playing) {
        if (playing) {
            collapsed.setViewVisibility(R.id.button_media_play, View.GONE);
            collapsed.setViewVisibility(R.id.button_media_pause, View.VISIBLE);
        } else {
            collapsed.setViewVisibility(R.id.button_media_play, View.VISIBLE);
            collapsed.setViewVisibility(R.id.button_media_pause, View.GONE);
        }
    }

    @Override
    public void setupIntents(final Service service) {
        Intent stopIntent = new Intent(service, PlayerService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(service, STOP_CODE, stopIntent, 0);
        setStopIntent(stopPending);
        Intent pauseIntent = new Intent(service, PlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(service, PAUSE_CODE, pauseIntent, 0);
        setPauseIntent(pausePending);
        Intent resumeIntent = new Intent(service, PlayerService.class);
        resumeIntent.setAction(ACTION_RESUME);
        PendingIntent resumePending = PendingIntent.getService(service, RESUME_CODE, resumeIntent, 0);
        setPlayIntent(resumePending);
        Intent nextIntent = new Intent(service, PlayerService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(service, NEXT_CODE, nextIntent, 0);
        setNextIntent(nextPending);
        Intent previousIntent = new Intent(service, PlayerService.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent previousPending = PendingIntent.getService(service, PREVIOUS_CODE, previousIntent, 0);
        setPreviousIntent(previousPending);
    }

    protected String buildTitle(final String main, final String second) {
        if (second == null) {
            return main;
        }
        return main + "-" + second;
    }
}
