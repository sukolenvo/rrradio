package com.dakare.radiorecord.app.player.listener.remote;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NotificationRemoteJelly extends NotificationRemoteImpl {

    private final RemoteViews expanded;

    public NotificationRemoteJelly(final String packageName, final Notification notification) {
        super(packageName, notification);
        expanded = new RemoteViews(packageName, R.layout.notification_expanded);
        notification.bigContentView = expanded;
    }

    @Override
    public void updateTitle(final PlaybackStatePlayerMessage message) {
        super.updateTitle(message);
        expanded.setTextViewText(R.id.text_media_title, message.getSong() == null
                                                        ? buildTitle(message.getPlaying().getTitle(),
                                                                     message.getPlaying().getSubtitle())
                                                        : buildTitle(message.getArtist(), message.getSong()));
    }

    @Override
    public void setImage(final Bitmap image) {
        super.setImage(image);
        expanded.setImageViewBitmap(R.id.image_media_preview, image);
    }

    @Override
    public void setImage(final int resId) {
        super.setImage(resId);
        expanded.setImageViewResource(R.id.image_media_preview, resId);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setPlaying(final boolean playing) {
        super.setPlaying(playing);
        if (playing) {
            expanded.setViewVisibility(R.id.button_media_play, View.GONE);
            expanded.setViewVisibility(R.id.button_media_pause, View.VISIBLE);
        } else {
            expanded.setViewVisibility(R.id.button_media_play, View.VISIBLE);
            expanded.setViewVisibility(R.id.button_media_pause, View.GONE);
        }
    }

    @Override
    protected void setPreviousIntent(final PendingIntent previousPending) {
        super.setPreviousIntent(previousPending);
        expanded.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
    }

    @Override
    protected void setNextIntent(final PendingIntent nextPending) {
        super.setNextIntent(nextPending);
        expanded.setOnClickPendingIntent(R.id.button_media_next, nextPending);
    }

    @Override
    protected void setPlayIntent(final PendingIntent resumePending) {
        super.setPlayIntent(resumePending);
        expanded.setOnClickPendingIntent(R.id.button_media_play, resumePending);
    }

    @Override
    protected void setPauseIntent(final PendingIntent pausePending) {
        super.setPauseIntent(pausePending);
        expanded.setOnClickPendingIntent(R.id.button_media_pause, pausePending);

    }

    @Override
    protected void setStopIntent(final PendingIntent stopPending) {
        super.setStopIntent(stopPending);
        expanded.setOnClickPendingIntent(R.id.button_media_close, stopPending);
    }
}
