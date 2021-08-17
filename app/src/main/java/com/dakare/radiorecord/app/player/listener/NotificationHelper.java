package com.dakare.radiorecord.app.player.listener;

import android.app.NotificationChannel;
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
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.view.theme.Theme;
import lombok.Getter;

import static com.dakare.radiorecord.app.player.listener.NotificationListener.*;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "radio_record_audio";

    @Getter
    private final NotificationCompat.Builder builder;

    private PendingIntent stopPending;
    private PendingIntent pausePending;
    private PendingIntent resumePending;
    private PendingIntent nextPending;
    private PendingIntent previousPending;

    public NotificationHelper(final Service service) {
        stopPending = PendingIntent.getService(service, STOP_CODE,
                new Intent(service, PlayerService.class).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);
        pausePending = PendingIntent.getService(service, PAUSE_CODE,
                new Intent(service, PlayerService.class).setAction(ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
        resumePending = PendingIntent.getService(service, RESUME_CODE,
                new Intent(service, PlayerService.class).setAction(ACTION_RESUME), PendingIntent.FLAG_UPDATE_CURRENT);
        nextPending = PendingIntent.getService(service, NEXT_CODE,
                new Intent(service, PlayerService.class).setAction(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
        previousPending = PendingIntent.getService(service, PREVIOUS_CODE,
                new Intent(service, PlayerService.class).setAction(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
        createChannelId(service);
        Intent intent = new Intent(RecordApplication.getInstance(), PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Theme theme = PreferenceManager.getInstance(service).getTheme();
        this.builder = new NotificationCompat.Builder(service, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(service, NotificationListener.CONTENT_CODE, intent, 0))
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(createCollapsedView(service, theme))
                .setCustomBigContentView(createBigContentView(service, theme));
    }

    private RemoteViews createCollapsedView(Service service, Theme theme) {
        RemoteViews collapsed = new RemoteViews(service.getPackageName(),
                theme == Theme.DARK ? R.layout.notification_collapsed_dark : R.layout.notification_collapsed);
        collapsed.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        collapsed.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        collapsed.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        collapsed.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        collapsed.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        return collapsed;
    }

    private RemoteViews createBigContentView(Service service, Theme theme) {
        RemoteViews expanded = new RemoteViews(service.getPackageName(),
                theme == Theme.DARK ? R.layout.notification_expanded_dark : R.layout.notification_expanded);
        expanded.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
        expanded.setOnClickPendingIntent(R.id.button_media_next, nextPending);
        expanded.setOnClickPendingIntent(R.id.button_media_play, resumePending);
        expanded.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
        expanded.setOnClickPendingIntent(R.id.button_media_close, stopPending);
        return expanded;
    }

    private void createChannelId(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_title_audio),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setImage(Bitmap image) {
        builder.getContentView().setImageViewBitmap(R.id.image_media_preview, image);
        builder.getBigContentView().setImageViewBitmap(R.id.image_media_preview, image);
    }

    public void setImage(int resId) {
        builder.getContentView().setImageViewResource(R.id.image_media_preview, resId);
        builder.getBigContentView().setImageViewResource(R.id.image_media_preview, resId);
    }

    public void updateTitle(PlaybackStatePlayerMessage message) {
        String title = message.getSong() == null
                ? buildTitle(message.getPlaying().getTitle(), message.getPlaying().getSubtitle())
                : message.getSong();
        builder.getContentView().setTextViewText(R.id.text_media_title, title);
        builder.getBigContentView().setTextViewText(R.id.text_media_title, title);
    }

    private String buildTitle(final String main, final String second) {
        if (second == null) {
            return main;
        }
        return main + "-" + second;
    }

    public void setPlaying(boolean playing) {
        if (playing) {
            builder.getBigContentView().setViewVisibility(R.id.button_media_play, View.GONE);
            builder.getBigContentView().setViewVisibility(R.id.button_media_pause, View.VISIBLE);
            builder.getContentView().setViewVisibility(R.id.button_media_play, View.GONE);
            builder.getContentView().setViewVisibility(R.id.button_media_pause, View.VISIBLE);
        } else {
            builder.getBigContentView().setViewVisibility(R.id.button_media_play, View.VISIBLE);
            builder.getBigContentView().setViewVisibility(R.id.button_media_pause, View.GONE);
            builder.getContentView().setViewVisibility(R.id.button_media_play, View.VISIBLE);
            builder.getContentView().setViewVisibility(R.id.button_media_pause, View.GONE);
        }
    }

}
