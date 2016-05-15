package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;


public class NotificationListener extends AbstractPlayerStateListener
{
    private final Service service;

    public NotificationListener(final Service service)
    {
        this.service = service;
    }

    @Override
    protected void onPlaybackChange(final PlaybackStatePlayerMessage message)
    {
        if (message.isPlaying())
        {
            Intent intent = new Intent(service, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PlaylistItem playlistItem = message.getItems().get(message.getPosition());
            Notification notification = new Notification.Builder(service)
                    .setSmallIcon(playlistItem.getStation().getIcon())
                    .setContentTitle(playlistItem.getTitle())
                    .setContentText(playlistItem.getSubtitle())
                    .setContentIntent(PendingIntent.getActivity(service, 0, intent, 0))
                    .setOngoing(true).getNotification();
            service.startForeground(1, notification);
        } else
        {
            service.stopForeground(true);
        }
    }
}
