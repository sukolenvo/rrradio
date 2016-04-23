package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import com.dakare.radiorecord.app.player.PlayerActivity;
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
            Notification notification = new Notification.Builder(service)
                    .setSmallIcon(message.getStation().getIcon())
                    .setContentTitle(message.getStation().getName())
                    .setContentIntent(PendingIntent.getActivity(service, 0, new Intent(service, PlayerActivity.class), 0))
                    .setOngoing(true).getNotification();
            service.startForeground(1, notification);
        } else
        {
            service.stopForeground(true);
        }
    }
}
