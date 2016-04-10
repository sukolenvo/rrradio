package com.dakare.radiorecord.app.player.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Player implements MediaPlayer.OnPreparedListener
{
    private static final String PREFIX = "http://air2.radiorecord.ru:805/";

    private MediaPlayer mediaPlayer;
    @Getter
    private final Context context;
    @Getter
    private Station station;
    @Setter
    private PlayerServiceMessageHandler playerServiceMessageHandler;

    public Player(final Context context)
    {
        this.context = context;
    }

    public void play(final Station station, final String quality)
    {
        Toast.makeText(context, R.string.connecting, Toast.LENGTH_LONG).show();
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(station.getIcon())
                .setContentTitle(station.getName())
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, PlayerActivity.class), 0))
                .setOngoing(true).getNotification();
        ((Service) context).startForeground(1, notification);
        synchronized (Player.class)
        {
            this.station = station;
            try
            {
                if (mediaPlayer != null)
                {
                    mediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(PREFIX + station.getCode() + "_" + quality);
                mediaPlayer.prepareAsync();
            } catch (IOException e)
            {
                Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
                this.station = null;
            }
        }
        playerServiceMessageHandler.handleServiceResponse(new PlaybackStatePlayerMessage(station, station != null));
    }

    public void stop()
    {
        context.stopService(new Intent(context, PlayerService.class));
        ((Service) context).stopForeground(true);
        synchronized (Player.class)
        {
            station = null;
            if (mediaPlayer != null)
            {
                mediaPlayer.stop();
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        synchronized (Player.class)
        {
            if (mediaPlayer == mp)
            {
                mediaPlayer.start();
            }
        }
    }
}
